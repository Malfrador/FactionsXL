/*
 * Copyright (c) 2017-2018 Daniel Saukel
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

import de.erethon.factionsxl.config.FMessage;
import static de.erethon.factionsxl.economy.Resource.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public enum ResourceSubcategory {

    // BUILDING
    WOOD(FMessage.RESOURCE_SUBCATEGORY_WOOD, OAK.getIcon(), true, ACACIA, BIRCH, DARK_OAK, JUNGLE, OAK, SPRUCE),
    GLASS(FMessage.RESOURCE_SUBCATEGORY_GLASS, Resource.GLASS.getIcon(), false, Resource.GLASS, QUARTZ, OBSIDIAN),
    ROADS(FMessage.RESOURCE_SUBCATEGORY_ROADS, GRAVEL.getIcon(), true, GRAVEL, Resource.STONE),
    STONE(FMessage.RESOURCE_SUBCATEGORY_STONE, Resource.STONE.getIcon(), false, ANDESITE, DIORITE, GRANITE, Resource.STONE, PURPUR, RED_SANDSTONE, YELLOW_SANDSTONE),
    // CLOTHES
    DYE(FMessage.RESOURCE_SUBCATEGORY_DYE, LAPIS_LAZULI.getIcon(), false, LAPIS_LAZULI, BEETROOT, INK, CACTUS),
    FUR(FMessage.RESOURCE_SUBCATEGORY_FUR, SHEEP.getIcon(), false, RABBIT, SHEEP),
    LEATHER(FMessage.RESOURCE_SUBCATEGORY_LEATHER, new ItemStack(Material.LEATHER), true, COW, HORSE, PIG, SHEEP),
    // FOOD
    MEAT(FMessage.RESOURCE_SUBCATEGORY_MEAT, PUFFERFISH.getIcon(), false, CHICKEN, COW, HORSE, PIG, RABBIT, SHEEP, CLOWNFISH, PUFFERFISH),
    STAPLES(FMessage.RESOURCE_SUBCATEGORY_STAPLES, WHEAT.getIcon(), true, APPLE, BEETROOT, CARROT, MELON, POTATO, PUMPKIN, WHEAT, MUSHROOMS, CODFISH, SALMON),
    SPICES(FMessage.RESOURCE_SUBCATEGORY_SPICES, SALT.getIcon(), false, COCOA, SUGAR, SALT, NETHER_WART),
    WATER(FMessage.RESOURCE_SUBCATEGORY_WATER, Resource.WATER.getIcon(), true, Resource.WATER),
    // HEATING
    HEATING(FMessage.RESOURCE_SUBCATEGORY_HEATING, COAL.getIcon(), true, COAL, ACACIA, BIRCH, DARK_OAK, JUNGLE, OAK, SPRUCE),
    // JEWELRY
    JEWELRY(FMessage.RESOURCE_SUBCATEGORY_JEWELRY, DIAMOND.getIcon(), false, GOLD, DIAMOND, EMERALD, LAPIS_LAZULI, QUARTZ, REDSTONE, OBSIDIAN, PRISMARINE),
    // MILITARY
    SMITHERY(FMessage.RESOURCE_SUBCATEGORY_SMITHERY, IRON.getIcon(), false, GOLD, IRON),
    CANNONING(FMessage.RESOURCE_SUBCATEGORY_CANNONING, SULPHUR.getIcon(), false, IRON, SULPHUR),
    RIDERS(FMessage.RESOURCE_SUBCATEGORY_RIDERS, HORSE.getIcon(), false, HORSE),
    // SCIENCE
    ALCHEMY(FMessage.RESOURCE_SUBCATEGORY_ALCHEMY, REDSTONE.getIcon(), false, SULPHUR, GOLD, REDSTONE, Resource.WATER),
    LITERATURE(FMessage.RESOURCE_SUBCATEGORY_LITERATURE, new ItemStack(Material.BOOK_AND_QUILL), false, INK, PAPER);

    private FMessage name;
    private ItemStack icon;
    private boolean basic;
    private Resource[] resources;

    ResourceSubcategory(FMessage name, ItemStack icon, boolean basic, Resource... resources) {
        this.name = name;
        this.icon = icon;
        this.basic = basic;
        this.resources = resources;
    }

    public ItemStack getIcon() {
        return icon.clone();
    }

    public String getName() {
        return name.getMessage();
    }

    public Resource[] getResources() {
        return resources;
    }

    /**
     * Basic resources are those that the people definitely need in order to stay alive.
     *
     * @return
     * if the resources are basic
     */
    public boolean isBasic() {
        return basic;
    }

    /* Statics */
    /**
     * @param icon
     * the icon to check
     * @return
     * the subcategory that is represented by this icon
     */
    public static ResourceSubcategory getByIcon(ItemStack icon) {
        for (ResourceSubcategory category : values()) {
            if (icon.getType() == category.getIcon().getType() && icon.getDurability() == category.getIcon().getDurability()) {
                return category;
            }
        }
        return null;
    }

}
