/*
 * Copyright (C) 2016-2017 Daniel Saukel
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
package io.github.dre2n.factionsxl.util;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.faction.Relation;
import io.github.dre2n.factionsxl.player.FPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Partially adapted from FactionsOne by Sataniel.
 *
 * @author Daniel Saukel
 */
public enum ParsingUtil {

    FACTION_ADMIN("%faction_admin%"),
    FACTION_MEMBER_LIST("%faction_member_list%"),
    FACTION_MOD_LIST("%faction_mod_list%"),
    FACTION_TAG("%faction_tag%"),
    FACTION_PLAYER_COUNT("%faction_player_count%"),
    FACTION_PLAYER_LIST("%faction_player_list%"),
    PLAYER_NAME("%player_name%"),
    PLAYER_PREFIX("%player_prefix%"),
    PLAYER_TITLE("%player_title%"),
    RELATION("%relation%"),
    RELATION_COLOR("%relation_color%");

    private String placeholder;

    ParsingUtil(String placeholder) {
        this.placeholder = placeholder;
    }

    /* Getters and setters */
    /**
     * @return the placeholder
     */
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public String toString() {
        return placeholder;
    }

    /* Statics */
    /**
     * Replace the placeholders that are relevant for the chat in a String automatically.
     *
     * @param string
     * the String that contains the placeholders
     * @param faction
     * the faction the replacements are taken from
     */
    public static String replaceChatPlaceholders(String string, FPlayer sender, FPlayer receiver) {
        Faction faction = sender.getFaction();
        Relation relation = faction.getRelation(receiver);
        string = string.replaceAll(PLAYER_NAME.getPlaceholder(), sender.getName());
        string = string.replaceAll(PLAYER_PREFIX.getPlaceholder(), sender.getPrefix());
        string = string.replaceAll(PLAYER_TITLE.getPlaceholder(), sender.getTitle());
        string = string.replaceAll(FACTION_TAG.getPlaceholder(), faction.getName());
        string = string.replaceAll(RELATION.getPlaceholder(), relation.getName());
        string = string.replaceAll(RELATION_COLOR.getPlaceholder(), relation.getColor().toString());
        return string;
    }

    /**
     * Replace the faction placeholders in a String automatically.
     *
     * @param string
     * the String that contains the placeholders
     * @param faction
     * the faction the replacements are taken from
     */
    public static String replaceFactionPlaceholders(String string, Faction faction) {
        if (faction.getAdmin() != null) {
            string = string.replaceAll(FACTION_ADMIN.getPlaceholder(), faction.getAdmin().getName());
        }
        string = string.replaceAll(FACTION_MEMBER_LIST.getPlaceholder(), namesToString(faction.getNonPrivilegedFPlayers()));
        string = string.replaceAll(FACTION_MOD_LIST.getPlaceholder(), namesToString(faction.getFMods()));
        string = string.replaceAll(FACTION_TAG.getPlaceholder(), faction.getName());
        string = string.replaceAll(FACTION_PLAYER_COUNT.getPlaceholder(), String.valueOf(faction.getFPlayers().size()));
        string = string.replaceAll(FACTION_PLAYER_LIST.getPlaceholder(), namesToString(faction.getFPlayers()));

        return string;
    }

    /**
     * Replace the relation and player placeholders in a String automatically.
     *
     * @param string
     * the String that contains the placeholders
     * @param standpoint
     * the standpoint of this Faction will be chosen for the relation purposes
     * @param object
     * the FPlayer to compare to the standpoint faction
     */
    public static String replaceRelationPlaceholders(String string, Faction standpoint, FPlayer object) {
        string = string.replaceAll(RELATION.getPlaceholder(), standpoint.getRelation(object).getColor().toString());
        string = string.replaceAll(RELATION_COLOR.getPlaceholder(), standpoint.getRelation(object).getColor().toString());
        if (object.hasFaction()) {
            string = string.replaceAll(PLAYER_PREFIX.getPlaceholder(), object.getPrefix());
            string = string.replaceAll(PLAYER_TITLE.getPlaceholder(), object.getTitle());
        }
        return string;
    }

    public static List<String> namesToList(Collection<FPlayer> fPlayers) {
        List<String> names = new ArrayList<>();
        for (FPlayer fPlayer : fPlayers) {
            names.add(fPlayer.getName());
        }
        return names;
    }

