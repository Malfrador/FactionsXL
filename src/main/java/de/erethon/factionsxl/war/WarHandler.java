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

package de.erethon.factionsxl.war;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.entity.Relation;
import de.erethon.factionsxl.entity.RelationRequest;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.util.ParsingUtil;
import org.bukkit.Bukkit;

import java.util.Date;

/**
 * @author Malfrador
 */

public class WarHandler {
    FactionsXL plugin = FactionsXL.getInstance();
    FConfig config = plugin.getFConfig();
    public void calculateWar() {
        long now = System.currentTimeMillis();
        for (War w : plugin.getWarCache().getWars()) {
            if ((w.getTruce()) && (now > w.getStartDate().getTime() + config.getTruceTime()) ) {
                w.setTruce(false);
                Faction attacker = (Faction) w.getAttacker().getLeader();
                Faction defender = (Faction) w.getDefender().getLeader();
                ParsingUtil.broadcastMessage(FMessage.WAR_TRUCE_ENDED.getMessage(), attacker, defender);
            }
        }
    }
    public void calculateWarStatus() {
        for (War war : plugin.getWarCache().getWars()) {
            // Add exhaustion
            for (Faction f : war.getAttacker().getFactions()) {
                if (war.getAttacker().getPoints() < 0) {
                    f.setExhaustion(f.getExhaustion() + config.getExhaustionLoosing());
                    return;
                }
                f.setExhaustion(f.getExhaustion() + config.getExhaustion());
            }
            for (Faction f : war.getDefender().getFactions()) {
                if (war.getDefender().getPoints() < 0) {
                    f.setExhaustion(f.getExhaustion() + config.getExhaustionLoosing());
                    return;
                }
                f.setExhaustion(f.getExhaustion() + config.getExhaustion());
            }

            // If the attacker wins, the war goals (depending on CB) get forced
            if (war.getAttacker().getPoints() >= 100) {
                forceWarGoal(war.getAttacker());
            }

            // If the defender wins, the war just ends
            if (war.getDefender().getPoints() >= 100) {
                war.end();
            }


        }

        // Regenerate exhaustion
        for (Faction f : plugin.getFactionCache().getActive()) {
            if (!f.isInWar() && f.getExhaustion() >= 0) {
                f.setExhaustion(f.getExhaustion() - config.getExhaustion());
            }
        }

    }

    public void forceWarGoal(WarParty warParty) {
        WarParty enemy = warParty.getEnemy();
        switch (warParty.getWar().getCasusBelli().getType()) {
            case BORDER_FRICTION: // Not implemented
            case LIBERATION:
            case RESTORATION_OF_UNION:
            case IMPERIAL_BAN:
            case CLAIM_ON_THRONE:

            case RAID: // Literally no goals
                break;

            case RECONQUEST: // Gives all core regions back to their owner. If current (loosing) owner has a core there as well, add Reconquest CB.
                Faction leader = (Faction) warParty.getLeader();
                for (Region r : leader.getRegions()) {
                    if (r.getCoreFactions().containsKey((Faction) warParty.getLeader())) {
                        if (r.getCoreFactions().containsKey(r.getOwner())) {
                            r.getOwner().getCasusBelli().add(new CasusBelli(CasusBelli.Type.RECONQUEST, warParty.getLeader(), null));
                        }
                        r.setOwner((Faction) warParty.getLeader());
                    }
                }


            case CONQUEST: // Give all claimed regions to all winners / just regions claimed by the leader to the leader. Adds Reconquest CB if core
                for (Faction f : enemy.getFactions()) {
                    for (Region r : f.getRegions()) {

                        if (config.isForceWarGoalsForAllWinners()) {
                            for (Faction ally : warParty.getFactions()) {
                                if (r.getClaimFactions().containsKey(ally)) {
                                    if (r.getCoreFactions().containsKey(r.getOwner())) {
                                        r.getOwner().getCasusBelli().add(new CasusBelli(CasusBelli.Type.RECONQUEST, warParty.getLeader(), null));
                                    }
                                    r.setOwner(ally);
                                }
                            }
                        }

                        else {
                            if (r.getClaimFactions().containsKey((Faction) warParty.getLeader())) {
                                if (r.getCoreFactions().containsKey(r.getOwner())) {
                                    r.getOwner().getCasusBelli().add(new CasusBelli(CasusBelli.Type.RECONQUEST, warParty.getLeader(), null));
                                }
                                r.setOwner((Faction) warParty.getLeader());
                            }

                        }
                    }
                }

            case INDEPENDENCE: // Make faction independent
                Faction independent = (Faction) warParty.getLeader();
                Faction lord = (Faction) warParty.getWar().getCasusBelli().getTarget();
                lord.getRelations().remove(independent);
                independent.getRelations().remove(lord);
                lord.getCasusBelli().add(new CasusBelli(CasusBelli.Type.RESUBJAGATION, independent, new Date(System.currentTimeMillis() + (config.getCBLiberationExp() * FConfig.DAY))));

            case RESUBJAGATION: // Make loosing faction vassal
                Faction newLord = (Faction) warParty.getLeader();
                Faction newVassal = (Faction) warParty.getWar().getCasusBelli().getTarget();
                new RelationRequest(Bukkit.getConsoleSender(), newLord, newVassal, Relation.VASSAL).confirm();
                newVassal.getCasusBelli().add(new CasusBelli(CasusBelli.Type.RESUBJAGATION, newLord, new Date(System.currentTimeMillis() + (config.getCBLiberationExp() * FConfig.DAY))));
        }

        warParty.getWar().end();

    }
}
