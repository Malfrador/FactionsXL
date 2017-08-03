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
package io.github.dre2n.factionsxl.war;

import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.LegalEntity;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel Saukel
 */
public class WarCache {

    private Set<War> wars = new HashSet<>();

    public WarCache(File dir) {
        assert dir.isDirectory();
        for (File file : dir.listFiles()) {
            wars.add(new War(file));
        }
    }

    /* Getters and setters */
    /**
     * @return
     * all wars
     */
    public Set<War> getWars() {
        return wars;
    }

    /**
     * @param faction
     * the faction to check
     * @return
     * all wars in that the faction takes part
     */
    public Set<War> getByFaction(Faction faction) {
        Set<War> fWars = new HashSet<>();
        for (War war : wars) {
            if (war.getAttacker().getFactions().contains(faction) || war.getDefender().getFactions().contains(faction)) {
                fWars.add(war);
            }
        }
        return fWars;
    }

    /**
     * @param party
     * the WarParty to check
     * @return
     * all wars in that the faction takes part
     */
    public War getByParty(WarParty party) {
        for (War war : wars) {
            if (war.getAttacker().equals(party) || war.getDefender().equals(party)) {
                return war;
            }
        }
        return null;
    }

    /**
     * @param attacker
     * the attacker to check
     * @param defender
     * the defender to check
     * @return
     * all wars in that the faction takes part
     */
    public War getByPartyLeaders(LegalEntity attacker, LegalEntity defender) {
        for (War war : wars) {
            if (war.getAttacker().getLeader().equals(attacker) && war.getDefender().getLeader().equals(defender)) {
                return war;
            }
        }
        return null;
    }

}
