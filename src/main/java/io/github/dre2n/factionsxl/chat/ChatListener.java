/*
 * Copyright (C) 2017 Daniel Saukel
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author Daniel Saukel
 */
public class ChatListener implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();
    FPlayerCache fPlayers = plugin.getFPlayerCache();
    FConfig fConfig = plugin.getFConfig();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = fPlayers.getByPlayer(player);
        if (!fPlayer.hasFaction() && fPlayer.getChatChannel() != ChatChannel.PUBLIC) {
            fPlayer.setChatChannel(ChatChannel.PUBLIC);
        }
        ChatChannel channel = fPlayer.getChatChannel();
        if (channel == ChatChannel.PUBLIC && !fConfig.isPublicChatHandled()) {
            return;
        }

        event.setCancelled(true);
        for (Relation relation : channel.getRelations()) {
            for (Player ally : fPlayer.getFaction().getOnlineByRelation(relation)) {
                String format = ParsingUtil.replaceChatPlaceholders(fConfig.getChatFormat(channel), fPlayer, fPlayers.getByPlayer(ally));
                MessageUtil.sendMessage(ally, format + event.getMessage());
            }
        }
    }

}
