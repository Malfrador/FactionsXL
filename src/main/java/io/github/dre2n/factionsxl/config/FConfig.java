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

import io.github.dre2n.commons.config.ConfigUtil;
import io.github.dre2n.commons.config.DREConfig;
import io.github.dre2n.commons.misc.EnumUtil;
import io.github.dre2n.commons.misc.ProgressBar;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.RegionType;
import static io.github.dre2n.factionsxl.board.RegionType.*;
import io.github.dre2n.factionsxl.board.dynmap.DynmapStyle;
import static io.github.dre2n.factionsxl.board.dynmap.DynmapStyle.DEFAULT_STYLE;
import io.github.dre2n.factionsxl.chat.ChatChannel;
import io.github.dre2n.factionsxl.economy.Resource;
import io.github.dre2n.factionsxl.relation.Relation;
import static io.github.dre2n.factionsxl.relation.Relation.*;
import static io.github.dre2n.factionsxl.util.ParsingUtil.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Represents the main config.yml.
 * The scoreboard part is taken from FactionsCosmetics by Sataniel.
 *
 * @author Daniel Saukel
 */
public class FConfig extends DREConfig {

    public static final int CONFIG_VERSION = 11;

    public static final long SECOND = 20;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;
    public static final long WEEK = DAY * 7;
    public static final long MONTH = DAY * 30;

    public static final String SCOREBOARD_VALUE_PREFIX = "&7 ";

    private String language = "english";
    private double autoSaveInterval = 10;
    private double dayLength = 24;
    private int maxIdeaGroups = 2;
    private int moveCapitalCooldown = 30;

    // Consume
    private double defaultManpowerModifier = 1;
    private int saturationPerDay = 10;

    // Economy
    private boolean economyEnabled = true;
    private double priceCreate = 1000;
    private double priceCreateVassal = 1000;
    private double priceHomeWarp = 5;
    private boolean playerHomesEnabled = true;
    private Map<Relation, Double> priceRelation = new HashMap<Relation, Double>() {
        {
            put(REAL_UNION, 500D);
            put(ALLIANCE, 750D);
            put(PERSONAL_UNION, 0D);
            put(LORD, 0D);
            put(VASSAL, 750D);
            put(COALITION, 0D);
            put(PEACE, 0D);
            put(ENEMY, 0D);
        }
    };
    private double priceClaimBase = 100;
    private double priceClaimPerChunk = 10;
    private double priceClaimIncrease = 100;
    private double importModifier = 2;
    private double exportModifier = 0.5;
    private int requiredResourceUnitsPer1000Persons = 10;

    // Chat
    private boolean publicChatHandled = false;
    private int localChatRange = 50;
    private String chatFormatLocal = PERM_PREFIX + "&7[" + RELATION_COLOR + FACTION_TAG + "&7] " + RELATION_COLOR + PLAYER_PREFIX + PLAYER_NAME + "&7" + ": " + PERM_SUFFIX;
    private String chatFormatPublic = PERM_PREFIX + "&f[" + RELATION_COLOR + FACTION_TAG + "&f] " + RELATION_COLOR + PLAYER_PREFIX + PLAYER_NAME + "&f" + ": " + PERM_SUFFIX;
    private String chatFormatAlly = "&d[" + RELATION_COLOR + FACTION_TAG + "&d] " + RELATION_COLOR + PLAYER_PREFIX + "&d" + PLAYER_NAME + ": ";
    private String chatFormatCoalition = "&c[" + RELATION_COLOR + FACTION_TAG + "&c] " + RELATION_COLOR + PLAYER_PREFIX + "&c" + PLAYER_NAME + ": ";
    private String chatFormatFaction = RELATION_COLOR.getPlaceholder() + PLAYER_PREFIX + "&a" + PLAYER_TITLE + " " + PLAYER_NAME + ": ";

    // Protection
    private boolean lwcEnabled = true;
    private boolean wildernessProtected = true;

