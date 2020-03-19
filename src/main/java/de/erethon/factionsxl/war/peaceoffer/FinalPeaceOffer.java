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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.entity.Relation;
import de.erethon.factionsxl.entity.RelationRequest;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.faction.Federation;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.WarPartyRole;
import de.erethon.factionsxl.war.demand.WarDemand;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class FinalPeaceOffer extends PeaceOffer {

    public FinalPeaceOffer(War war, WarParty demanding, WarParty target, WarDemand... demands) {
        this.war = war;
        subject = demanding;
        object = target;
        this.demands = new ArrayList(Arrays.asList(demands));
    }

    public FinalPeaceOffer(Map<String, Object> args) {
        war = FactionsXL.getInstance().getWarCache().getByDate((long) args.get("war"));
        subject = WarPartyRole.valueOf((String) args.get("subject")) == WarPartyRole.ATTACKER ? war.getAttacker() : war.getDefender();
        object = getSubject().getRole() == WarPartyRole.ATTACKER ? war.getDefender() : war.getAttacker();
        demands = (List<WarDemand>) args.get("demands");
        expiration = (long) args.get("expiration");
    }

    @Override
    public WarParty getSubject() {
        return (WarParty) subject;
    }

    @Override
    public WarParty getObject() {
        return (WarParty) object;
    }

    @Override
    public void confirm() {
        Bukkit.broadcastMessage("Confirm triggered");
        boolean canPay = true;
        for (WarDemand demand : demands) {
            if (!demand.canPay(getObject())) {
                canPay = false;
            }
        }
        if (canPay) {
            demands.forEach(d -> d.pay(getSubject(), getObject()));
        } else {
            new RelationRequest(Bukkit.getConsoleSender(), (Faction) getSubject().getLeader(), (Faction) getObject().getLeader(), Relation.VASSAL).confirm();
            // TODO: Might break after government update
            // TODO: Add time modifier
        }
    }

    @Override
    public String getAcceptCommand() {
        war.end();
        return "/factionsxl relation " + getObject().getName() + " " + getSubject().getName() + " " + "peace";
    }

    @Override
    public String getDenyCommand() {
        return getAcceptCommand() + "-deny";
    }

    @Override
    public void sendSubjectMessage() {
        new RelationRequest(Bukkit.getConsoleSender(), (Faction) getSubject().getLeader(), (Faction) getObject().getLeader(), Relation.PEACE);
    }

    @Override
    public void sendObjectMessage() {
        new RelationRequest(Bukkit.getConsoleSender(), (Faction) getObject().getLeader(), (Faction) getSubject().getLeader(), Relation.PEACE);
    }

    @Override
    public ItemStack getButton(Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> args = new HashMap<>();
        args.put("war", war.getStartDate().getTime());
        args.put("subject", getSubject().getRole().name());
        args.put("object", getObject().getRole().name());
        args.put("demands", demands);
        args.put("expiration", expiration);
        return args;
    }

}
