/*
 * Copyright (c) 2017-2018 Daniel Saukel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.factionsxl.chat;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.player.FPlayerCache;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author Daniel Saukel
 */
public class ChatListener implements Listener {

    FactionsXL plugin;
    FPlayerCache fPlayers;
    FConfig config;

    public ChatListener(FactionsXL plugin) {
        this.plugin = plugin;
        fPlayers = plugin.getFPlayerCache();
        config = plugin.getFConfig();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = fPlayers.getByPlayer(player);
        if (!fPlayer.hasFaction() && fPlayer.getChatChannel() != ChatChannel.PUBLIC && fPlayer.getChatChannel() != ChatChannel.LOCAL) {
            fPlayer.setChatChannel(ChatChannel.PUBLIC);
        }
        ChatChannel channel = fPlayer.getChatChannel();
        if ((channel == ChatChannel.PUBLIC || channel == ChatChannel.LOCAL) && !config.isPublicChatHandled()) {
            return;
        }

        if (!event.isCancelled()) {
            event.setCancelled(true);
            if (channel == ChatChannel.PUBLIC) {
                for (Player receiver : Bukkit.getOnlinePlayers()) {
                    String format = ParsingUtil.replaceChatPlaceholders(config.getChatFormat(channel), fPlayer, fPlayers.getByPlayer(receiver));
                    MessageUtil.sendMessage(receiver, format + event.getMessage());
                }
            } else if (channel == ChatChannel.LOCAL) {
                for (Entity entity : player.getNearbyEntities(config.getLocalChatRange(), config.getLocalChatRange(), config.getLocalChatRange())) {
                    if (entity instanceof Player) {
                        Player receiver = (Player) entity;
                        String format = ParsingUtil.replaceChatPlaceholders(config.getChatFormat(channel), fPlayer, fPlayers.getByPlayer(receiver));
                        MessageUtil.sendMessage(receiver, format + event.getMessage());
                    }
                }
                String format = ParsingUtil.replaceChatPlaceholders(config.getChatFormat(channel), fPlayer, fPlayer);
                MessageUtil.sendMessage(player, format + event.getMessage());
            } else {
                for (Relation relation : channel.getRelations()) {
                    for (Player receiver : fPlayer.getFaction().getOnlineByRelationAndVassals(relation)) {
                        String format = ParsingUtil.replaceChatPlaceholders(config.getChatFormat(channel), fPlayer, fPlayers.getByPlayer(receiver));
                        MessageUtil.sendMessage(receiver, format + event.getMessage());
                    }
                }
            }
            MessageUtil.log("[FXL-Chat] [" + channel + "] " + player.getName() + ": " + event.getMessage());
        }
    }

}
