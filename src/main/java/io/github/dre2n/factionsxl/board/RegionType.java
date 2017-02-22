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

import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.economy.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public enum RegionType {

    BARREN(2000, Arrays.asList(Barren.LEVEL_1), FMessage.REGION_BARREN),
    CITY(500000, City.LEVELS, FMessage.REGION_CITY),
    DESERT(2000, Arrays.asList(Desert.LEVEL_1), FMessage.REGION_DESERT),
    FARMLAND(20000, Arrays.asList(Farmland.LEVEL_1), FMessage.REGION_FARMLAND),
    FOREST(10000, Arrays.asList(Forest.LEVEL_1), FMessage.REGION_FOREST),
    MAGIC(5000, Arrays.asList(Magic.LEVEL_1), FMessage.REGION_MAGIC),
    MOUNTAINOUS(4000, Arrays.asList(Mountainous.LEVEL_1), FMessage.REGION_MOUNTAINOUS),
    SEA(20000, Arrays.asList(Sea.LEVEL_1), FMessage.REGION_SEA);

    private int maxPopulation;
    private List<Map<Resource, Integer>> resources = new ArrayList<>();
    private FMessage name;

    RegionType(int maxPopulation, List<Map<Resource, Integer>> resources, FMessage name) {
        this.maxPopulation = maxPopulation;
        this.resources = resources;
        this.name = name;
    }

    /**
     * @return
     * the name of the type
     */
    public String getName() {
        return name.getMessage();
    }

    /**
     * @return
     * the maximum population
     */
    public int getMaxPopulation() {
        return maxPopulation;
    }

    /**
     * (maximum population / maximum level) * level
     *
     * @return
     * the maximum population
     */
    public int getMaxPopulation(int level) {
        return (maxPopulation / resources.size() + 1) * level;
    }

    /**
     * @param level
     * the region level
     * @return
     * the resources
     */
    public Map<Resource, Integer> getResources(int level) {
        try {
            return resources.get(level - 1);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return resources.get(0);
        }
    }

    /**
     * @param level
     * the level of the region
     * @return
     * a GUI with the income values
     */
    public Inventory getGUI(int level) {
        Inventory gui = Bukkit.createInventory(null, InventoryType.CHEST, ChatColor.GREEN + name.getMessage() + " - " + level);
        for (Map.Entry<Resource, Integer> resource : resources.get(level - 1).entrySet()) {
            ItemStack icon = new ItemStack(resource.getKey().getIcon());
            icon.setAmount(resource.getValue());
            gui.addItem(icon);
        }
        return gui;
    }

    // Map factories
    static class Barren {

        public static final Map<Resource, Integer> LEVEL_1 = new HashMap<>();

        static {
            LEVEL_1.put(Resource.GRAVEL, 100);
        }

    }

    static class City {

        public static final Map<Resource, Integer> LEVEL_1 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_2 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_3 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_4 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_5 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_6 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_7 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_8 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_9 = new HashMap<>();
        public static final Map<Resource, Integer> LEVEL_10 = new HashMap<>();
        public static final List<Map<Resource, Integer>> LEVELS = Arrays.asList(
                City.LEVEL_1, City.LEVEL_2, City.LEVEL_3, City.LEVEL_4, City.LEVEL_5, City.LEVEL_6, City.LEVEL_7, City.LEVEL_8, City.LEVEL_9, City.LEVEL_10
        );

        static {
            LEVEL_1.put(Resource.CRAFT, 2);
            LEVEL_2.put(Resource.CRAFT, 4);
            LEVEL_3.put(Resource.CRAFT, 6);
            LEVEL_4.put(Resource.CRAFT, 8);
            LEVEL_5.put(Resource.CRAFT, 10);
            LEVEL_6.put(Resource.CRAFT, 12);
            LEVEL_7.put(Resource.CRAFT, 14);
            LEVEL_8.put(Resource.CRAFT, 16);
            LEVEL_9.put(Resource.CRAFT, 18);
            LEVEL_10.put(Resource.CRAFT, 20);
            LEVEL_1.put(Resource.TAXES, 5);
            LEVEL_2.put(Resource.TAXES, 10);
            LEVEL_3.put(Resource.TAXES, 15);
            LEVEL_4.put(Resource.TAXES, 20);
            LEVEL_5.put(Resource.TAXES, 25);
            LEVEL_6.put(Resource.TAXES, 30);
            LEVEL_7.put(Resource.TAXES, 35);
            LEVEL_8.put(Resource.TAXES, 40);
            LEVEL_9.put(Resource.TAXES, 45);
            LEVEL_10.put(Resource.TAXES, 50);
        }

    }

    static class Desert {

        public static final Map<Resource, Integer> LEVEL_1 = new HashMap<>();

        static {
            LEVEL_1.put(Resource.GRAVEL, 100);
        }

    }

    static class Farmland {

        public static final Map<Resource, Integer> LEVEL_1 = new HashMap<>();

        static {
            LEVEL_1.put(Resource.GRAVEL, 100);
        }

    }

    static class Forest {

        public static final Map<Resource, Integer> LEVEL_1 = new HashMap<>();

        static {
            LEVEL_1.put(Resource.GRAVEL, 100);
        }

    }

    static class Magic {

        public static final Map<Resource, Integer> LEVEL_1 = new HashMap<>();

        static {
            LEVEL_1.put(Resource.GRAVEL, 100);
        }

    }

    static class Mountainous {

        public static final Map<Resource, Integer> LEVEL_1 = new HashMap<>();

        static {
            LEVEL_1.put(Resource.GRAVEL, 100);
        }

    }

    static class Sea {

        public static final Map<Resource, Integer> LEVEL_1 = new HashMap<>();

        static {
            LEVEL_1.put(Resource.GRAVEL, 100);
        }

    }

}
