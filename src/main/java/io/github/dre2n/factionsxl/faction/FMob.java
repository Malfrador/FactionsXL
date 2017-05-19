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
package io.github.dre2n.factionsxl.faction;

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import io.github.dre2n.factionsxl.FactionsXL;
import static io.github.dre2n.factionsxl.config.FMessage.MOB_TRADER;
import static io.github.dre2n.factionsxl.config.FMessage.MOB_VILLAGER;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * @author Daniel Saukel
 */
public class FMob implements Listener {

    public static String fromString(String string) {
        string = string.toLowerCase();
        switch (string) {
            case "n":
            case "nitwit":
            case "v":
            case "villager":
                return MOB_VILLAGER.getMessage();
            case "m":
            case "merchant":
            case "t":
            case "trader":
                return MOB_TRADER.getMessage();
            default:
                return null;
        }
    }

    /**
     * @param location
     * the location where the villager will spawn
     * @return
     * the spawned villager
     */
    public static Villager spawnVillager(Location location) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        if (Version.andHigher(Version.MC1_11).contains(CompatibilityHandler.getInstance().getVersion())) {
            villager.setProfession(Profession.NITWIT);
        } else {
            villager.setProfession(Profession.FARMER);
        }
        villager.setCustomName(MOB_VILLAGER.getMessage());
        villager.setCustomNameVisible(true);
        return villager;
    }

    /**
     * @param location
     * the location where the trader will spawn
     * @return
     * the spawned trader
     */
    public static Villager spawnTrader(Location location) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setProfession(Profession.LIBRARIAN);
        villager.setCustomName(MOB_TRADER.getMessage());
        villager.setCustomNameVisible(true);
        return villager;
    }

    /**
     * @param entity
     * the entity to check
     * @return
     * true if the entity is a villager
     */
    public static boolean isVillager(Entity entity) {
        return MOB_VILLAGER.getMessage().equals(entity.getCustomName());
    }

    /**
     * @param entity
     * the entity to check
     * @return
     * true if the entity is a trader
     */
    public static boolean isTrader(Entity entity) {
        return MOB_TRADER.getMessage().equals(entity.getCustomName());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        Faction faction = FactionsXL.getInstance().getFactionCache().getByLocation(entity.getLocation());
        if (faction == null) {
            return;
        }
        if (isVillager(entity)) {
            event.setCancelled(true);
            faction.getPopulationMenu().openMain(player);
        } else if (isTrader(entity)) {
            event.setCancelled(true);
            faction.getEconomyMenu().open(player);
        }
    }

}
