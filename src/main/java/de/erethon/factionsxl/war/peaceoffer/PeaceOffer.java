/*
 * Copyright (C) 2017-2018 Daniel Saukel
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
package de.erethon.factionsxl.war.peaceoffer;

import de.erethon.factionsxl.entity.Request;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.demand.ItemDemand;
import de.erethon.factionsxl.war.demand.WarDemand;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Daniel Saukel
 */
public abstract class PeaceOffer extends Request {

    private static Set<Class<? extends WarDemand>> types = new HashSet<>();

    static {
        types.add(ItemDemand.class);
    }

    /**
     * A Set of all types that can be expanded
     *
     * @return
     * all types
     */
    public static Set<Class<? extends WarDemand>> getDemandTypes() {
        return types;
    }

    protected War war;
    protected List<WarDemand> demands;

    /**
     * The war that the two parties fight
     *
     * @return
     * the war
     */
    public War getWar() {
        return war;
    }

    /**
     * @return
     * the demands
     */
    public List<WarDemand> getDemands() {
        return demands;
    }

}
