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
package io.github.dre2n.factionsxl.board;

import io.github.dre2n.commons.util.EnumUtil;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.dynmap.DynmapStyle;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.economy.Resource;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.util.SerializationUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Represents an undividable collection of claims.
 *
 * @author Daniel Saukel
 */
public class Region {

    FactionsXL plugin = FactionsXL.getInstance();

    private int id;
    private String name;
    private RegionType type;
    private int level;
    private int population;
    private Faction owner;
    private List<Chunk> chunks = new ArrayList<>();
    private Map<Faction, Date> cores = new HashMap<>();
    private Map<Faction, Date> claims = new HashMap<>();
    private String mapFillColor;
    private String mapLineColor;
    private DynmapStyle dynmapStyle;

    public Region(String name, Chunk chunk) {
        id = plugin.getBoard().generateId();
        type = RegionType.BARREN;
        this.name = name;
        chunks.add(chunk);
    }

    public Region(int id, ConfigurationSection config) {
        this.id = id;

        name = config.getString("name");
        String typeString = config.getString("type");
        type = EnumUtil.isValidEnum(RegionType.class, typeString) ? RegionType.valueOf(typeString) : RegionType.BARREN;
        level = config.getInt("level");
        population = config.getInt("population");
        if (config.contains("owner")) {
            owner = plugin.getFactionCache().getById(config.getInt("owner"));
        }

        for (String chunk : config.getStringList("chunks")) {
            chunks.add(SerializationUtil.deserializeChunk(chunk));
        }
        for (Entry<String, Object> entry : config.getConfigurationSection("cores").getValues(true).entrySet()) {
            Faction faction = plugin.getFactionCache().getById(NumberUtil.parseInt(entry.getKey()));
            Date date = new Date((long) entry.getValue());
            cores.put(faction, date);
        }
        mapFillColor = config.getString("mapFillColor");
        mapLineColor = config.getString("mapLineColor");
    }

    public ConfigurationSection serialize() {
        ConfigurationSection config = new YamlConfiguration();

        config.set("name", name);
        config.set("type", type.toString());
        config.set("level", level);
        if (owner != null) {
            config.set("population", population);
        } else {
            config.set("population", 0);
        }
        if (owner != null) {
            config.set("owner", owner.getId());
        }

        List<String> serializedChunks = new ArrayList<>();
        for (Chunk chunk : chunks) {
            serializedChunks.add(SerializationUtil.serializeChunk(chunk));
        }
        config.set("chunks", serializedChunks);

        Map<Integer, Long> serializedCores = new HashMap<>();
        for (Entry<Faction, Date> entry : cores.entrySet()) {
            serializedCores.put(entry.getKey().getId(), entry.getValue().getTime());
        }
        config.set("cores", serializedCores);

        Map<Integer, Long> serializedClaims = new HashMap<>();
        for (Entry<Faction, Date> entry : claims.entrySet()) {
            serializedClaims.put(entry.getKey().getId(), entry.getValue().getTime());
        }
        config.set("claims", serializedClaims);

        config.set("mapFillColor", mapFillColor);
        config.set("mapLineColor", mapLineColor);

        return config;
    }

    /* Getters and setters */
    /**
     * @return
     * the ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return
     * the name of the region
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     * the resource type of the region
     */
    public RegionType getType() {
        return type;
    }

    /**
     * @return
     * a Map of the resources of the region
     */
    public Map<Resource, Integer> getResources() {
        return type.getResources(level);
    }

    /**
     * @param type
     * the resource type to set
     */
    public void setType(RegionType type) {
        this.type = type;
    }

    /**
     * @return
     * the region resource level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level
     * the region resource level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return
     * the population for FationMobs
     */
    public int getPopulation() {
        return population;
    }

    /**
     * @param population
     * the amount of soldiers for FactionMobs
     */
    public void setPopulation(int population) {
        this.population = population;
    }

    /**
     * @return
     * the faction that owns the region
     */
    public Faction getOwner() {
        return owner;
    }

    /**
     * @param faction
     * the new owner
     */
    public void setOwner(Faction faction) {
        owner = faction;
    }

    /**
     * @return
     * the chunks that belong to this region
     */
    public List<Chunk> getChunks() {
        return chunks;
    }

    /**
     * @return
     * the amount of chunks that belong to this region
     */
    public int getSize() {
        return chunks.size();
    }

    /**
     * @return
     * all faction that regard this region as their core land
     */
    public Map<Faction, Date> getCoreFactions() {
        return cores;
    }

    /**
     * @return
     * all factions that claim this land
     */
    public Map<Faction, Date> getClaimFactions() {
        return claims;
    }

    /**
     * @return
     * the Dynmap fill color
     */
    public String getMapFillColor() {
        return mapFillColor;
    }

    /**
     * @return
     * the Dynmap line color
     */
    public String getMapLineColor() {
        return mapLineColor;
    }

    /**
     * @param fill
     * the Dynmap fill color to set
     * @param line
     * the Dynmap line color to set
     */
    public void setMapColor(String fill, String line) {
        if (fill.matches("#[0-9A-F]{6}") && line.matches("#[0-9A-F]{6}")) {
            mapFillColor = fill;
            mapLineColor = line;
            dynmapStyle = new DynmapStyle(DynmapStyle.DEFAULT_STYLE).setStrokeColor(mapLineColor).setFillColor(mapFillColor);
        }
    }

    /**
     * @return
     * the dynmap style of the faction
     */
    public DynmapStyle getDynmapStyle() {
        if (dynmapStyle == null) {
            if (mapLineColor == null || mapFillColor == null) {
                dynmapStyle = plugin.getFConfig().getDynmapRegionTypeStyles().get(type);
            } else {
                dynmapStyle = new DynmapStyle(DynmapStyle.DEFAULT_STYLE).setStrokeColor(mapLineColor).setFillColor(mapFillColor);
            }
        }
        return dynmapStyle;
    }

    /**
     * @return
     * true if the region has no owner
     */
    public boolean isNeutral() {
        return owner == null;
    }

    /**
     * @return
     * the world where the region is
     */
    public World getWorld() {
        return chunks.get(0).getWorld();
    }

    /**
     * @param faction
     * the faction to calculate the per claim price increase
     * @return
     * the price to claim this region
     */
    public double getClaimPrice(Faction faction) {
        FConfig config = plugin.getFConfig();
        double base = config.getPriceClaimBase() + config.getPriceClaimPerChunk() * chunks.size();
        double increase = faction != null ? config.getPriceClaimIncrease() * faction.getRegions().size() : 0;
        return base + increase;
    }

}
