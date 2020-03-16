/*
 * Copyright (c) 2017-2019 Daniel Saukel
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

    public void win(Player player) {
        Faction f = factions.getByMember(player);
        Region r = plugin.getBoard().getByLocation(player.getLocation());
        Set<WarParty> WP = f.getWarParties();
        for (WarParty w : WP) {
            if (w.getRole() == WarPartyRole.ATTACKER) {
                if (r.getInfluence() >= 0) {
                    r.setInfluence(r.getInfluence() - 1);
                    break;
                }
            }
            else if (w.getRole() == WarPartyRole.DEFENDER) {
                if (r.getInfluence() <= 100) {
                    r.setInfluence(r.getInfluence() + 1);
                    break;
                }
            }
            else {
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Battle{player1=" + player1.getName() + "player2=" + player2.getName() + "}";
    }

}
