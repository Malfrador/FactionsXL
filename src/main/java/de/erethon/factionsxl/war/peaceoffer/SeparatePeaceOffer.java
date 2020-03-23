/*
 *
 *  * Copyright (C) 2017-2020 Daniel Saukel, Malfrador
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.erethon.factionsxl.war.peaceoffer;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.faction.LegalEntity;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.demand.WarDemand;

import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class SeparatePeaceOffer extends PeaceOffer {

    public SeparatePeaceOffer(War war, LegalEntity demanding, LegalEntity target, WarDemand... demands) {
        this.war = war;
        subject = demanding;
        object = target;
        this.demands = new ArrayList(Arrays.asList(demands));
    }

    public SeparatePeaceOffer(Map<String, Object> args) {
        war = FactionsXL.getInstance().getWarCache().getByDate((long) args.get("war"));
        subject = null;
        object = null;
        demands = (List<WarDemand>) args.get("demands");
        expiration = (long) args.get("expiration");
    }

    @Override
    public void confirm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ItemStack getButton(Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAcceptCommand() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDenyCommand() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendSubjectMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendObjectMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> args = new HashMap<>();
        args.put("war", war.getStartDate().getTime());
        //args.put("subject", getSubject().getId());
        //args.put("object", getObject().getId());
        args.put("demands", demands);
        args.put("expiration", expiration);
        return args;
    }

}
