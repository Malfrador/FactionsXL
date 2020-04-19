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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.entity.Relation;
import de.erethon.factionsxl.entity.RelationRequest;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.util.ParsingUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


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
        for (War w : plugin.getWarCache().getWars()) {
            for (Faction f : w.getAttacker().getFactions()) {
                if (w.getAttacker().getPoints() < 0) {
                    f.setExhaustion(f.getExhaustion() + config.getExhaustionLoosing());
                    return;
                }
                f.setExhaustion(f.getExhaustion() + config.getExhaustion());
            }
            for (Faction f : w.getDefender().getFactions()) {
                if (w.getDefender().getPoints() < 0) {
                    f.setExhaustion(f.getExhaustion() + config.getExhaustionLoosing());
                    return;
                }
                f.setExhaustion(f.getExhaustion() + config.getExhaustion());
            }
        }
        for (Faction f : plugin.getFactionCache().getActive()) {
            if (!f.isInWar() && f.getExhaustion() >= 0) {
                f.setExhaustion(f.getExhaustion() - config.getExhaustion());
            }
        }
    }
}
