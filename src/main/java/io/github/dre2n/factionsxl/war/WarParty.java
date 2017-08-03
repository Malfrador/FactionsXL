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

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.faction.Federation;
import io.github.dre2n.factionsxl.faction.LegalEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Daniel Saukel
 */
public class WarParty {

    FactionCache factions = FactionsXL.getInstance().getFactionCache();

    private LegalEntity leader;
    private Set<LegalEntity> participants = new HashSet<>();
    private int kills;
    private int fights;

    public WarParty(LegalEntity entity) {
        leader = entity;
    }

    public WarParty(ConfigurationSection config) {
        leader = factions.getById(config.getInt("leader"));
        config.getList("participants").forEach(p -> participants.add(factions.getById((Integer) p)));
        kills = config.getInt("kills");
        fights = config.getInt("fights");
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

    /* Serialization */
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("leader", leader.getId());
        ArrayList<Integer> participants = new ArrayList<>();
        this.participants.forEach(p->participants.add(p.getId()));
        serialized.put("partcipants", participants);
        serialized.put("kills", kills);
        serialized.put("fights", fights);
        return serialized;
    }

}
