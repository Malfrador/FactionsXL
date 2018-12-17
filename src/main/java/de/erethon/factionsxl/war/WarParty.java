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
package de.erethon.factionsxl.war;

import de.erethon.commons.player.PlayerCollection;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.entity.FEntity;
import de.erethon.factionsxl.entity.Relation;
import de.erethon.factionsxl.entity.Request;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.faction.FactionCache;
import de.erethon.factionsxl.faction.Federation;
import de.erethon.factionsxl.faction.LegalEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.OfflinePlayer;

/**
 * @author Daniel Saukel
 */
public class WarParty implements FEntity {

    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();

    private LegalEntity leader;
    private Set<LegalEntity> participants = new HashSet<>();
    private List<Request> requests;
    private WarPartyRole role;
    public int kills;
    public int deaths;
    public int fights;

    public WarParty(LegalEntity entity, WarPartyRole role) {
        leader = entity;
        participants.add(entity);
        requests = new ArrayList<>();
        this.role = role;
    }

    public WarParty(Map<String, Object> serialized) {
        leader = factions.getById((int) serialized.get("leader"));
        ((List<Integer>) serialized.get("participants")).forEach(p -> participants.add(factions.getById(p)));
        requests = (List<Request>) serialized.get("requests");
        kills = (int) serialized.get("kills");
        deaths = (int) serialized.get("deaths");
        fights = (int) serialized.get("fights");
        role = WarPartyRole.valueOf((String) serialized.get("role"));
    }

    @Override
    public String getName() {
        return leader.getName();
    }

    @Override
    public Relation getRelation(FEntity object) {
        return leader.getRelation(object);
    }

    @Override
    public boolean isInWar(FEntity object) {
        return leader.isInWar(object);
    }

    @Override
    public PlayerCollection getRequestAuthorizedPlayers(Class<? extends Request> type) {
        return leader.getRequestAuthorizedPlayers(type);
    }

    @Override
    public List<Request> getRequests() {
        return requests;
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

    /**
     * @return
     * kills / deaths ratio. 0 deaths are treated like 1.
     */
    public double getKD() {
        if (deaths != 0) {
            return kills / deaths;
        }
        return kills;
    }

    /**
     * @return
     * the amount of collected war points
     */
    public int getPoints() {
        return 0;
    }

    /**
     * @return
     * the WarParty's role in the war
     */
    public WarPartyRole getRole() {
        return role;
    }

    public War getWar() {
        return plugin.getWarCache().getByParty(this);
    }

    /**
     * Returns the defender if this party is the attacker; the attacker if this party is the defender and null if this party is anything else.
     *
     * @return the other party
     */
    public WarParty getEnemy() {
        return getWar().getEnemy(this);
    }

    /* Serialization */
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("leader", leader.getId());
        ArrayList<Integer> participants = new ArrayList<>();
        this.participants.forEach(p -> participants.add(p.getId()));
        serialized.put("participants", participants);
        serialized.put("kills", kills);
        serialized.put("deaths", this.deaths);
        serialized.put("fights", fights);
        serialized.put("role", role.name());
        return serialized;
    }

    @Override
    public String toString() {
        return "WarParty{leader=" + leader.toString() + "}";
    }

}
