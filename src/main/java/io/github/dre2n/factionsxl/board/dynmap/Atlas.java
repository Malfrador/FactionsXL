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
package io.github.dre2n.factionsxl.board.dynmap;

import io.github.dre2n.factionsxl.FactionsXL;

/**
 * @author Daniel Saukel
 */
public class Atlas {

    public FactionMap factionMap;
    public RegionMap regionMap;

    public Atlas(FactionsXL plugin) {
        factionMap = new FactionMap(plugin);
        regionMap = new RegionMap(plugin);
    }

    public void init() {
        factionMap.init();
        regionMap.init();
    }

}
