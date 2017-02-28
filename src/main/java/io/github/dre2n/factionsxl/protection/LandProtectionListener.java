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
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Daniel Saukel
 */
public class LandProtectionListener implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();
    Board board = plugin.getBoard();
    FConfig config = plugin.getFConfig();
    boolean wildernessProtected = config.isWildernessProtected();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        if (FPermission.hasPermission(breaker, FPermission.BUILD)) {
            return;
        }

        Region region = board.getByChunk(event.getBlock().getChunk());
        if (region == null || region.isNeutral()) {
            if (wildernessProtected) {
                event.setCancelled(true);
                ParsingUtil.sendMessage(breaker, FMessage.PROTECTION_CANNOT_DESTROY_WILDERNESS.getMessage());
            }
            return;
        }

        Faction bFaction = factions.getByMember(breaker);
        Faction owner = region.getOwner();
        Relation rel = owner.getRelation(bFaction);
        if (!rel.canBuild()) {
            event.setCancelled(true);
            ParsingUtil.sendMessage(breaker, FMessage.PROTECTION_CANNOT_DESTROY_FACTION.getMessage(), region.getOwner());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (FPermission.hasPermission(player, FPermission.BUILD)) {
            return;
        }

        Region region = board.getByChunk(event.getBlock().getChunk());
        if (region == null || region.isNeutral()) {
            if (wildernessProtected) {
                event.setCancelled(true);
                ParsingUtil.sendMessage(player, FMessage.PROTECTION_CANNOT_BUILD_WILDERNESS.getMessage());
            }
            return;
        }

        Faction bFaction = factions.getByMember(player);
        Faction owner = region.getOwner();
        Relation rel = owner.getRelation(bFaction);
        if (!rel.canBuild()) {
            event.setCancelled(true);
            ParsingUtil.sendMessage(player, FMessage.PROTECTION_CANNOT_BUILD_FACTION.getMessage(), region.getOwner());
        }
    }

}
