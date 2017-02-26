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
package io.github.dre2n.factionsxl.player;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.scoreboard.FScoreboard;
import io.github.dre2n.factionsxl.scoreboard.sidebar.FDefaultSidebar;
import io.github.dre2n.factionsxl.scoreboard.sidebar.FInfoSidebar;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Daniel Saukel
 */
public class PlayerListener implements Listener {

    FactionsXL plugin;
    FPlayerCache fPlayers;
    Board board;

    public PlayerListener(FactionsXL plugin) {
        this.plugin = plugin;
        fPlayers = plugin.getFPlayerCache();
        board = plugin.getBoard();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = new FPlayer(player);
        fPlayers.addPlayer(fPlayer);

        Region region = board.getByLocation(player.getLocation());
        MessageUtil.sendActionBarMessage(player, ParsingUtil.getRegionName(player, region));

        if (plugin.getFConfig().isScoreboardEnabledByDefault()) {
            FScoreboard.init(fPlayer);
            FScoreboard.get(fPlayer).setDefaultSidebar(new FDefaultSidebar(), plugin.getFConfig().getScoreboardUpdateInterval());
            FScoreboard.get(fPlayer).setSidebarVisibility(fPlayer.isScoreboardEnabled());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        FPlayer fPlayer = fPlayers.getByPlayer(event.getPlayer());
        FScoreboard.remove(fPlayer);
        fPlayers.removePlayer(fPlayer);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();
        if (fromChunk == toChunk) {
            return;
        }

        Region fromRegion = board.getByChunk(fromChunk);
        Region toRegion = board.getByChunk(toChunk);
        if (fromRegion == toRegion) {
            return;
        }

        Player player = event.getPlayer();
        MessageUtil.sendActionBarMessage(event.getPlayer(), ParsingUtil.getRegionName(player, toRegion));
        if (toRegion != null) {
            Faction fromFaction = fromRegion != null ? fromRegion.getOwner() : null;
            Faction toFaction = toRegion != null ? toRegion.getOwner() : null;
            if (fromFaction != toFaction) {
                stopSound(player, fromFaction);
                if (toFaction != null && toFaction.getDescription() != null) {
                    MessageUtil.sendCenteredMessage(player, ParsingUtil.getFactionName(player, toFaction));
                    MessageUtil.sendCenteredMessage(player, toFaction.getDescription());
                    stopSound(player, toFaction);
                    player.playSound(player.getLocation(), toFaction.getAnthem(), 1, 1);
                    if (fPlayers.getByPlayer(player).isScoreboardEnabled()) {
                        FScoreboard.get(player).setTemporarySidebar(new FInfoSidebar(toFaction));
                    }
                }
            }
        }
    }

    private void stopSound(Player player, Faction faction) {
        if (player == null || faction == null || faction.getAnthem() == null) {
            return;
        }
        player.stopSound(faction.getAnthem());
    }

}