    public static String namesToString(Collection<FPlayer> fPlayers) {
        return namesToString(fPlayers, ChatColor.WHITE);
    }

    public static String namesToString(Collection<FPlayer> fPlayers, ChatColor comma) {
        String names = new String();
        boolean first = true;
        for (FPlayer fPlayer : fPlayers) {
            if (!first) {
                names += comma + ", ";
            } else {
                first = false;
            }
            names += fPlayer.getName();
        }
        return names;
    }

    public static String getPlayerName(Player subject, Player object) {
        FactionsXL plugin = FactionsXL.getInstance();
        Faction subjectFaction = plugin.getFactionCache().getByMember(subject);
        Faction objectFaction = plugin.getFactionCache().getByMember(object);
        ChatColor color = subjectFaction != null ? subjectFaction.getRelation(objectFaction).getColor() : ChatColor.WHITE;
        return color + object.getName();
    }

    public static String getFactionName(Player subject, Faction object) {
        Faction subjectFaction = FactionsXL.getInstance().getFactionCache().getByMember(subject);
        ChatColor color = subjectFaction != null ? subjectFaction.getRelation(object).getColor() : ChatColor.WHITE;
        String objectName = object != null ? object.getName() : new String();
        return color + objectName;
    }

    public static String getRegionName(Player subject, Region object) {
        if (object == null) {
            return FMessage.MISC_WILDERNESS.getMessage();
        }
        Faction subjectFaction = FactionsXL.getInstance().getFactionCache().getByMember(subject);
        Faction objectFaction = object.getOwner();
        ChatColor color = subjectFaction != null ? subjectFaction.getRelation(objectFaction).getColor() : ChatColor.WHITE;
        String name = objectFaction != null ? color + "[" + objectFaction.getName() + "] " : new String();
        return name + object.getName();
    }

    /**
     * Handles CommandSenders, Players, FPlayers, FactionCache and Regions as arguments
     */
    public static void broadcastMessage(String message, Object... args) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMessage(player, message, args);
        }
        sendMessage(Bukkit.getConsoleSender(), message, args);
    }

    /**
     * Handles CommandSenders, Players, FPlayers, FactionCache and Regions as arguments
     */
    public static void sendMessage(CommandSender sender, String message, Object... args) {
        FactionCache factions = FactionsXL.getInstance().getFactionCache();
        Faction subjectFaction = null;
        if (sender instanceof Player) {
            subjectFaction = factions.getByMember((Player) sender);
        }
        String messageParsed = message;
        int i = 0;
        for (Object arg : args) {
            i++;
            if (arg != null) {
                if (arg instanceof String) {
                    messageParsed = messageParsed.replace("&v" + i, (String) arg);
                } else if (arg instanceof CommandSender) {
                    Faction objectFaction = null;
                    if (arg instanceof Player) {
                        objectFaction = factions.getByMember((Player) arg);
                    }
                    ChatColor color = subjectFaction == null ? ChatColor.WHITE : subjectFaction.getRelation(objectFaction).getColor();
                    messageParsed = messageParsed.replace("&v" + i, color + ((CommandSender) arg).getName());
                } else if (arg instanceof FPlayer) {
                    Faction objectFaction = factions.getByMember(((FPlayer) arg).getPlayer());
                    ChatColor color = subjectFaction == null ? ChatColor.WHITE : subjectFaction.getRelation(objectFaction).getColor();
                    messageParsed = messageParsed.replace("&v" + i, color + ((FPlayer) arg).getName());
                } else if (arg instanceof Faction) {
                    Faction objectFaction = (Faction) arg;
                    ChatColor color = subjectFaction == null ? ChatColor.WHITE : subjectFaction.getRelation(objectFaction).getColor();
                    messageParsed = messageParsed.replace("&v" + i, color + objectFaction.getName());
                } else if (arg instanceof Region) {
                    Faction objectFaction = ((Region) arg).getOwner();
                    ChatColor color = subjectFaction == null ? ChatColor.WHITE : subjectFaction.getRelation(objectFaction).getColor();
                    messageParsed = messageParsed.replace("&v" + i, color + ((Region) arg).getName());
                } else {
                    messageParsed = messageParsed.replace("&v" + i, arg.toString());
                }

            } else {
                messageParsed = messageParsed.replace("&v" + i, "null");
            }
        }
        MessageUtil.sendMessage(sender, messageParsed);
    }

}