    // PvP
    private boolean territoryProtectionEnabled = true;
    private double territoryShield = 0.66;
    private boolean capitalProtectionEnabled = false;

    // Power
    private int maxPower = 100;
    private int minPower = -100;
    private long powerUpdateInterval = MINUTE;
    private double powerIncreaseRate = 1;
    private double powerDecreaseRate = 0.01;
    private double powerDeathLoss = 25;

    // Holograms
    private boolean hologramsEnabled = true;
    private List<String> homeHologramText = new ArrayList<>(Arrays.asList(
            "&4&lH O M E",
            "&a" + ProgressBar.BAR,
            "&6&l=== " + FACTION_LONG_TAG + " &6&l===",
            "&7&o" + FACTION_CAPITAL,
            "&a" + ProgressBar.BAR
    ));

    // Nametag and scoreboard
    private String nametagPrefix = "%relation_color%%faction_short_tag% ";
    private double scoreboardExpiration = 7;
    private double scoreboardUpdateInterval = 1;
    private boolean defaultScoreboardPrefixes = true;
    private boolean scoreboardEnabledByDefault = true;
    private boolean scoreboardFactionlessEnabled = true;
    private String defaultScoreboardTitle = "&a" + PLAYER_NAME;
    private List<String> scoreboardDefault = new ArrayList<>(Arrays.asList(
            "&6Your Faction",
            SCOREBOARD_VALUE_PREFIX + FACTION_TAG,
            "&3Your Dynasty",
            SCOREBOARD_VALUE_PREFIX + PLAYER_DYNASTY,
            "&6Your Power",
            SCOREBOARD_VALUE_PREFIX + PLAYER_POWER,
            "&3Balance",
            SCOREBOARD_VALUE_PREFIX + PLAYER_BALANCE
    ));
    private List<String> scoreboardFactionInfo = new ArrayList<>(Arrays.asList(
            "&6Government Type",
            SCOREBOARD_VALUE_PREFIX + FACTION_GOVERNMENT_TYPE,
            "&3Federation",
            SCOREBOARD_VALUE_PREFIX + FEDERATION_TAG,
            "&6Leader",
            SCOREBOARD_VALUE_PREFIX + FACTION_ADMIN,
            "&3Members",
            SCOREBOARD_VALUE_PREFIX + FACTION_ONLINE_COUNT + "/" + FACTION_PLAYER_COUNT,
            "&6Capital",
            SCOREBOARD_VALUE_PREFIX + FACTION_CAPITAL,
            "&3Stability / Power / Provinces",
            SCOREBOARD_VALUE_PREFIX + FACTION_STABILITY + "/" + FACTION_POWER + "/" + FACTION_PROVINCE_COUNT
    ));
    private List<String> scoreboardFactionless = new ArrayList<>(Arrays.asList("Join a faction!"));

