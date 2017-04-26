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

import io.github.dre2n.commons.util.EnumUtil;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.util.PageGUI;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public enum Resource {

    // MINES
    // Resource
    COAL(FMessage.RESOURCE_COAL, new ItemStack(Material.COAL), 20),
    SULPHUR(FMessage.RESOURCE_SULPHUR, new ItemStack(Material.SULPHUR), 25),
    GOLD(FMessage.RESOURCE_GOLD, new ItemStack(Material.GOLD_INGOT), 800),
    IRON(FMessage.RESOURCE_IRON, new ItemStack(Material.IRON_INGOT), 75),
    DIAMOND(FMessage.RESOURCE_DIAMOND, new ItemStack(Material.DIAMOND), 1000),
    EMERALD(FMessage.RESOURCE_EMERALD, new ItemStack(Material.EMERALD), 750),
    LAPIS_LAZULI(FMessage.RESOURCE_LAPIS_LAZULI, new ItemStack(Material.INK_SACK, 1, (short) 4), 400),
    QUARTZ(FMessage.RESOURCE_QUARTZ, new ItemStack(Material.QUARTZ), 50),
    REDSTONE(FMessage.RESOURCE_REDSTONE, new ItemStack(Material.REDSTONE), 40),
    // Stones
    ANDESITE(FMessage.RESOURCE_ANDESITE, new ItemStack(Material.STONE, 1, (short) 5), 55),
    DIORITE(FMessage.RESOURCE_DIORITE, new ItemStack(Material.STONE, 1, (short) 3), 110),
    GRANITE(FMessage.RESOURCE_GRANITE, new ItemStack(Material.STONE, 1, (short) 1), 160),
    GRAVEL(FMessage.RESOURCE_GRAVEL, new ItemStack(Material.GRAVEL), 2.5),
    OBSIDIAN(FMessage.RESOURCE_OBSIDIAN, new ItemStack(Material.OBSIDIAN), 100),
    STONE(FMessage.RESOURCE_STONE, new ItemStack(Material.STONE), 5), // Cobblestone, Stone, Smooth bricks
    // FARMING
    // Animals
    CHICKEN(FMessage.RESOURCE_CHICKEN, new ItemStack(Material.EGG), 15), // EGGS, CHICKEN
    COW(FMessage.RESOURCE_COW, new ItemStack(Material.MILK_BUCKET), 25), // LEATHER, BEEF, MILK
    HORSE(FMessage.RESOURCE_HORSE, new ItemStack(Material.SADDLE), 100),
    PIG(FMessage.RESOURCE_PIG, new ItemStack(Material.GRILLED_PORK), 20), // PORK
    RABBIT(FMessage.RESOURCE_RABBIT, new ItemStack(Material.RABBIT_FOOT), 10), // RABBIT, HIDE
    SHEEP(FMessage.RESOURCE_SHEEP, new ItemStack(Material.WOOL), 20), // MUTTON, WOOL
    // Plants
    APPLE(FMessage.RESOURCE_APPLE, new ItemStack(Material.APPLE), 4.5),
    BEETROOT(FMessage.RESOURCE_BEETROOT, new ItemStack(Material.BEETROOT), 3.75),
    CARROT(FMessage.RESOURCE_CARROT, new ItemStack(Material.CARROT_ITEM), 2.25),
    CHORUS(FMessage.RESOURCE_CHORUS, new ItemStack(Material.CHORUS_FRUIT), 120),
    COCOA(FMessage.RESOURCE_COCOA, new ItemStack(Material.COCOA), 50),
    MELON(FMessage.RESOURCE_MELON, new ItemStack(Material.MELON), 4),
    POTATO(FMessage.RESOURCE_POTATO, new ItemStack(Material.POTATO_ITEM), 2),
    PUMPKIN(FMessage.RESOURCE_PUMPKIN, new ItemStack(Material.JACK_O_LANTERN), 8),
    SUGAR(FMessage.RESOURCE_SUGAR, new ItemStack(Material.SUGAR_CANE), 15),
    WHEAT(FMessage.RESOURCE_WHEAT, new ItemStack(Material.WHEAT), 0.5),
    // WOODS
    ACACIA(FMessage.RESOURCE_ACACIA, new ItemStack(Material.LOG_2), 12.5),
    BIRCH(FMessage.RESOURCE_BIRCH, new ItemStack(Material.LOG, 1, (short) 2), 5),
    DARK_OAK(FMessage.RESOURCE_DARK_OAK, new ItemStack(Material.LOG_2, 1, (short) 1), 15),
    JUNGLE(FMessage.RESOURCE_JUNGLE, new ItemStack(Material.LOG, 1, (short) 3), 10),
    OAK(FMessage.RESOURCE_OAK, new ItemStack(Material.LOG), 3),
    SPRUCE(FMessage.RESOURCE_SPRUCE, new ItemStack(Material.LOG, 1, (short) 1), 4),
    PAPER(FMessage.RESOURCE_PAPER, new ItemStack(Material.PAPER), 1.5),
    MUSHROOMS(FMessage.RESOURCE_MUSHROOMS, new ItemStack(Material.BROWN_MUSHROOM), 5),
    // SEA
    // Sea animals
    CODFISH(FMessage.RESOURCE_CODFISH, new ItemStack(Material.RAW_FISH), 2.5),
    CLOWNFISH(FMessage.RESOURCE_CLOWNFISH, new ItemStack(Material.RAW_FISH, 1, (short) 2), 50),
    PUFFERFISH(FMessage.RESOURCE_PUFFERFISH, new ItemStack(Material.RAW_FISH, 1, (short) 3), 75),
    SALMON(FMessage.RESOURCE_SALMON, new ItemStack(Material.RAW_FISH, 1, (short) 1), 10),
    INK(FMessage.RESOURCE_INK, new ItemStack(Material.INK_SACK), 17.5),
    SALT(FMessage.RESOURCE_SALT, new ItemStack(Material.SUGAR), 40),
    WATER(FMessage.RESOURCE_WATER, PageGUI.GUI_WATER_BOTTLE.clone(), 0.1),
    // Seabed
    CLAY(FMessage.RESOURCE_CLAY, new ItemStack(Material.CLAY_BRICK), 15),
    PRISMARINE(FMessage.RESOURCE_PRISMARINE, new ItemStack(Material.PRISMARINE_SHARD), 150),
    // MAGIC
    DRAGON_BREATH(FMessage.RESOURCE_DRAGON_BREATH, new ItemStack(Material.DRAGONS_BREATH), 2500),
    EXPERIENCE(FMessage.RESOURCE_EXPERIENCE, new ItemStack(Material.EXP_BOTTLE), 50),
    NETHER_WART(FMessage.RESOURCE_NETHER_WART, new ItemStack(Material.NETHER_WARTS), 10),
    PURPUR(FMessage.RESOURCE_PURPUR, new ItemStack(Material.PURPUR_BLOCK), 60),
    // DESERT
    CACTUS(FMessage.RESOURCE_CACTUS, new ItemStack(Material.CACTUS), 8),
    GLASS(FMessage.RESOURCE_GLASS, new ItemStack(Material.GLASS), 12),
    RED_SANDSTONE(FMessage.RESOURCE_RED_SANDSTONE, new ItemStack(Material.RED_SANDSTONE), 3),
    YELLOW_SANDSTONE(FMessage.RESOURCE_YELLOW_SANDSTONE, new ItemStack(Material.SANDSTONE), 2),
    // CITY
    CRAFT(FMessage.RESOURCE_CRAFT, new ItemStack(Material.WORKBENCH), 100),
    MANPOWER(FMessage.RESOURCE_MANPOWER, new ItemStack(Material.SKULL_ITEM, 1, (short) 3), 4),
    TAXES(FMessage.RESOURCE_TAXES, new ItemStack(Material.GOLD_NUGGET), 1);

    private ItemStack icon;
    private double value;
    private FMessage name;

    Resource(FMessage name, ItemStack icon, double value) {
        this.name = name;
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
        return name.getMessage();
    }

    /**
     * @return
     * if the resource is physical.
     * Non-physical resources are taxes and manpower
     */
    public boolean isPhysical() {
        return this != MANPOWER && this != TAXES;
    }

    /* Statics */
    /**
     * @param icon
     * the icon to check
     * @return
     * the resource that is represented by this icon
     */
    public static Resource getByIcon(ItemStack icon) {
        for (Resource resource : values()) {
            if (icon.getType() == resource.getIcon().getType() && icon.getDurability() == resource.getIcon().getDurability()) {
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
            if (resource.getName().equalsIgnoreCase(name) || resource.name().equalsIgnoreCase(name)) {
                return resource;
            }
        }
        return null;
    }

    /**
     * Loads the resource prices
     */
    public static void loadPrices(Map<String, Object> prices) {
        for (Entry<String, Object> price : prices.entrySet()) {
            if (!EnumUtil.isValidEnum(Resource.class, price.getKey())) {
                continue;
            }
            valueOf(price.getKey()).value = (double) price.getValue();
        }
    }

}
