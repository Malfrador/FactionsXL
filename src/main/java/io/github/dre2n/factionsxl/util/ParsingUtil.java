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

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.relation.RelationParticipator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Partially adapted from FactionsOne by Sataniel.
 *
 * @author Daniel Saukel
 */
public enum ParsingUtil {

    FACTION_ADMIN("%faction_admin%"),
    FACTION_BALANCE("%faction_balance%"),
    FACTION_CAPITAL("%faction_capital%"),
    FACTION_DESCRIPTION("%faction_description%"),
    FACTION_GOVERNMENT_TYPE("%faction_gov_type%"),
    FACTION_LONG_TAG("%faction_long_tag%"),
    FACTION_MEMBER_LIST("%faction_member_list%"),
    FACTION_MOD_LIST("%faction_mod_list%"),
    FACTION_ONLINE_COUNT("%faction_online%"),
    FACTION_POWER("%faction_power%"),
    FACTION_PLAYER_COUNT("%faction_player_count%"),
    FACTION_PLAYER_LIST("%faction_player_list%"),
    FACTION_PROVINCE_COUNT("%faction_province_count%"),
    FACTION_SHORT_TAG("%faction_short_tag%"),
    FACTION_STABILITY("%faction_stability%"),
    FACTION_TAG("%faction_tag%"),
    FEDERATION_TAG("%federation_tag%"),
    PLAYER_BALANCE("%player_balance%"),
    PLAYER_DYNASTY("%player_dynasty%"),
    PLAYER_NAME("%player_name%"),
    PLAYER_POWER("%player_power%"),
    PLAYER_PREFIX("%player_prefix%"),
    PLAYER_TITLE("%player_title%"),
    REGION_CORE_LIST("%region_core_list%"),
    REGION_CLAIM_LIST("%region_claim_list%"),
    REGION_LEVEL("%region_level%"),
    REGION_NAME("%region_name%"),
    REGION_OWNER("%region_owner%"),
    REGION_POPULATION("%region_population%"),
    REGION_SIZE("%region_size%"),
    REGION_TYPE("%region_type%"),
    RELATION("%relation%"),
    RELATION_COLOR("%relation_color%"),
    // External
    PERM_PREFIX("%perm_prefix%"),
    PERM_SUFFIX("%perm_suffix%");

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
        string = string.replace(PLAYER_NAME.getPlaceholder(), sender.getName());
        string = string.replace(PLAYER_PREFIX.getPlaceholder(), sender.getPrefix());
        string = string.replace(PLAYER_TITLE.getPlaceholder(), sender.getTitle());
        Relation relation = null;
        if (faction != null) {
            relation = faction.getRelation(receiver);
            string = string.replace(FACTION_LONG_TAG.getPlaceholder(), faction.getLongName());
            string = string.replace(FACTION_TAG.getPlaceholder(), faction.getName());
            string = string.replace(FACTION_SHORT_TAG.getPlaceholder(), faction.getShortName());
        } else {
            relation = Relation.PEACE;
            string = string.replace(FACTION_LONG_TAG.getPlaceholder(), FMessage.MISC_LONER.getMessage());
            string = string.replace(FACTION_TAG.getPlaceholder(), FMessage.MISC_LONER.getMessage());
            string = string.replace(FACTION_SHORT_TAG.getPlaceholder(), FMessage.MISC_LONER.getMessage());
        }
        string = string.replace(RELATION.getPlaceholder(), relation.getName());
        string = string.replace(RELATION_COLOR.getPlaceholder(), relation.getColor().toString());
        // External
        try {
            RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
            if (chatProvider != null) {
                Chat chat = chatProvider.getProvider();
                if (chat != null) {
                    string = string.replace(PERM_PREFIX.getPlaceholder(), chat.getPlayerPrefix(sender.getPlayer()));
                    string = string.replace(PERM_SUFFIX.getPlaceholder(), chat.getPlayerSuffix(sender.getPlayer()));
                }
            }
        } catch (NoClassDefFoundError error) {
        }

