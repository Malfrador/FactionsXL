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
package io.github.dre2n.factionsxl.economy;

import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public enum Resource {

    // MINES
    // Resource
    COAL(new ItemStack(Material.COAL), 20),
    SULPHUR(new ItemStack(Material.SULPHUR), 25),
    GOLD(new ItemStack(Material.GOLD_INGOT), 800),
    IRON(new ItemStack(Material.IRON_INGOT), 75),
    DIAMOND(new ItemStack(Material.DIAMOND), 1000),
    EMERALD(new ItemStack(Material.EMERALD), 750),
    LAPIS_LAZULI(new ItemStack(Material.INK_SACK, 1, (short) 4), 400),
    QUARTZ(new ItemStack(Material.QUARTZ), 50),
    REDSTONE(new ItemStack(Material.REDSTONE), 40),
    // Stones
    ANDESITE(new ItemStack(Material.STONE, 1, (short) 5), 55),
    DIORITE(new ItemStack(Material.STONE, 1, (short) 3), 110),
    GRANITE(new ItemStack(Material.STONE, 1, (short) 1), 160),
    GRAVEL(new ItemStack(Material.GRAVEL), 2.5),
    OBSIDIAN(new ItemStack(Material.OBSIDIAN), 100),
    STONE(new ItemStack(Material.STONE), 5), // Cobblestone, Stone, Smooth bricks
    // FARMING
    // Animals
    CHICKEN(new ItemStack(Material.EGG), 15), // EGGS, CHICKEN
    COW(new ItemStack(Material.MILK_BUCKET), 25), // LEATHER, BEEF, MILK
    HORSE(new ItemStack(Material.SADDLE), 100),
    PIG(new ItemStack(Material.GRILLED_PORK), 20), // PORK
    RABBIT(new ItemStack(Material.RABBIT_FOOT), 10), // RABBIT, HIDE
    SHEEP(new ItemStack(Material.WOOL), 20), // MUTTON, WOOL
    // Plants
    APPLE(new ItemStack(Material.APPLE), 4.5),
    BEETROOT(new ItemStack(Material.BEETROOT), 3.75),
    CARROT(new ItemStack(Material.CARROT_ITEM), 2.25),
    CHORUS(new ItemStack(Material.CHORUS_FRUIT), 120),
    COCOA(new ItemStack(Material.COCOA), 50),
    MELON(new ItemStack(Material.MELON), 4),
    POTATO(new ItemStack(Material.POTATO_ITEM), 2),
    PUMPKIN(new ItemStack(Material.JACK_O_LANTERN), 8),
    SUGAR(new ItemStack(Material.SUGAR_CANE), 15),
    WHEAT(new ItemStack(Material.WHEAT), 0.5),
    // WOODS
    ACACIA(new ItemStack(Material.LOG_2), 12.5),
    BIRCH(new ItemStack(Material.LOG, 1, (short) 2), 5),
    DARK_OAK(new ItemStack(Material.LOG_2, 1, (short) 1), 15),
    JUNGLE(new ItemStack(Material.LOG, 1, (short) 3), 10),
    OAK(new ItemStack(Material.LOG), 3),
    SPRUCE(new ItemStack(Material.LOG, 1, (short) 1), 4),
    PAPER(new ItemStack(Material.PAPER), 1.5),
    MUSHROOMS(new ItemStack(Material.BROWN_MUSHROOM), 5),
    // SEA
    // Sea animals
    CODFISH(new ItemStack(Material.RAW_FISH), 2.5),
    CLOWNFISH(new ItemStack(Material.RAW_FISH, 1, (short) 2), 50),
    PUFFERFISH(new ItemStack(Material.RAW_FISH, 1, (short) 3), 75),
    SALMON(new ItemStack(Material.RAW_FISH, 1, (short) 1), 10),
    INK(new ItemStack(Material.INK_SACK), 17.5),
    SALT(new ItemStack(Material.SUGAR), 40),
    WATER(ItemUtil.GUI_WATER_BOTTLE, 0.1),
    // Seabed
    CLAY(new ItemStack(Material.CLAY_BRICK), 15),
    PRISMARINE(new ItemStack(Material.PRISMARINE_SHARD), 150),
    // MAGIC
    DRAGON_BREATH(new ItemStack(Material.DRAGONS_BREATH), 2500),
    EXPERIENCE((new ItemStack(Material.EXP_BOTTLE)), 50),
    NETHER_WART(new ItemStack(Material.NETHER_WARTS), 10),
    PURPUR(new ItemStack(Material.PURPUR_BLOCK), 60),
    // DESERT
    CACTUS(new ItemStack(Material.CACTUS), 8),
    GLASS(new ItemStack(Material.GLASS), 12),
    RED_SAND_STONE(new ItemStack(Material.RED_SANDSTONE), 3),
    YELLOW_SAND_STONE(new ItemStack(Material.SANDSTONE), 2),
    // CITY
    CRAFT(new ItemStack(Material.WORKBENCH), 100),
    MANPOWER(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), 4),
    TAXES(new ItemStack(Material.GOLD_NUGGET), 1);

    private ItemStack icon;
    private double value;
    private FMessage name;

    Resource(ItemStack icon, double value) {
        this.icon = icon;
        this.value = value;
    }

    /**
     * @return
     * the inventory GUI icon
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * @return
     * the value of the resource
     */
    public double getValue() {
        return value;
    }

    /**
     * @return
     * the name of the resource
     */
    public String getName() {
        return toString();//name.getMessage();
    }

    /**
     * @return
     * if the resource is physical.
     * Non-physical resources are taxes and manpower
     */
    public boolean isPhysical() {
        return this != MANPOWER && this != TAXES;
    }

    /**
     * @param icon
     * the icon to check
     * @return
     * the resource that is represented by this icon
     */
    public static Resource getByIcon(ItemStack icon) {
        for (Resource resource : values()) {
            if (icon.getType() == resource.getIcon().getType()) {
                return resource;
            }
        }
        return null;
    }

    /**
     * @param name
     * the name to check
     * @return
     * the resource that has this name
     */
    public static Resource getByName(String name) {
        for (Resource resource : values()) {
            if (resource.getName().equals(name)) {
                return resource;
            }
        }
        return null;
    }

}
