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
import io.github.dre2n.factionsxl.faction.Federation;
import io.github.dre2n.factionsxl.faction.LegalEntity;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.OfflinePlayer;

/**
 * @author Daniel Saukel
 */
public class WarParty {

    private LegalEntity leader;
    private Set<LegalEntity> participants = new HashSet<>();
    private int kills;
    private int fights;

    public WarParty(LegalEntity entity) {
        leader = entity;
    }

    /**
     * @return
     * the party leader
     */
    public LegalEntity getLeader() {
        return leader;
    }

    /**
     * @return
     * the player who leads the party leader
     */
    public OfflinePlayer getLeaderAdmin() {
        return leader.getAdmin();
    }

    /**
     * @return
     * a Set of all federations and factions that participate
     */
    public Set<LegalEntity> getParticipants() {
        return participants;
    }

    /**
     * @return
     * a Set of all single factions and the factions of the federations that participate
     */
    public Set<Faction> getFactions() {
        Set<Faction> factions = new HashSet<>();
        for (LegalEntity entity : participants) {
            if (entity instanceof Faction) {
                factions.add((Faction) entity);
            } else if (entity instanceof Federation) {
                factions.addAll(((Federation) entity).getFactions());
            }
        }
        return factions;
    }

    public void addParticipant(LegalEntity participant) {
        participants.add(participant);
    }

}