        return ChatColor.translateAlternateColorCodes('&', string);
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
        if (string.contains(FACTION_ADMIN.getPlaceholder()) && faction.getAdmin() != null) {
            string = string.replace(FACTION_ADMIN.getPlaceholder(), faction.getAdmin().getName());
        }
        if (string.contains(FACTION_BALANCE.getPlaceholder()) && FactionsXL.getInstance().getFConfig().isEconomyEnabled()) {
            string = string.replace(FACTION_BALANCE.getPlaceholder(), faction.getAccount().getFormatted());
        }
        string = string.replace(FACTION_CAPITAL.getPlaceholder(), faction.getCapital().getName());
        string = string.replace(FACTION_DESCRIPTION.getPlaceholder(), faction.getDescription());
        string = string.replace(FACTION_GOVERNMENT_TYPE.getPlaceholder(), faction.getGovernmentType().getName());
        string = string.replace(FACTION_LONG_TAG.getPlaceholder(), faction.getLongName());
        if (string.contains(FACTION_MEMBER_LIST.getPlaceholder())) {
            string = string.replace(FACTION_MEMBER_LIST.getPlaceholder(), namesToString(faction.getNonPrivilegedMembers()));
        }
        if (string.contains(FACTION_MOD_LIST.getPlaceholder())) {
            string = string.replace(FACTION_MOD_LIST.getPlaceholder(), collectionToString(faction.getMods().getNames()));
        }
        string = string.replace(FACTION_ONLINE_COUNT.getPlaceholder(), String.valueOf(faction.getOnlineMembers().size()));
        string = string.replace(FACTION_PLAYER_COUNT.getPlaceholder(), String.valueOf(faction.getMembers().size()));
        if (string.contains(FACTION_PLAYER_LIST.getPlaceholder())) {
            string = string.replace(FACTION_PLAYER_LIST.getPlaceholder(), collectionToString(faction.getMembers().getNames()));
        }
        string = string.replace(FACTION_POWER.getPlaceholder(), String.valueOf(faction.getPower()));
        string = string.replace(FACTION_PROVINCE_COUNT.getPlaceholder(), String.valueOf(faction.getRegions().size()));
        string = string.replace(FACTION_SHORT_TAG.getPlaceholder(), faction.getShortName());
        string = string.replace(FACTION_STABILITY.getPlaceholder(), String.valueOf(faction.getStability()));
        string = string.replace(FACTION_TAG.getPlaceholder(), faction.getName());
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Replace the region placeholders in a String automatically.
     *
     * @param string
     * the String that contains the placeholders
     * @param region
     * the region the replacements are taken from
     */
    public static String replaceRegionPlaceholders(String string, Region region) {
        if (string.contains(REGION_CLAIM_LIST.getPlaceholder())) {
            string = string.replace(REGION_CLAIM_LIST.getPlaceholder(), factionsToString(region.getClaimFactions().keySet()));
        }
        if (string.contains(REGION_CORE_LIST.getPlaceholder())) {
            string = string.replace(REGION_CORE_LIST.getPlaceholder(), factionsToString(region.getCoreFactions().keySet()));
        }
        string = string.replace(REGION_LEVEL.getPlaceholder(), String.valueOf(region.getLevel()));
        string = string.replace(REGION_NAME.getPlaceholder(), region.getName());
        string = string.replace(REGION_OWNER.getPlaceholder(), region.getOwner() != null ? region.getOwner().getName() : FMessage.MISC_WILDERNESS.getMessage());
        string = string.replace(REGION_POPULATION.getPlaceholder(), String.valueOf(region.getPopulation()));
        string = string.replace(REGION_SIZE.getPlaceholder(), String.valueOf(region.getSize()));
        string = string.replace(REGION_TYPE.getPlaceholder(), region.getType().getName());
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Replace the relation placeholders in a String automatically.
     *
     * @param string
     * the String that contains the placeholders
     * @param standpoint
     * the standpoint of this Faction will be chosen for the relation purposes
     * @param object
     * the FPlayer to compare to the standpoint faction
     */
    public static String replaceRelationPlaceholders(String string, RelationParticipator standpoint, RelationParticipator object) {
        Relation relation = standpoint.getRelation(object);
        string = string.replace(RELATION.getPlaceholder(), relation.getName());
        string = string.replace(RELATION_COLOR.getPlaceholder(), relation.getColor().toString());
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Replace the player placeholders in a String automatically.
     *
     * @param string
     * the String that contains the placeholders
     * @param fPlayer
     * the scoreboard owner
     */
    public static String replacePlayerPlaceholders(String string, FPlayer fPlayer) {
        FactionsXL plugin = FactionsXL.getInstance();
        Economy econ = plugin.getEconomyProvider();

        if (string.contains(PLAYER_BALANCE.getPlaceholder()) && plugin.getFConfig().isEconomyEnabled()) {
            string = string.replace(PLAYER_BALANCE.getPlaceholder(), econ.format(econ.getBalance(fPlayer.getPlayer())));
        }
        string = string.replace(PLAYER_DYNASTY.getPlaceholder(), fPlayer.getDynasty() != null ? fPlayer.getDynasty().getName() : "None");
        string = string.replace(PLAYER_NAME.getPlaceholder(), fPlayer.getName());
        string = string.replace(PLAYER_POWER.getPlaceholder(), String.valueOf(fPlayer.getPower()));
        string = string.replace(PLAYER_PREFIX.getPlaceholder(), fPlayer.getPrefix());
        string = string.replace(PLAYER_TITLE.getPlaceholder(), fPlayer.getTitle() != null ? fPlayer.getTitle() : "None");

        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String factionsToString(Collection<Faction> factions) {
        return factionsToString(factions, ChatColor.WHITE);
    }

    public static String factionsToString(Collection<Faction> factions, ChatColor comma) {
        String names = new String();
        boolean first = true;
        for (Faction faction : factions) {
            if (!first) {
                names += comma + ", ";
            } else {
                first = false;
            }
            names += faction.getName();
        }
        return names;
    }

    public static List<String> namesToList(Collection<FPlayer> fPlayers) {
        List<String> names = new ArrayList<>();
        for (FPlayer fPlayer : fPlayers) {
            names.add(fPlayer.getName());
        }
        return names;
    }

    public static String fNamesToString(Collection<FPlayer> fPlayers) {
        return fNamesToString(fPlayers, null);
    }

    public static String fNamesToString(Collection<FPlayer> fPlayers, ChatColor comma) {
        String names = new String();
        boolean first = true;
        for (FPlayer fPlayer : fPlayers) {
            if (!first) {
                names += (comma != null ? comma : new String()) + ", ";
            } else {
                first = false;
            }
            names += fPlayer.getName();
        }
        return names;
    }

    public static String namesToString(Collection<OfflinePlayer> players) {
        return namesToString(players, null);
    }

    public static String namesToString(Collection<OfflinePlayer> players, ChatColor comma) {
        String names = new String();
        boolean first = true;
        for (OfflinePlayer player : players) {
            if (!first) {
                names += (comma != null ? comma : new String()) + ", ";
            } else {
                first = false;
            }
            names += player.getName();
        }
        return names;
    }

    public static String collectionToString(Collection<String> strings) {
        return collectionToString(strings, null);
    }

    public static String collectionToString(Collection<String> strings, ChatColor comma) {
        String names = new String();
        boolean first = true;
        for (String string : strings) {
            if (!first) {
                names += (comma != null ? comma : new String()) + ", ";
            } else {
                first = false;
            }
            names += string;
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
     * Handles CommandSenders, OfflinePlayers, Players, FPlayers, FactionCache and Regions as arguments
     */
    public static void sendMessage(CommandSender sender, String message, Object... args) {
        FactionsXL plugin = FactionsXL.getInstance();
        FactionCache factions = plugin.getFactionCache();
        Faction subjectFaction = null;
        if (sender instanceof Player) {
            subjectFaction = factions.getByMember((Player) sender);
        }
        String messageParsed = message;
        int i = 0;
        for (Object arg : args) {
            i++;
            if (arg != null) {
                if (arg instanceof OfflinePlayer && !(arg instanceof Player)) {
                    arg = plugin.getFPlayerCache().getByPlayer((OfflinePlayer) arg);
                }
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
                    Faction objectFaction = factions.getByFPlayer(((FPlayer) arg));
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
