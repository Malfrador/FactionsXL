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

import static io.github.dre2n.factionsxl.economy.Resource.*;

/**
 * @author Daniel Saukel
 */
public enum ResourceSubcategory {

    // BUILDING
    WOOD(ACACIA, BIRCH, DARK_OAK, JUNGLE, OAK, SPRUCE),
    GLASS(Resource.GLASS, QUARTZ, OBSIDIAN),
    ROADS(GRAVEL, Resource.STONE),
    STONE(ANDESITE, DIORITE, GRANITE, Resource.STONE, PURPUR, RED_SAND_STONE, YELLOW_SAND_STONE),
    // CLOTHES
    DYE(LAPIS_LAZULI, BEETROOT, INK, CACTUS),
    FUR(RABBIT, SHEEP),
    LEATHER(COW, HORSE, PIG, SHEEP),
    // FOOD
    MEAT(CHICKEN, COW, HORSE, PIG, RABBIT, SHEEP, CLOWNFISH, PUFFERFISH),
    STAPLES(APPLE, BEETROOT, CARROT, MELON, POTATO, PUMPKIN, WHEAT, MUSHROOMS, CODFISH, SALMON),
    SPICES(COCOA, SUGAR, SALT, NETHER_WART),
    WATER(Resource.WATER),
    // HEATING
    HEATING(COAL, ACACIA, BIRCH, DARK_OAK, JUNGLE, OAK, SPRUCE),
    // JEWELRY
    JEWELRY(GOLD, DIAMOND, EMERALD, LAPIS_LAZULI, QUARTZ, REDSTONE, OBSIDIAN, PRISMARINE),
    // MILITARY
    SMITHERY(GOLD, IRON),
    CANNONING(IRON, SULPHUR),
    RIDERS(HORSE),
    // SCIENCE
    ALCHEMY(SULPHUR, GOLD, REDSTONE, Resource.WATER),
    LITERATURE(INK, PAPER);

    private Resource[] resources;

    ResourceSubcategory(Resource... resources) {
        this.resources = resources;
    }

    public Resource[] getResources() {
        return resources;
    }

}
