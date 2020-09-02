/*
 * Copyright (C) 2017-2020 Daniel Saukel
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

package de.erethon.factionsxl.building;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.util.LazyChunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a building in the world.
 *
 * @author Malfrador
 */

public class BuildSite implements ConfigurationSerializable {

    FactionsXL plugin = FactionsXL.getInstance();
    BuildingManager buildingManager = plugin.getBuildingManager();

    Building building;
    Region region;
    Location corner;
    Location otherCorner;
    Map<Material, Integer> placedBlocks = new HashMap<>();
    boolean finished;

    public BuildSite(Building b, Region rg, Location loc1, Location loc2) {
        building = b;
        region = rg;
        finished = false;
        corner = loc1;
        otherCorner = loc2;
    }

    public BuildSite (Map<String, Object> args) {
        building = buildingManager.getByID((String) args.get("building"));
        region = plugin.getBoard().getById((int) args.get("region"));
        corner = Location.deserialize((Map<String, Object>) args.get("corner"));
        otherCorner = Location.deserialize((Map<String, Object>) args.get("otherCorner"));
        finished = (boolean) args.get("finished");
    }

    public BuildSite (ConfigurationSection config) {
        building = buildingManager.getByID((String) config.get("building"));
        region = plugin.getBoard().getById((int) config.get("region"));
        corner =  config.getLocation("corner");
        otherCorner = config.getLocation("otherCorner");
        finished = config.getBoolean("finished");
    }

    public Building getBuilding() {
        return building;
    }

    public Region getRegion() {
        return region;
    }

    public Location getCorner() {
        return corner;
    }

    public Location getOtherCorner() {
        return otherCorner;
    }

    public Map<Material, Integer> getPlacedBlocks() {
        return placedBlocks;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> args = new HashMap<>();
        args.put("building", building.getId());
        args.put("region", region.getId());
        args.put("corner", corner.serialize());
        args.put("otherCorner", otherCorner.serialize());
        args.put("finished", finished);
        return args;
    }
}
