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
package io.github.dre2n.factionsxl.war.demand;

import io.github.dre2n.factionsxl.entity.Relation;
import io.github.dre2n.factionsxl.entity.RelationRequest;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.LegalEntity;
import io.github.dre2n.factionsxl.war.War;
import io.github.dre2n.factionsxl.war.WarParty;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;

/**
 * @author Daniel Saukel
 */
public class PeaceOffer {

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

    private War war;
    private WarParty demanding;
    private LegalEntity target;
    private List<WarDemand> demands;

    public PeaceOffer(War war, WarParty demanding, LegalEntity target, WarDemand... demands) {
        this.war = war;
        this.demanding = demanding;
        this.target = target;
        this.demands = Arrays.asList(demands);
    }

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
     * the party that demands
     */
    public WarParty getDemandingParty() {
        return demanding;
    }

    /**
     * @return
     * the target that is supposed to pay
     */
    public LegalEntity getTarget() {
        return target;
    }

    /**
     * @return
     * true if the target is not leader of a war party
     */
    public boolean isSeparatePeace() {
        return !war.getAttacker().getLeader().equals(target) && !war.getDefender().getLeader().equals(target);
    }

    /**
     * @return
     * the demands
     */
    public List<WarDemand> getDemands() {
        return demands;
    }

    /**
     * Ends the war
     */
    public void accept() {
        boolean canPay = true;
        for (WarDemand demand : demands) {
            if (!demand.canPay()) {
                canPay = false;
            }
        }
        if (canPay) {
            demands.forEach(d -> d.pay());
        } else {
            new RelationRequest(Bukkit.getConsoleSender(), (Faction) demanding.getLeader(), (Faction) target, Relation.VASSAL).confirm(); // TODO: Might break after government update
            // TODO: Add time modifier
        }
    }

}
