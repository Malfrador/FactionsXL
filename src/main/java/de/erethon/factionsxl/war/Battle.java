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
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.faction.FactionCache;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * @author Daniel Saukel
 */
public class Battle {

    private Player player1;
    private Player player2;
    private long start;
    private long expiration;
    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();

    public Battle(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        start = System.currentTimeMillis();
        expiration = System.currentTimeMillis() + FConfig.MINUTE;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public long getStartTime() {
        return start;
    }

    public long getExpirationTime() {
        return expiration;
    }

    public void expandExpirationTime() {
        expiration = System.currentTimeMillis() + FConfig.MINUTE;
    }

    public boolean takesPart(Player player) {
        return player.equals(player1) || player.equals(player2);
    }

    /**
     * @param winner The winning player
     * @param looser the loosing (dead) player
     */
    public void win(Player winner, Player looser) {
        Faction f = factions.getByMember(winner);
        Faction fl = factions.getByMember(looser);
        Region r = plugin.getBoard().getByLocation(winner.getLocation());
        Set<WarParty> WP = f.getWarParties();
        Set<WarParty> lWP = fl.getWarParties();
        for (WarParty w : WP) {
            w.addKill();
            if (w.getFactions().contains(r.getOwner())) {
                if (r.getInfluence() <= 100) {
                    r.setInfluence(r.getInfluence() + 1);
                    MessageUtil.sendActionBarMessage(winner, "&aRegion verteidigt! &8- &7Einfluss&8: &a+1 &7(" + r.getInfluence() + "&7)");
                }
            }
            else {
                    if (r.getInfluence() >= 0) {
                        r.setInfluence(r.getInfluence() - 1);
                        MessageUtil.sendActionBarMessage(winner, "&aRegion geschwächt! &8- &7Einfluss&8: &c-1 &7(" + r.getInfluence() + "&7)");
                    }
                }
        }
        for (WarParty wL : lWP) {
            wL.addDeath();
        }
    }

    @Override
    public String toString() {
        return "Battle{player1=" + player1.getName() + "player2=" + player2.getName() + "}";
    }

}
