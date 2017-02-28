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
package io.github.dre2n.factionsxl.config;

import com.google.common.collect.ImmutableMap;
import io.github.dre2n.commons.config.BRConfig;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.dynmap.DynmapStyle;
import io.github.dre2n.factionsxl.chat.ChatChannel;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import io.github.dre2n.factionsxl.util.ProgressBar;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;

/**
 * Represents the main config.yml.
 * The scoreboard part is taken from FactionsCosmetics by Sataniel.
 *
 * @author Daniel Saukel
 */
public class FConfig extends BRConfig {

    public static final int CONFIG_VERSION = 0;

    public static final long SECOND = 20;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;
    public static final long WEEK = DAY * 7;
    public static final long MONTH = DAY * 30;

    public static final String SCOREBOARD_VALUE_PREFIX = "&7 ";

    private String language = "english";
    private double dayLength = 24;
    private int maxPower = 10;
    private int maxIdeaGroups = 2;
    private double defaultManpowerModifier = 5;
    private boolean economyEnabled = true;

    // Chat
    private String chatFormatAlly = "&d[%relation_color%%faction_tag%&d] %relation_color%%player_prefix%&d%player_name%: ";
    private String chatFormatCoalition = "&c[%relation_color%%faction_tag%&c] %relation_color%%player_prefix%&c%player_name%: ";
    private String chatFormatFaction = "%relation_color%%player_prefix%&a%player_title% %player_name%: ";

    // Protection
    private boolean lwcEnabled = true;
    private boolean wildernessProtected = true;

    // Holograms
    private boolean hologramsEnabled = true;
    private List<String> homeHologramText = new ArrayList<>(Arrays.asList(
            "&4&lH O M E",
            "&a" + ProgressBar.BAR,
            "&6&l=== " + ParsingUtil.FACTION_LONG_TAG + " &6&l===",
            "&7&o" + ParsingUtil.FACTION_CAPITAL,
            "&a" + ProgressBar.BAR
    ));

    // Nametag and scoreboard
    private String nametagPrefix = "%relation_color%%faction_tag% ";
    private double scoreboardExpiration = 7;
    private double scoreboardUpdateInterval = 1;
    private boolean defaultScoreboardPrefixes = true;
    private boolean scoreboardEnabledByDefault = true;
    private boolean scoreboardFactionlessEnabled = true;
    private String defaultScoreboardTitle = "&a" + ParsingUtil.PLAYER_NAME;
    private List<String> scoreboardDefault = new ArrayList<>(Arrays.asList(
            "&6Your Faction",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.FACTION_TAG,
            "&3Your Dynasty",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.PLAYER_DYNASTY,
            "&6Your Power",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.PLAYER_POWER,
            "&3Balance",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.PLAYER_BALANCE
    ));
    private List<String> scoreboardFactionInfo = new ArrayList<>(Arrays.asList(
            "&6Government Type",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.FACTION_GOVERNMENT_TYPE,
            "&3Federation",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.FEDERATION_TAG,
            "&6Leader",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.FACTION_ADMIN,
            "&3Members",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.FACTION_ONLINE_COUNT + "/" + ParsingUtil.FACTION_PLAYER_COUNT,
            "&6Capital",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.FACTION_CAPITAL,
            "&3Stability / Power / Provinces",
            SCOREBOARD_VALUE_PREFIX + ParsingUtil.FACTION_STABILITY + "/" + ParsingUtil.FACTION_POWER + "/" + ParsingUtil.FACTION_PROVINCE_COUNT
    ));
    private List<String> scoreboardFactionless = new ArrayList<>(Arrays.asList("Join a faction!"));

    // Dynmap
    private boolean dynmap = true;
    private int dynmapLayerPriority = 2;
    private int dynmapLayerMinimumZoom = 0;
    private String dynmapDescription
            = "<div class=\"infowindow\">\n"
            + "<span style=\"font-weight: bold; font-size: 150%;\">%name%</span><br>\n"
            + "<span style=\"font-style: italic; font-size: 110%;\">%description%</span><br>"
            + "<br>\n"
            + "<span style=\"font-weight: bold;\">Leader:</span> %players.leader%<br>\n"
            + "<span style=\"font-weight: bold;\">Admins:</span> %players.admins.count%<br>\n"
            + "<span style=\"font-weight: bold;\">Moderators:</span> %players.moderators.count%<br>\n"
            + "<span style=\"font-weight: bold;\">Members:</span> %players.normals.count%<br>\n"
            + "<span style=\"font-weight: bold;\">TOTAL:</span> %players.count%<br>\n"
            + "</br>\n"
            + "<span style=\"font-weight: bold;\">Bank:</span> %money%<br>\n"
            + "<br>\n"
            + "</div>";
    private boolean dynmapDescriptionMoney = false;
    private boolean dynmapVisibilityByFaction = true;
    private Set<String> dynmapVisibleFactions = new HashSet<>();
    private Set<String> dynmapHiddenFactions = new HashSet<>();
    private DynmapStyle dynmapDefaultStyle = new DynmapStyle()
            .setStrokeColor("#FFFFFF")
            .setLineOpacity(0.8)
            .setLineWeight(3)
            .setFillColor("#E0E0E0")
            .setFillOpacity(0.35)
            .setHomeMarker("greenflag")
            .setBoost(false);
    private Map<String, DynmapStyle> dynmapFactionStyles = ImmutableMap.of(
            "SafeZone", new DynmapStyle().setStrokeColor("#FF00FF").setFillColor("#FF00FF").setBoost(false),
            "WarZone", new DynmapStyle().setStrokeColor("#FF0000").setFillColor("#FF0000").setBoost(false)
    );
    private Map<String, DynmapStyle> dynmapRegionStyles = ImmutableMap.of(
            "PLACEHOLDER", new DynmapStyle().setStrokeColor("#FF00FF").setFillColor("#FF00FF").setBoost(false)
    );

