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
package io.github.dre2n.factionsxl.protection;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Daniel Saukel
 */
public class LandProtectionListener implements Listener {

    FactionsXL plugin;
    FactionCache factions;
    Board board;
    FConfig config;
    boolean wildernessProtected;

    public LandProtectionListener(FactionsXL plugin) {
        this.plugin = plugin;
        factions = plugin.getFactionCache();
        board = plugin.getBoard();
        config = plugin.getFConfig();
        wildernessProtected = config.isWildernessProtected();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getBlock(), event, true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getBlock(), event, false);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getBlock(), event, true);
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getBlockClicked(), event, true);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        forbidIfInProtectedTerritory(event.getPlayer(), event.getBlockClicked(), event, false);
    }

    private static final Set<Material> NO_INTERACT = new HashSet<>(Arrays.asList(
            ANVIL,
            BEACON,
            BLACK_SHULKER_BOX,
            BLUE_SHULKER_BOX,
            BREWING_STAND,
            BROWN_SHULKER_BOX,
            BURNING_FURNACE,
            CAKE_BLOCK,
            CAULDRON,
            CHEST,
            CYAN_SHULKER_BOX,
            DIODE_BLOCK_OFF,
            DIODE_BLOCK_ON,
            DISPENSER,
            DRAGON_EGG,
            DROPPER,
            ENCHANTMENT_TABLE,
            ENDER_CHEST,
            ENDER_PORTAL_FRAME,
            FIRE,
            FURNACE,
            GOLD_PLATE,
            GRASS,
            GRAY_SHULKER_BOX,
            GREEN_SHULKER_BOX,
            HOPPER,
            IRON_PLATE,
            LIGHT_BLUE_SHULKER_BOX,
            LIME_SHULKER_BOX,
            MAGENTA_SHULKER_BOX,
            NOTE_BLOCK,
            ORANGE_SHULKER_BOX,
            PINK_SHULKER_BOX,
            PURPLE_SHULKER_BOX,
            RED_SHULKER_BOX,
            REDSTONE_COMPARATOR_OFF,
            REDSTONE_COMPARATOR_ON,
            SILVER_SHULKER_BOX,
            WHITE_SHULKER_BOX,
            YELLOW_SHULKER_BOX
    ));

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && NO_INTERACT.contains(event.getClickedBlock().getType())) {
            Player breaker = event.getPlayer();
            if (breaker == null) {
                return;
            }
            if (FPermission.hasPermission(breaker, FPermission.BUILD)) {
                return;
            }

            Region region = board.getByChunk(event.getClickedBlock().getChunk());
            if (region == null || region.isNeutral()) {
                return;
            }

            Faction bFaction = factions.getByMember(breaker);
            Faction owner = region.getOwner();
            Relation rel = owner.getRelation(bFaction);
            if (!rel.canBuild()) {
                event.setCancelled(true);
                ParsingUtil.sendActionBarMessage(breaker, FMessage.PROTECTION_CANNOT_INTERACT_FACTION.getMessage(), event.getClickedBlock().getType().toString(), region.getOwner());
            }
        }
    }

    private void forbidIfInProtectedTerritory(Player breaker, Block destroyed, Cancellable event, boolean destroy) {
        if (breaker == null) {
            return;
        }
        if (FPermission.hasPermission(breaker, FPermission.BUILD)) {
            return;
        }

        Region region = board.getByChunk(destroyed.getChunk());
        if (region == null || region.isNeutral()) {
            if (wildernessProtected) {
                event.setCancelled(true);
                ParsingUtil.sendActionBarMessage(breaker, (destroy ? FMessage.PROTECTION_CANNOT_DESTROY_WILDERNESS : FMessage.PROTECTION_CANNOT_BUILD_WILDERNESS).getMessage());
            }
            return;
        }

        Faction bFaction = factions.getByMember(breaker);
        Faction owner = region.getOwner();
        Relation rel = owner.getRelation(bFaction);
        if (rel == Relation.ENEMY) {
            if (event instanceof BlockBreakEvent) {
                // DO STUFF
            } else if (event instanceof BlockPlaceEvent) {
                // DO STUFF
            }
        }
        if (!rel.canBuild()) {
            event.setCancelled(true);
            ParsingUtil.sendActionBarMessage(breaker, (destroy ? FMessage.PROTECTION_CANNOT_DESTROY_FACTION : FMessage.PROTECTION_CANNOT_BUILD_FACTION).getMessage(), region.getOwner());
        }
    }

}
