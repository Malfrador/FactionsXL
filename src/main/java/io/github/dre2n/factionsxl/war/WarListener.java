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
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.protection.EntityProtectionListener;
import io.github.dre2n.factionsxl.relation.Relation;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class WarListener implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();

    Set<Battle> battleCache = new HashSet<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player1 = EntityProtectionListener.getDamageSource(event.getDamager());
        if (!(event.getEntity() instanceof Player) || player1 == null) {
            return;
        }
        Player player2 = (Player) event.getEntity();
        Faction faction1 = factions.getByMember(player1);
        Faction faction2 = factions.getByMember(player2);
        if (faction1 == null || faction2 == null || faction1.getRelation(faction2) != Relation.ENEMY) {
            return;
        }
        Battle takesPart = null;
        for (Battle battle : battleCache) {
            if (battle.takesPart(player1) && battle.takesPart(player2)) {
                takesPart = battle;
                break;
            }
        }
        if (takesPart == null) {
            takesPart = new Battle(player1, player2);
            battleCache.add(takesPart);
            new Expiration(takesPart).runTaskTimer(plugin, 0L, FConfig.SECOND);
        } else {
            takesPart.expandExpirationTime();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player1 = event.getEntity().getKiller();
        Player player2 = event.getEntity();
        for (Battle battle : battleCache) {
            if (battle.takesPart(player2)) {
                battleCache.remove(battle);
                System.out.println("Removed battle " + battle.toString());
                if (battle.takesPart(player1)) {
                    battle.win(player1);
                }
            }
        }
    }

    private class Expiration extends BukkitRunnable {

        private Battle battle;

        Expiration(Battle battle) {
            this.battle = battle;
        }

        @Override
        public void run() {
            if (battle.getExpirationTime() <= System.currentTimeMillis()) {
                battleCache.remove(battle);
            }
        }

    }

}