    public FConfig(File file) {
        super(file, CONFIG_VERSION);

        if (initialize) {
            initialize();
        }
        load();
    }

    /* Getters and setters */
    /**
     * @return
     * the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @return
     * the time until taxes are collected
     */
    public long getDayLength() {
        return (long) (dayLength * HOUR);
    }

    /**
     * @return
     * the maximum power that a player may store
     */
    public int getMaxPower() {
        return maxPower;
    }

    /**
     * @return
     * the maximum amount of idea groups that a faction can have
     */
    public int getMaxIdeaGroups() {
        return maxIdeaGroups;
    }

    /**
     * @return
     * the default manpower modifier
     */
    public double getDefaultManpowerModifier() {
        return defaultManpowerModifier;
    }

    /**
     * @return
     * true if economy features are enabled;
     * false if not or if Vault is not installed
     */
    public boolean isEconomyEnabled() {
        return FactionsXL.getInstance().getEconomyProvider() != null && economyEnabled;
    }

    /**
     * @param channel
     * the chat channel
     * @return
     * the chat format
     */
    public String getChatFormat(ChatChannel channel) {
        switch (channel) {
            case ALLY:
                return chatFormatAlly;
            case COALITION:
                return chatFormatCoalition;
            case FACTION:
                return chatFormatFaction;
            default:
                return null;
        }
    }

    /**
     * @return
     * if LWC protection shall be enabled
     */
    public boolean isLWCEnabled() {
        return lwcEnabled;
    }

    /**
     * @return
     * if wilderness chunks shall be protected
     */
    public boolean isWildernessProtected() {
        return wildernessProtected;
    }

