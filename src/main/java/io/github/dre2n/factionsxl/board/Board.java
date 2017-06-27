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

import io.github.dre2n.commons.config.ConfigUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Stores all regions and claim ownerships.
 *
 * @author Daniel Saukel
 */
public class Board {

    public static final File FILE = new File(FactionsXL.getInstance().getDataFolder() + "/board.yml");

    private List<Region> regions = new ArrayList<>();

    public Board() {
        if (!FILE.exists()) {
            try {
                FILE.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        if (config.contains("regions")) {
            for (Entry<String, Object> region : ConfigUtil.getMap(config, "regions").entrySet()) {
                int id = Integer.parseInt(region.getKey());
                regions.add(new Region(id, (ConfigurationSection) region.getValue()));
            }

        } else {
            config.set("regions", regions);
            try {
                config.save(FILE);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /* Getters and setters */
    /**
     * @param id
     * the ID to check
     * @return
     * the region that contains the chunk
     */
    public Region getById(int id) {
        for (Region region : regions) {
            if (region.getId() == id) {
                return region;
            }
        }
        return null;
    }

    /**
     * @param name
     * the name to check
     * @return
     * the region that contains the chunk
     */
    public Region getByName(String name) {
        for (Region region : regions) {
            if (region.getName().equals(name)) {
                return region;
            }
        }
        return null;
    }

    /**
     * @param location
     * the location to check
     * @return
     * the region that contains the chunk
     */
    public Region getByLocation(Location location) {
        return getByChunk(location.getChunk());
    }

    /**
     * @param chunk
     * the chunk to check
     * @return
     * the region that contains the chunk
     */
    public Region getByChunk(Chunk chunk) {
        for (Region region : regions) {
            if (region.getChunks().contains(chunk)) {
                return region;
            }
        }
        return null;
    }

    /**
     * @return
     * a list of all regions
     */
    public List<Region> getRegions() {
        return regions;
    }

    /**
     * @param location
     * the location to check
     * @return
     * true if the region is neutral
     * false if the region is owned by a faction
     * false if the chunk is not covered by a region
     */
    public boolean isNeutral(Location location) {
        Region region = getByLocation(location);
        return region == null ? false : region.isNeutral();
    }

    /**
     * @param chunk
     * the chunk to check
     * @return
     * true if the region is neutral
     * false if the region is owned by a faction
     * false if the chunk is not covered by a region
     */
    public boolean isNeutral(Chunk chunk) {
        Region region = getByChunk(chunk);
        return region == null ? false : region.isNeutral();
    }

    /**
     * @param location
     * the location to check
     * @return
     * true if the region is annexable
     * false if the chunk is not covered by a region
     */
    public boolean isAnnexable(Location location) {
        Region region = getByLocation(location);
        return region == null ? false : region.isAnnexable();
    }

    /**
     * @param chunk
     * the chunk to check
     * @return
     * true if the region is annexable
     * false if the chunk is not covered by a region
     */
    public boolean isAnnexable(Chunk chunk) {
        Region region = getByChunk(chunk);
        return region == null ? false : region.isAnnexable();
    }

    /**
     * @param location
     * the location to check
     * @return
     * true if the chunk is not covered by a region
     */
    public boolean isWilderness(Location location) {
        return getByLocation(location) == null;
    }

    /**
     * @param chunk
     * the chunk to check
     * @return
     * true if the chunk is not covered by a region
     */
    public boolean isWilderness(Chunk chunk) {
        return getByChunk(chunk) == null;
    }

    /**
     * @return
     * a new, unused region ID.
     */
    public int generateId() {
        return regions.size();
    }

    /* Serialize */
    public void save(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (Region region : regions) {
            config.set("regions." + region.getId(), region.serialize());
        }
        try {
            config.save(FILE);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
