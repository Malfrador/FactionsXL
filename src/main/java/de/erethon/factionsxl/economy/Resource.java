/*
 * Copyright (c) 2017-2019 Daniel Saukel
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
package de.erethon.factionsxl.economy;

import de.erethon.commons.misc.EnumUtil;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.util.GUIButton;
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
    COAL(FMessage.RESOURCE_COAL, Material.COAL, 20),
    SULPHUR(FMessage.RESOURCE_SULPHUR, Material.GUNPOWDER, 25),
    GOLD(FMessage.RESOURCE_GOLD, Material.GOLD_INGOT, 800),
    IRON(FMessage.RESOURCE_IRON, Material.IRON_INGOT, 75),
    DIAMOND(FMessage.RESOURCE_DIAMOND, Material.DIAMOND, 1000),
    EMERALD(FMessage.RESOURCE_EMERALD, Material.EMERALD, 750),
    LAPIS_LAZULI(FMessage.RESOURCE_LAPIS_LAZULI, Material.LAPIS_LAZULI, 400),
    QUARTZ(FMessage.RESOURCE_QUARTZ, Material.QUARTZ, 50),
    REDSTONE(FMessage.RESOURCE_REDSTONE, Material.REDSTONE, 40),
    // Stones
    ANDESITE(FMessage.RESOURCE_ANDESITE, Material.ANDESITE, 55),
    DIORITE(FMessage.RESOURCE_DIORITE, Material.DIORITE, 110),
    GRANITE(FMessage.RESOURCE_GRANITE, Material.GRANITE, 160),
    GRAVEL(FMessage.RESOURCE_GRAVEL, Material.GRAVEL, 2.5),
    OBSIDIAN(FMessage.RESOURCE_OBSIDIAN, Material.OBSIDIAN, 100),
    STONE(FMessage.RESOURCE_STONE, Material.STONE, 5), // Cobblestone, Stone, Smooth bricks
    // FARMING
    // Animals
    CHICKEN(FMessage.RESOURCE_CHICKEN, Material.EGG, 15), // EGGS, CHICKEN
    COW(FMessage.RESOURCE_COW, Material.MILK_BUCKET, 25), // LEATHER, BEEF, MILK
    HORSE(FMessage.RESOURCE_HORSE, Material.SADDLE, 100),
    PIG(FMessage.RESOURCE_PIG, Material.COOKED_PORKCHOP, 20), // PORK
    RABBIT(FMessage.RESOURCE_RABBIT, Material.RABBIT_FOOT, 10), // RABBIT, HIDE
    SHEEP(FMessage.RESOURCE_SHEEP, Material.WHITE_WOOL, 20), // MUTTON, WOOL
    // Plants
    APPLE(FMessage.RESOURCE_APPLE, Material.APPLE, 4.5),
    BEETROOT(FMessage.RESOURCE_BEETROOT, Material.BEETROOT, 3.75),
    CARROT(FMessage.RESOURCE_CARROT, Material.CARROT, 2.25),
    CHORUS(FMessage.RESOURCE_CHORUS, Material.CHORUS_FRUIT, 120),
    COCOA(FMessage.RESOURCE_COCOA, Material.COCOA, 50),
    MELON(FMessage.RESOURCE_MELON, Material.MELON, 4),
    POTATO(FMessage.RESOURCE_POTATO, Material.POTATO, 2),
    PUMPKIN(FMessage.RESOURCE_PUMPKIN, Material.JACK_O_LANTERN, 8),
    SUGAR(FMessage.RESOURCE_SUGAR, Material.SUGAR_CANE, 15),
    WHEAT(FMessage.RESOURCE_WHEAT, Material.WHEAT, 0.5),
    // WOODS
    ACACIA(FMessage.RESOURCE_ACACIA, Material.ACACIA_LOG, 12.5),
    BIRCH(FMessage.RESOURCE_BIRCH, Material.BIRCH_LOG, 5),
    DARK_OAK(FMessage.RESOURCE_DARK_OAK, Material.DARK_OAK_LOG, 15),
    JUNGLE(FMessage.RESOURCE_JUNGLE, Material.JUNGLE_LOG, 10),
    OAK(FMessage.RESOURCE_OAK, Material.OAK_LOG, 3),
    SPRUCE(FMessage.RESOURCE_SPRUCE, Material.SPRUCE_LOG, 4),
    PAPER(FMessage.RESOURCE_PAPER, Material.PAPER, 1.5),
    MUSHROOMS(FMessage.RESOURCE_MUSHROOMS, Material.BROWN_MUSHROOM, 5),
    // SEA
    // Sea animals
    CODFISH(FMessage.RESOURCE_CODFISH, Material.COD, 2.5),
    CLOWNFISH(FMessage.RESOURCE_CLOWNFISH, Material.TROPICAL_FISH, 50),
    PUFFERFISH(FMessage.RESOURCE_PUFFERFISH, Material.PUFFERFISH, 75),
    SALMON(FMessage.RESOURCE_SALMON, Material.SALMON, 10),
    INK(FMessage.RESOURCE_INK, Material.INK_SAC, 17.5),
    SALT(FMessage.RESOURCE_SALT, Material.SUGAR, 40),
    WATER(FMessage.RESOURCE_WATER, Material.POTION, 0.1),
    // Seabed
    CLAY(FMessage.RESOURCE_CLAY, Material.CLAY, 15),
    PRISMARINE(FMessage.RESOURCE_PRISMARINE, Material.PRISMARINE_SHARD, 150),
    // MAGIC
    DRAGON_BREATH(FMessage.RESOURCE_DRAGON_BREATH, Material.DRAGON_BREATH, 2500),
    EXPERIENCE(FMessage.RESOURCE_EXPERIENCE, Material.EXPERIENCE_BOTTLE, 50),
    NETHER_WART(FMessage.RESOURCE_NETHER_WART, Material.NETHER_WART, 10),
    PURPUR(FMessage.RESOURCE_PURPUR, Material.PURPUR_BLOCK, 60),
    // DESERT
    CACTUS(FMessage.RESOURCE_CACTUS, Material.CACTUS, 8),
    GLASS(FMessage.RESOURCE_GLASS, Material.GLASS, 12),
    RED_SANDSTONE(FMessage.RESOURCE_RED_SANDSTONE, Material.RED_SANDSTONE, 3),
    YELLOW_SANDSTONE(FMessage.RESOURCE_YELLOW_SANDSTONE, Material.SANDSTONE, 2),
    // CITY
    CRAFT(FMessage.RESOURCE_CRAFT, Material.CRAFTING_TABLE, 100),
    MANPOWER(FMessage.RESOURCE_MANPOWER, Material.PLAYER_HEAD, 4),
    TAXES(FMessage.RESOURCE_TAXES, Material.GOLD_NUGGET, 1);

    private FMessage name;
    private Material icon;
    private double value;
    private double requiredAmountModifier;

    Resource(FMessage name, Material icon, double value) {
        this.name = name;
        this.icon = icon;
        this.value = value;
        this.requiredAmountModifier = 1.0;
    }

    Resource(FMessage name, Material icon, double value, double requiredAmountModifier) {
        this.name = name;
        this.icon = icon;
        this.value = value;
        this.requiredAmountModifier = requiredAmountModifier;
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
     * the type of the inventory GUI icon
     */
    public Material getIconType() {
        return icon;
    }

    /**
     * @return
     * the inventory GUI icon
     */
    public ItemStack getIcon() {
        return this == WATER ? GUIButton.GUI_WATER_BOTTLE.clone() : new ItemStack(icon);
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
     * a modifier that determines relatively how much of this resource people need
     */
    public double getRequiredAmountModifier() {
        return requiredAmountModifier;
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
            if (icon.getType() == resource.icon) {
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

    /**
     * Loads the required amount modifiers
     */
    public static void loadRequiredAmountModifiers(Map<String, Object> modifiers) {
        for (Entry<String, Object> modifier : modifiers.entrySet()) {
            if (!EnumUtil.isValidEnum(Resource.class, modifier.getKey())) {
                continue;
            }
            valueOf(modifier.getKey()).requiredAmountModifier = (double) modifier.getValue();
        }
    }

}