    /**
     * @return
     * true if hologram features are enabled;
     * false if not or if HolographicDisplays is not installed
     */
    public boolean areHologramsEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays") && hologramsEnabled;
    }

    /**
     * @return
     * the home hologram text lines
     */
    public List<String> getHomeHologramText() {
        return homeHologramText;
    }

    /**
     * @return
     * the nametag prefix
     */
    public String getNametagPrefix() {
        return nametagPrefix;
    }

    /**
     * @return
     * the time in ticks the scoreboard stays
     */
    public int getScoreboardExpiration() {
        return (int) (scoreboardExpiration * 20);
    }

    /**
     * @return
     * how often the scoreboard will be updated
     */
    public int getScoreboardUpdateInterval() {
        return (int) (scoreboardUpdateInterval * 20);
    }

    /**
     * @return
     * the default scoreboard prefixes
     */
    public boolean getDefaultScoreboardPrefixes() {
        return defaultScoreboardPrefixes;
    }

    /**
     * @return
     * if the scoreboard pops up when the player joins
     */
    public boolean isScoreboardEnabledByDefault() {
        return scoreboardEnabledByDefault;
    }

    /**
     * @return
     * if the scoreboard pops up when the player joins
     */
    public boolean isScoreboardFactionlessEnabled() {
        return scoreboardFactionlessEnabled;
    }

    /**
     * @return
     * the default scoreboard title
     */
    public String getDefaultScoreboardTitle() {
        return defaultScoreboardTitle;
    }

    /**
     * @return
     * the information the scoreboard shows
     */
    public List<String> getScoreboardDefault() {
        return scoreboardDefault;
    }

    /**
     * @return
     * the information the scoreboard shows
     */
    public List<String> getScoreboardFactionInfo() {
        return scoreboardFactionInfo;
    }

    /**
     * @return
     * the information the scoreboard shows
     */
    public List<String> getScoreboardFactionless() {
        return scoreboardFactionless;
    }

    /* Actions */
    @Override
    public void initialize() {
        if (!config.contains("language")) {
            config.set("language", language);
        }

        if (!config.contains("dayLength")) {
            config.set("dayLength", dayLength);
        }

        if (!config.contains("maxPower")) {
            config.set("maxPower", maxPower);
        }

        if (!config.contains("maxIdeaGroups")) {
            config.set("maxIdeaGroups", maxIdeaGroups);
        }

        if (!config.contains("economyEnabled")) {
            config.set("economyEnabled", economyEnabled);
        }

        if (!config.contains("chatFormat.ally")) {
            config.set("chatFormat.ally", chatFormatAlly);
        }

        if (!config.contains("chatFormat.coalition")) {
            config.set("chatFormat.coalition", chatFormatCoalition);
        }

        if (!config.contains("chatFormat.faction")) {
            config.set("chatFormat.faction", chatFormatFaction);
        }

        if (!config.contains("lwcEnabled")) {
            config.set("lwcEnabled", lwcEnabled);
        }

        if (!config.contains("wildernessProtected")) {
            config.set("wildernessProtected", wildernessProtected);
        }

        if (!config.contains("hologramsEnabled")) {
            config.set("hologramsEnabled", hologramsEnabled);
        }

        if (!config.contains("homeHologramText")) {
            config.set("homeHologramText", homeHologramText);
        }

        if (!config.contains("nametag.prefix")) {
            config.set("nametag.prefix", nametagPrefix);
        }

        if (!config.contains("scoreboard.expiration")) {
            config.set("scoreboard.expiration", scoreboardExpiration);
        }

        if (!config.contains("scoreboard.updateInterval")) {
            config.set("scoreboard.updateInterval", scoreboardUpdateInterval);
        }

        if (!config.contains("scoreboard.defaultPrefixes")) {
            config.set("scoreboard.defaultPrefixes", defaultScoreboardPrefixes);
        }

        if (!config.contains("scoreboard.enabledByDefault")) {
            config.set("scoreboard.enabledByDefault", scoreboardEnabledByDefault);
        }

        if (!config.contains("scoreboard.factionlessEnabled")) {
            config.set("scoreboard.factionlessEnabled", scoreboardFactionlessEnabled);
        }

        if (!config.contains("scoreboard.defaultTitle")) {
            config.set("scoreboard.defaultTitle", defaultScoreboardTitle);
        }

        if (!config.contains("scoreboard.default")) {
            config.set("scoreboard.default", scoreboardDefault);
        }

        if (!config.contains("scoreboard.factionInfo")) {
            config.set("scoreboard.factionInfo", scoreboardFactionInfo);
        }

        if (!config.contains("scoreboard.factionless")) {
            config.set("scoreboard.factionless", scoreboardFactionless);
        }

        save();
    }

    @Override
    public void load() {
        if (config.contains("language")) {
            language = config.getString("language");
        }

        if (config.contains("dayLength")) {
            dayLength = config.getDouble("dayLength");
        }

        if (config.contains("maxPower")) {
            maxPower = config.getInt("maxPower");
        }

        if (config.contains("maxIdeaGroups")) {
            maxIdeaGroups = config.getInt("maxIdeaGroups");
        }

        if (config.contains("economyEnabled")) {
            economyEnabled = config.getBoolean("economyEnabled");
        }

        if (config.contains("chatFormat.ally")) {
            chatFormatAlly = config.getString("chatFormat.ally");
        }

        if (config.contains("chatFormat.coalition")) {
            chatFormatCoalition = config.getString("chatFormat.coalition");
        }

        if (config.contains("chatFormat.faction")) {
            chatFormatFaction = config.getString("chatFormat.faction");
        }

        if (config.contains("lwcEnabled")) {
            lwcEnabled = config.getBoolean("lwcEnabled");
        }

        if (config.contains("wildernessProtected")) {
            wildernessProtected = config.getBoolean("wildernessProtected");
        }

        if (config.contains("hologramsEnabled")) {
            hologramsEnabled = config.getBoolean("hologramsEnabled");
        }

        if (config.contains("homeHologramText")) {
            homeHologramText = config.getStringList("homeHologramText");
        }

        if (config.contains("nametag.prefix")) {
            nametagPrefix = config.getString("nametag.prefix");
        }

        if (config.contains("scoreboard.expiration")) {
            scoreboardExpiration = config.getDouble("scoreboard.expiration");
        }

        if (config.contains("scoreboard.updateInterval")) {
            scoreboardUpdateInterval = config.getDouble("scoreboard.updateInterval");
        }

        if (config.contains("scoreboard.defaultPrefixes")) {
            defaultScoreboardPrefixes = config.getBoolean("scoreboard.defaultPrefixes");
        }

        if (config.contains("scoreboard.enabledByDefault")) {
            scoreboardEnabledByDefault = config.getBoolean("scoreboard.enabledByDefault");
        }

        if (config.contains("scoreboard.factionlessEnabled")) {
            scoreboardFactionlessEnabled = config.getBoolean("scoreboard.factionlessEnabled");
        }

        if (config.contains("scoreboard.defaultTitle")) {
            defaultScoreboardTitle = config.getString("scoreboard.defaultTitle");
        }

        if (config.contains("scoreboard.default")) {
            scoreboardDefault = config.getStringList("scoreboard.default");
        }

        if (config.contains("scoreboard.factionInfo")) {
            scoreboardFactionInfo = config.getStringList("scoreboard.factionInfo");
        }

        if (config.contains("scoreboard.factionless")) {
            scoreboardFactionless = config.getStringList("scoreboard.factionless");
        }
    }

}
