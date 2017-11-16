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
package io.github.dre2n.factionsxl.protection;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.board.RegionType;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import static io.github.dre2n.factionsxl.protection.EntityProtectionListener.Action.*;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * @author Daniel Saukel
 */
public class EntityProtectionListener implements Listener {

    enum Action {
        ATTACK,
        BUILD,
        EQUIP,
        LEASH,
        SHEAR,
        SPLASH_POTION,
        TAME,
        UNLEASH
    }

    FactionsXL plugin = FactionsXL.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = getDamageSource(event.getDamager());
        Entity eAttacker = attacker != null ? attacker : event.getDamager();
        Entity eDefender = event.getEntity();
        if (!(eAttacker instanceof Player)) {
            return;
        }
        if (!(eDefender instanceof Player)) {
            forbidIfInProtectedTerritory(attacker, eDefender, event, ATTACK);
            return;
        }
        Player defender = (Player) eDefender;
        Region region = plugin.getBoard().getByLocation(defender.getLocation());
        if (region.getType() == RegionType.WARZONE) {
            return;
        }
        Faction aFaction = plugin.getFactionCache().getByMember(attacker);
        Faction dFaction = plugin.getFactionCache().getByMember(defender);
        Faction rFaction = region.getOwner();
        double shield = plugin.getFConfig().getTerritoryShield();
        if (aFaction != null && aFaction.getRelation(dFaction).isProtected()) {
            ParsingUtil.sendActionBarMessage(attacker, FMessage.PROTECTION_CANNOT_ATTACK_PLAYER.getMessage(), dFaction);
            event.setCancelled(true);
        } else if (rFaction != null && rFaction.getRelation(dFaction).isProtected()) {
            if (plugin.getFConfig().isTerritoryProtectionEnabled() && (!plugin.getFConfig().isCapitalProtectionEnabled()
                    || rFaction.getCapital().equals(plugin.getBoard().getByLocation(eDefender.getLocation())))) {
                ParsingUtil.sendActionBarMessage(attacker, (plugin.getFConfig().isCapitalProtectionEnabled() ? FMessage.PROTECTION_CANNOT_ATTACK_CAPITAL
                        : FMessage.PROTECTION_CANNOT_ATTACK_FACTION).getMessage(), rFaction);
                event.setCancelled(true);
            } else if (shield != 0) {
                event.setDamage(event.getDamage() - event.getDamage() * shield);
                ParsingUtil.sendActionBarMessage(attacker, FMessage.PROTECTION_DAMAGE_REDUCED.getMessage(), (int) (shield * 100), rFaction);
            }
        }
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        Player owner = null;
        if (event.getOwner() instanceof Player) {
            owner = (Player) event.getOwner();
        }
        forbidIfInProtectedTerritory(owner, event.getEntity(), event, TAME);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        forbidIfInProtectedTerritory(getDamageSource(event.getRemover()), event.getEntity(), event, ATTACK);
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getEntity(), event, BUILD);
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getRightClicked(), event, EQUIP);
    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getEntity(), event, SHEAR);
    }

    @EventHandler
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getEntity(), event, LEASH);
    }

    @EventHandler
    public void onPlayerUnLeashEntity(PlayerUnleashEntityEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getEntity(), event, UNLEASH);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        forbidIfInProtectedTerritory(getDamageSource(event.getAttacker()), event.getVehicle(), event, ATTACK);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ProjectileSource shooter = event.getPotion().getShooter();
        if (!(shooter instanceof Player)) {
            return;
        }
        if (event.getAffectedEntities().isEmpty()) {
            return;
        }
        forbidIfInProtectedTerritory((Player) shooter, event.getAffectedEntities().iterator().next(), event, SPLASH_POTION);
    }

    @EventHandler
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        ProjectileSource shooter = event.getAreaEffectCloud().getSource();
        if (!(shooter instanceof Player)) {
            return;
        }
        forbidIfInProtectedTerritory((Player) shooter, event.getAreaEffectCloud(), event, SPLASH_POTION);
    }

    private void forbidIfInProtectedTerritory(Player attacker, Entity damaged, Cancellable event, Action action) {
        if (attacker == null) {
            return;
        }
        if (FPermission.hasPermission(attacker, FPermission.BUILD)) {
            return;
        }
        if (damaged instanceof Monster) {
            return;
        }

        boolean living = damaged instanceof LivingEntity;
        Region region = plugin.getBoard().getByLocation(damaged.getLocation());
        if (region == null || region.isNeutral()) {
            return;
        }

        Faction aFaction = plugin.getFactionCache().getByMember(attacker);
        Faction owner = region.getOwner();
        Relation rel = owner.getRelation(aFaction);
        if (!rel.canBuild()) {
            event.setCancelled(true);
            FMessage message = FMessage.PROTECTION_CANNOT_BUILD_FACTION;
            switch (action) {
                case ATTACK:
                    message = living ? FMessage.PROTECTION_CANNOT_ATTACK_FACTION : FMessage.PROTECTION_CANNOT_DESTROY_FACTION;
                    break;
                case BUILD:
                    message = FMessage.PROTECTION_CANNOT_BUILD_FACTION;
                    break;
                case EQUIP:
                    message = FMessage.PROTECTION_CANNOT_EQUIP_FACTION;
                    break;
                case LEASH:
                    message = FMessage.PROTECTION_CANNOT_LEASH_FACTION;
                    break;
                case SPLASH_POTION:
                    message = FMessage.PROTECTION_CANNOT_SPLASH_POTION_FACTION;
                    break;
                case SHEAR:
                    message = FMessage.PROTECTION_CANNOT_SHEAR_FACTION;
                    break;
                case TAME:
                    message = FMessage.PROTECTION_CANNOT_TAME_FACTION;
                    break;
                case UNLEASH:
                    message = FMessage.PROTECTION_CANNOT_UNLEASH_FACTION;
            }
            ParsingUtil.sendActionBarMessage(attacker, message.getMessage(), region.getOwner());
        }
    }

    public static Player getDamageSource(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        } else if (damager instanceof Arrow) {
            ProjectileSource shooter = ((Arrow) damager).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }

}