    // Dynmap
    private boolean dynmapEnabled = true;
    private int dynmapLayerPriorityFaction = 2;
    private int dynmapLayerPriorityRegion = 2;
    private int dynmapLayerMinimumZoom = 0;
    private String dynmapDescriptionFaction
            = "<div class=\"infowindow\">\n"
            + "<span style=\"font-weight: bold; font-size: 150%;\">" + FACTION_LONG_TAG + "</span><br>\n"
            + "<span style=\"font-style: italic; font-size: 110%;\">" + FACTION_DESCRIPTION + "</span><br>"
            + "<br>\n"
            + "<span style=\"font-weight: bold;\">Admin:</span> " + FACTION_ADMIN + "<br>\n"
            + "<span style=\"font-weight: bold;\">Moderators:</span> " + FACTION_MOD_LIST + "<br>\n"
            + "<span style=\"font-weight: bold;\">Members:</span> " + FACTION_MEMBER_LIST + "<br>\n"
            + "<span style=\"font-weight: bold;\">TOTAL:</span> " + FACTION_PLAYER_COUNT + "<br>\n"
            + "</br>\n"
            + "<span style=\"font-weight: bold;\">Bank:</span> " + FACTION_BALANCE + "<br>\n"
            + "<br>\n"
            + "</div>";
    private String dynmapDescriptionRegion
            = "<div class=\"infowindow\">\n"
            + "<span style=\"font-weight: bold; font-size: 150%;\">" + REGION_NAME + " (" + REGION_OWNER + ")" + "</span><br>\n"
            + "<br>\n"
            + "<span style=\"font-weight: bold;\">Type:</span> " + REGION_TYPE + "<br>\n"
            + "<span style=\"font-weight: bold;\">Level:</span> " + REGION_LEVEL + "<br>\n"
            + "<span style=\"font-weight: bold;\">Population:</span> " + REGION_POPULATION + "<br>\n"
            + "</br>\n"
            + "<span style=\"font-weight: bold;\">Cores:</span> " + REGION_CORE_LIST + "<br>\n"
            + "<span style=\"font-weight: bold;\">Claims:</span> " + REGION_CLAIM_LIST + "<br>\n"
            + "<br>\n"
            + "</div>";
    private double dynmapUpdateIntervalFaction = 5 * 60;
    private double dynmapUpdateIntervalRegion = 5 * 60;
    private List<String> dynmapHiddenWorlds = new ArrayList<>();
    private Map<RegionType, DynmapStyle> dynmapRegionTypeStyles = new HashMap<RegionType, DynmapStyle>() {
        {
            put(BARREN, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#9B8600").setFillColor("#816300"));
            put(CITY, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#5D82AE").setFillColor("#8EADC8"));
            put(DESERT, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#D0B203").setFillColor("#FFCF21"));
            put(FARMLAND, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#72C143").setFillColor("#A5F443"));
            put(FOREST, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#557B4C").setFillColor("#103700"));
            put(MAGIC, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#F8AB00").setFillColor("#DF3418"));
            put(MOUNTAINOUS, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#EDEDED").setFillColor("#DADADA"));
            put(SEA, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#00C7C2").setFillColor("#0088C2"));
            put(WARZONE, new DynmapStyle(DEFAULT_STYLE).setStrokeColor("#FF3333").setFillColor("#CC0000"));
        }
    };

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
     * the auto save interval
     */
    public long getAutoSaveInterval() {
        return (long) (autoSaveInterval * MINUTE);
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
     * the maximum amount of idea groups that a faction can have
     */
    public int getMaxIdeaGroups() {
        return maxIdeaGroups;
    }

    /**
     * @return
     * the time millis until a faction may move its capital again.
     */
    public long getMoveCapitalCooldown() {
        return moveCapitalCooldown * DAY;
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
     * the saturation a faction can get / lose per day
     */
    public int getSaturationPerDay() {
        return saturationPerDay;
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
     * @return
     * the price to create a faction
     */
    public double getPriceCreate() {
        return priceCreate;
    }

    /**
     * @return
     * the price to create a vassal
     */
    public double getPriceCreateVassal() {
        return priceCreateVassal;
    }

    /**
     * @return
     * the price to warp to the faction home
     */
    public double getPriceHomeWarp() {
        return priceHomeWarp;
    }

    /**
     * @return
     * if per player homes (/f playerHome) are enabled
     */
    public boolean arePlayerHomesEnabled() {
        return playerHomesEnabled;
    }

    /**
     * @param relation
     * the new relation
     * @return
     * the price to get the relation
     */
    public double getPriceRelation(Relation relation) {
        return priceRelation.get(relation) != null ? priceRelation.get(relation) : 0;
    }

    /**
     * @return
     * the base price for claiming a region
     */
    public double getPriceClaimBase() {
        return priceClaimBase;
    }

    /**
     * @return
     * the price per chunk for claiming a region
     */
    public double getPriceClaimPerChunk() {
        return priceClaimPerChunk;
    }

    /**
     * @return
     * the claiming price increase per region that the faction already owns
     */
    public double getPriceClaimIncrease() {
        return priceClaimIncrease;
    }

    /**
     * @return
     * the value modifier for importing goods
     */
    public double getImportModifier() {
        return importModifier;
    }

    /**
     * @return
     * the modifier for exporting goods
     */
    public double getExportModifier() {
        return exportModifier;
    }

    /**
     * @return
     * the amount of units 1000 persons
     */
    public int getRequiredResourceUnitsPer1000Persons() {
        return requiredResourceUnitsPer1000Persons;
    }

    /**
     * @return
     * if FXL should handle the public chat as well
     */
    public boolean isPublicChatHandled() {
        return publicChatHandled;
    }

    /**
     * @return
     * the block range of the local chat
     */
    public int getLocalChatRange() {
        return localChatRange;
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
            case LOCAL:
                return chatFormatLocal;
            case PUBLIC:
                return chatFormatPublic;
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
     * if players shall be protected in their territory
     */
    public boolean isTerritoryProtectionEnabled() {
        return territoryProtectionEnabled;
    }

    /**
     * @return
     * how much of the PVP damage in foreign territories shall be absorbed
     */
    public double getTerritoryShield() {
        return territoryShield;
    }

    /**
     * @return
     * if players shall only be protected in their capital province
     */
    public boolean isCapitalProtectionEnabled() {
        return capitalProtectionEnabled;
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
     * the minimum power that a player may store
     */
    public int getMinPower() {
        return minPower;
    }

    /**
     * @return
     * the power update interval
     */
    public long getPowerUpdateInterval() {
        return powerUpdateInterval;
    }

    /**
     * @return
     * the power increase rate
     */
    public double getPowerIncreaseRate() {
        return powerIncreaseRate;
    }

    /**
     * @return
     * the power decrease rate
     */
    public double getPowerDecreaseRate() {
        return powerDecreaseRate;
    }

    /**
     * @return
     * the power death loss rate
     */
    public double getPowerDeathLoss() {
        return powerDeathLoss;
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

    /**
     * @return
     * if Dynmap layers shall be enabled
     */
    public boolean isDynmapEnabled() {
        Plugin dynmap = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if (dynmap == null || !dynmap.isEnabled()) {
            return false;
        } else {
            return dynmapEnabled;
        }
    }

    /**
     * @return
     * the Dynmap faction description
     */
    public String getDynmapDescriptionFaction() {
        return dynmapDescriptionFaction;
    }

    /**
     * @return
     * the Dynmap region description
     */
    public String getDynmapDescriptionRegion() {
        return dynmapDescriptionRegion;
    }

    /**
     * @return
     * the Dynmap update interval for factions
     */
    public long getDynmapUpdateIntervalFaction() {
        return (long) (dynmapUpdateIntervalFaction * 20);
    }

    /**
     * @return
     * the Dynmap update interval for regions
     */
    public long getDynmapUpdateIntervalRegion() {
        return (long) (dynmapUpdateIntervalRegion * 20);
    }

    /**
     * @return
     * the priority of the factions layer
     */
    public int getDynmapLayerPriorityFaction() {
        return dynmapLayerPriorityFaction;
    }

    /**
     * @return
     * the priority of the regions layer
     */
    public int getDynmapLayerPriorityRegion() {
        return dynmapLayerPriorityRegion;
    }

    /**
     * @return
     * the minimum zoom of FXL Dynmaps layers
     */
    public int getDynmapLayerMinimumZoom() {
        return dynmapLayerMinimumZoom;
    }

    /**
     * @return
     * the worlds that are hidden at the dynamic map
     */
    public List<String> getDynmapHiddenWorlds() {
        return dynmapHiddenWorlds;
    }

    /**
     * @return
     * a map of all region type styles
     */
    public Map<RegionType, DynmapStyle> getDynmapRegionTypeStyles() {
        return dynmapRegionTypeStyles;
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

        if (!config.contains("maxIdeaGroups")) {
            config.set("maxIdeaGroups", maxIdeaGroups);
        }

        if (!config.contains("moveCapitalCooldown")) {
            config.set("moveCapitalCooldown", moveCapitalCooldown);
        }

        if (!config.contains("defaultManpowerModifier")) {
            config.set("defaultManpowerModifier", defaultManpowerModifier);
        }

        if (!config.contains("saturationPerDay")) {
            config.set("saturationPerDay", saturationPerDay);
        }

        if (!config.contains("economyEnabled")) {
            config.set("economyEnabled", economyEnabled);
        }

        if (!config.contains("playerHomesEnabled")) {
            config.set("playerHomesEnabled", playerHomesEnabled);
        }

        if (!config.contains("price.create")) {
            config.set("price.create", priceCreate);
        }

        if (!config.contains("price.createVassal")) {
            config.set("price.createVassal", priceCreateVassal);
        }

        if (!config.contains("price.homeWarp")) {
            config.set("price.homeWarp", priceHomeWarp);
        }

        if (!config.contains("price.relation")) {
            config.createSection("price.relation");
            for (Entry<Relation, Double> entry : priceRelation.entrySet()) {
                config.set("price.relation." + entry.getKey().toString(), entry.getValue());
            }
        }

        if (!config.contains("price.claim.base")) {
            config.set("price.claim.base", priceClaimBase);
        }

        if (!config.contains("price.claim.perChunk")) {
            config.set("price.claim.perChunk", priceClaimPerChunk);
        }

        if (!config.contains("price.claim.increase")) {
            config.set("price.claim.increase", priceClaimIncrease);
        }

        if (!config.contains("importModifier")) {
            config.set("importModifier", importModifier);
        }

        if (!config.contains("exportModifier")) {
            config.set("exportModifier", exportModifier);
        }

        if (!config.contains("requiredResourceUnitsPer1000Persons")) {
            config.set("requiredResourceUnitsPer1000Persons", requiredResourceUnitsPer1000Persons);
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

        if (!config.contains("chatFormat.local")) {
            config.set("chatFormat.local", chatFormatLocal);
        }

        if (!config.contains("chatFormat.public")) {
            config.set("chatFormat.public", chatFormatPublic);
        }

        if (!config.contains("chatFormat.handlePublic")) {
            config.set("chatFormat.handlePublic", publicChatHandled);
        }

        if (!config.contains("lwcEnabled")) {
            config.set("lwcEnabled", lwcEnabled);
        }

        if (!config.contains("wildernessProtected")) {
            config.set("wildernessProtected", wildernessProtected);
        }

        if (!config.contains("territoryProtectionEnabled")) {
            config.set("territoryProtectionEnabled", territoryProtectionEnabled);
        }

        if (!config.contains("territoryShield")) {
            config.set("territoryShield", territoryShield);
        }

        if (!config.contains("capitalProtectionEnabled")) {
            config.set("capitalProtectionEnabled", capitalProtectionEnabled);
        }

        if (!config.contains("maxPower")) {
            config.set("maxPower", maxPower);
        }

        if (!config.contains("minPower")) {
            config.set("minPower", minPower);
        }

        if (!config.contains("powerUpdateInterval")) {
            config.set("powerUpdateInterval", powerUpdateInterval);
        }

        if (!config.contains("powerIncreaseRate")) {
            config.set("powerIncreaseRate", powerIncreaseRate);
        }

        if (!config.contains("powerDecreaseRate")) {
            config.set("powerDecreaseRate", powerDecreaseRate);
        }

        if (!config.contains("powerDeathLoss")) {
            config.set("powerDeathLoss", powerDeathLoss);
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

        if (!config.contains("dynmap.enabled")) {
            config.set("dynmap.enabled", dynmapEnabled);
        }

        if (!config.contains("dynmap.layer.priority.faction")) {
            config.set("dynmap.layer.priority.faction", dynmapLayerPriorityFaction);
        }

        if (!config.contains("dynmap.layer.priority.region")) {
            config.set("dynmap.layer.priority.region", dynmapLayerPriorityRegion);
        }

        if (!config.contains("dynmap.layer.minimumZoom")) {
            config.set("dynmap.layer.minimumZoom", dynmapLayerMinimumZoom);
        }

        if (!config.contains("dynmap.description.faction")) {
            config.set("dynmap.description.faction", dynmapDescriptionFaction);
        }

        if (!config.contains("dynmap.description.region")) {
            config.set("dynmap.description.region", dynmapDescriptionRegion);
        }

        if (!config.contains("dynmap.updateInterval.faction")) {
            config.set("dynmap.updateInterval.faction", dynmapUpdateIntervalFaction);
        }

        if (!config.contains("dynmap.updateInterval.region")) {
            config.set("dynmap.updateInterval.region", dynmapUpdateIntervalRegion);
        }

        if (!config.contains("dynmap.hiddenWorlds")) {
            config.set("dynmap.hiddenWorlds", dynmapHiddenWorlds);
        }

        if (!config.contains("dynmap.regionTypeStyles")) {
            for (Entry<RegionType, DynmapStyle> entry : dynmapRegionTypeStyles.entrySet()) {
                config.set("dynmap.regionTypeStyles." + entry.getKey().toString(), entry.getValue().fillColor + "/" + entry.getValue().lineColor);
            }
        }

        for (Resource resource : Resource.values()) {
            config.set("resourcePrices." + resource.toString(), resource.getValue());
            config.set("requiredAmountModifiers." + resource.toString(), resource.getRequiredAmountModifier());
        }

        config.createSection("regionIncome");

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

        if (config.contains("maxIdeaGroups")) {
            maxIdeaGroups = config.getInt("maxIdeaGroups");
        }

        if (config.contains("moveCapitalCooldown")) {
            moveCapitalCooldown = config.getInt("moveCapitalCooldown");
        }

        if (config.contains("defaultManpowerModifier")) {
            defaultManpowerModifier = config.getDouble("defaultManpowerModifier");
        }

        if (config.contains("saturationPerDay")) {
            saturationPerDay = config.getInt("saturationPerDay");
        }

        if (config.contains("economyEnabled")) {
            economyEnabled = config.getBoolean("economyEnabled");
        }

        if (config.contains("playerHomesEnabled")) {
            playerHomesEnabled = config.getBoolean("playerHomesEnabled");
        }

        if (config.contains("price.create")) {
            priceCreate = config.getDouble("price.create");
        }

        if (config.contains("price.createVassal")) {
            priceCreateVassal = config.getDouble("price.createVassal");
        }

        if (config.contains("price.homeWarp")) {
            priceHomeWarp = config.getDouble("price.homeWarp");
        }

        if (config.contains("price.relation")) {
            for (Entry<String, Object> entry : ConfigUtil.getMap(config, "price.relation").entrySet()) {
                priceRelation.put(Relation.fromString(entry.getKey()), (double) entry.getValue());
            }
        }

        if (config.contains("price.claim.base")) {
            priceClaimBase = config.getDouble("price.claim.base");
        }

        if (config.contains("price.claim.perChunk")) {
            priceClaimPerChunk = config.getDouble("price.claim.perChunk");
        }

        if (config.contains("price.claim.increase")) {
            priceClaimIncrease = config.getDouble("price.claim.increase");
        }

        if (config.contains("importModifier")) {
            importModifier = config.getDouble("importModifier");
        }

        if (config.contains("exportModifier")) {
            exportModifier = config.getDouble("exportModifier");
        }

        if (config.contains("requiredResourceUnitsPer1000Persons")) {
            requiredResourceUnitsPer1000Persons = config.getInt("requiredResourceUnitsPer1000Persons");
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

        if (config.contains("chatFormat.local")) {
            chatFormatLocal = config.getString("chatFormat.local");
        }

        if (config.contains("chatFormat.public")) {
            chatFormatPublic = config.getString("chatFormat.public");
        }

        if (config.contains("chatFormat.handlePublic")) {
            publicChatHandled = config.getBoolean("chatFormat.handlePublic");
        }

        if (config.contains("lwcEnabled")) {
            lwcEnabled = config.getBoolean("lwcEnabled");
        }

        if (config.contains("wildernessProtected")) {
            wildernessProtected = config.getBoolean("wildernessProtected");
        }

        if (config.contains("territoryProtectionEnabled")) {
            territoryProtectionEnabled = config.getBoolean("territoryProtectionEnabled");
        }

        if (config.contains("territoryShield")) {
            territoryShield = config.getDouble("territoryShield");
        }

        if (config.contains("capitalProtectionEnabled")) {
            capitalProtectionEnabled = config.getBoolean("capitalProtectionEnabled");
        }

        if (config.contains("maxPower")) {
            maxPower = config.getInt("maxPower");
        }

        if (config.contains("minPower")) {
            minPower = config.getInt("minPower");
        }

        if (config.contains("powerUpdateInterval")) {
            powerUpdateInterval = config.getLong("powerUpdateInterval");
        }

        if (config.contains("powerIncreaseRate")) {
            powerIncreaseRate = config.getDouble("powerIncreaseRate");
        }

        if (config.contains("powerDecreaseRate")) {
            powerDecreaseRate = config.getDouble("powerDecreaseRate");
        }

        if (config.contains("powerDeathLoss")) {
            powerDeathLoss = config.getDouble("powerDeathLoss");
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

        if (config.contains("dynmap.enabled")) {
            dynmapEnabled = config.getBoolean("dynmap.enabled");
        }

        if (config.contains("dynmap.layer.priority.faction")) {
            dynmapLayerPriorityFaction = config.getInt("dynmap.layer.priority.faction");
        }

        if (config.contains("dynmap.layer.priority.region")) {
            dynmapLayerPriorityRegion = config.getInt("dynmap.layer.priority.region");
        }

        if (config.contains("dynmap.layer.minimumZoom")) {
            dynmapLayerMinimumZoom = config.getInt("dynmap.layer.minimumZoom");
        }

        if (config.contains("dynmap.description.faction")) {
            dynmapDescriptionFaction = config.getString("dynmap.description.faction");
        }

        if (config.contains("dynmap.description.region")) {
            dynmapDescriptionRegion = config.getString("dynmap.description.region");
        }

        if (config.contains("dynmap.updateInterval.faction")) {
            dynmapUpdateIntervalFaction = config.getDouble("dynmap.updateInterval.faction");
        }

        if (config.contains("dynmap.updateInterval.region")) {
            dynmapUpdateIntervalRegion = config.getDouble("dynmap.updateInterval.region");
        }

        if (config.contains("dynmap.hiddenWorlds")) {
            dynmapHiddenWorlds = config.getStringList("dynmap.hiddenWorlds");
        }

        for (Entry<String, Object> entry : ConfigUtil.getMap(config, "dynmap.regionTypeStyles").entrySet()) {
            RegionType type = null;
            if (EnumUtil.isValidEnum(RegionType.class, entry.getKey())) {
                type = RegionType.valueOf(entry.getKey());
            }
            DynmapStyle style = new DynmapStyle(DynmapStyle.DEFAULT_STYLE);
            String[] colors = ((String) entry.getValue()).split("/");
            if (colors.length == 2) {
                style.setFillColor(colors[0]);
                style.setStrokeColor(colors[1]);
            }
            dynmapRegionTypeStyles.put(type, style);
        }
        Resource.loadPrices(ConfigUtil.getMap(config, "resourcePrices"));
        Resource.loadRequiredAmountModifiers(ConfigUtil.getMap(config, "requiredAmountModifiers"));
        RegionType.loadResources(ConfigUtil.getMap(config, "regionIncome"));
    }

}
