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
package io.github.dre2n.factionsxl.command;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.GovernmentType;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.ArrayList;
import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ShowCommand extends FCommand {

    public ShowCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("show");
        setAliases("who", "f");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_SHOW.getMessage());
        setPermission(FPermission.SHOW.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Faction faction = null;
        if (args.length == 1 && sender instanceof Player) {
            faction = factions.getByMember((Player) sender);
        } else if (args.length == 2) {
            faction = factions.getByName(args[1]);
            if (faction == null) {
                faction = factions.getById(NumberUtil.parseInt(args[1], -1));
            }
            if (faction == null) {
                FPlayer fPlayer = fPlayers.getByName(args[1]);
                faction = fPlayer != null ? fPlayer.getFaction() : null;
            }
        }
        if (faction == null) {
            ParsingUtil.sendMessage(sender, args.length >= 2 ? FMessage.ERROR_NO_SUCH_FACTION.getMessage(args[1]) : FMessage.ERROR_SPECIFY_FACTION.getMessage());
            return;
        }
        showFaction((Player) sender, faction);
    }

    public void showFaction(Player player, Faction faction) {
        ChatColor c = faction.getRelation(factions.getByMember(player)).getColor();

        MessageUtil.sendCenteredMessage(player, c + "&l=== " + faction.getLongName() + (faction.isActive() ? new String() : " (\u271d)") + " ===");
        MessageUtil.sendCenteredMessage(player, "&6____________________________________________________");
        BaseComponent[] tag = TextComponent.fromLegacyText(FMessage.CMD_SHOW_TAG.getMessage() + c + faction.getShortName());
        HoverEvent tagHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("ID: " + faction.getId()).create());
        for (BaseComponent component : tag) {
            component.setHoverEvent(tagHover);
        }
        MessageUtil.sendMessage(player, tag);
        MessageUtil.sendMessage(player, FMessage.CMD_SHOW_DESCRIPTION.getMessage() + c + faction.getDescription());
        if (config.isEconomyEnabled()) {
            MessageUtil.sendMessage(player, FMessage.CMD_SHOW_BALANCE.getMessage() + c + econ.format(faction.getAccount().getBalance()));
        }
        String govType = faction.getGovernmentType().getName();
        if (faction.getGovernmentType() == GovernmentType.MONARCHY) {
            govType += " (" + (faction.getDynasty() != null ? faction.getDynasty().getName() : "&oInterregnum") + c + ")";
        }
        MessageUtil.sendMessage(player, FMessage.CMD_SHOW_GOVERNMENT_TYPE.getMessage() + c + govType);
        if (faction.isActive()) {
            MessageUtil.sendMessage(player, FMessage.CMD_SHOW_INVITATION.getMessage() + c + !faction.isOpen());
            MessageUtil.sendMessage(player, FMessage.CMD_SHOW_CAPITAL.getMessage() + c + faction.getCapital().getName());
            String power = String.valueOf(faction.getPower());
            String provinces = String.valueOf(faction.getRegions().size());
            MessageUtil.sendMessage(player, FMessage.CMD_SHOW_INFO.getMessage(c.toString(), power, provinces));
            MessageUtil.sendMessage(player, faction.getStabilityModifiers(c));

            ArrayList<BaseComponent> relList = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(FMessage.CMD_SHOW_RELATIONS.getMessage())));
            boolean relFirst = true;
            for (Faction other : factions.getActive()) {
                Relation relation = faction.getRelation(other);
                if (relation != Relation.PEACE && relation != Relation.OWN) {
                    if (!relFirst) {
                        relList.addAll(Arrays.asList(TextComponent.fromLegacyText(ChatColor.GOLD + ", ")));
                    }
                    relFirst = false;
                    relList.addAll(Arrays.asList(relation.getFormatted(other)));
                }
            }
            player.spigot().sendMessage(relList.toArray(new BaseComponent[]{}));

            String leader = faction.getAdmin() != null ? faction.getAdmin().getName() : "&oInterregnum";
            MessageUtil.sendMessage(player, FMessage.CMD_SHOW_LEADER.getMessage() + c + leader);

            ArrayList<BaseComponent> memList = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(FMessage.CMD_SHOW_MEMBERS.getMessage())));
            boolean memFirst = true;
            for (OfflinePlayer member : faction.getMembers().getOfflinePlayers()) {
                Double memPower = plugin.getFData().power.get(member.getUniqueId());
                if (memPower == null) {
                    memPower = 0D;
                }
                String memPowerHover = FMessage.CMD_POWER.getMessage(ChatColor.GOLD + member.getName(), String.valueOf(memPower.intValue()));
                HoverEvent onHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(memPowerHover));
                BaseComponent[] components = TextComponent.fromLegacyText(c + member.getName());
                for (BaseComponent component : components) {
                    component.setHoverEvent(onHover);
                }

                if (!memFirst) {
                    memList.addAll(Arrays.asList(TextComponent.fromLegacyText(ChatColor.GOLD + ", ")));
                }
                memFirst = false;
                memList.addAll(Arrays.asList(components));
            }
            player.spigot().sendMessage(memList.toArray(new BaseComponent[]{}));

        } else {
            MessageUtil.sendMessage(player, FMessage.CMD_SHOW_FORMER_LEADERS.getMessage()
                    + c + ParsingUtil.namesToString(faction.getFormerAdmins().getOfflinePlayers(), ChatColor.GOLD));
        }
    }

}
