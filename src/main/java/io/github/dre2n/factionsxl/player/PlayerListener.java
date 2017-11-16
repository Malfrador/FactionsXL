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

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.board.RegionType;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.scoreboard.FScoreboard;
import io.github.dre2n.factionsxl.scoreboard.sidebar.FDefaultSidebar;
import io.github.dre2n.factionsxl.scoreboard.sidebar.FInfoSidebar;
import io.github.dre2n.factionsxl.util.LazyChunk;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
    FConfig fConfig;

    public static final String SPACE = " " + ChatColor.RESET;

    public PlayerListener(FactionsXL plugin) {
        this.plugin = plugin;
        fPlayers = plugin.getFPlayerCache();
        board = plugin.getBoard();
        fConfig = plugin.getFConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = new FPlayer(player);
        fPlayers.addPlayer(fPlayer);

        Region region = fPlayer.getLastRegion();
        MessageUtil.sendActionBarMessage(player, getRegionName(player, region));

        if (fConfig.isScoreboardEnabledByDefault()) {
            FScoreboard.init(fPlayer);
            FScoreboard.get(fPlayer).setDefaultSidebar(new FDefaultSidebar(), fConfig.getScoreboardUpdateInterval());
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
    public void onDeath(PlayerDeathEvent event) {
        Player killedP = event.getEntity();
        Player killerP = event.getEntity().getKiller();
        FPlayer killedF = fPlayers.getByPlayer(killedP);
        FPlayer killerF = killerP != null ? fPlayers.getByPlayer(killerP) : null;
        double loss = fConfig.getPowerDeathLoss();
        double newPower = killedF.getPower() - loss;
        killedF.setPower(newPower < fConfig.getMinPower() ? fConfig.getMinPower() : newPower);
        if (killerP != null) {
            killerF.setPower(killerF.getPower() + loss);
            ParsingUtil.sendMessage(killedP, FMessage.DEATH_PLAYER_KILL_KILLED.getMessage(), killerF, String.valueOf(loss), String.valueOf(killedF.getPower()));
            ParsingUtil.sendMessage(killerP, FMessage.DEATH_PLAYER_KILL_KILLER.getMessage(), killedF, String.valueOf(loss), String.valueOf(killerF.getPower()));
        } else {
            ParsingUtil.sendMessage(killedP, FMessage.DEATH_DEFAULT_DEATH.getMessage(), String.valueOf(loss), String.valueOf(killedF.getPower()));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();
        if (fromChunk == toChunk) {
            return;
        }

        Player player = event.getPlayer();
        FPlayer fPlayer = fPlayers.getByPlayer(player);

        Region fromRegion = fPlayer.getLastRegion();
        Region toRegion = board.getByChunk(toChunk);
        fPlayer.setLastRegion(toRegion);

        if (fPlayer.isAutoclaiming() && toRegion == null) {
            fPlayer.getAutoclaimingRegion().getChunks().add(new LazyChunk(toChunk));
            ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_CHUNK_ADDED.getMessage(), fPlayer.getAutoclaimingRegion());
            return;
        }

        if (fromRegion == toRegion) {
            return;
        }

        MessageUtil.sendActionBarMessage(event.getPlayer(), getRegionName(player, toRegion));
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
                    if (fPlayer.isScoreboardEnabled()) {
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
        if (Version.andHigher(Version.MC1_10).contains(CompatibilityHandler.getInstance().getVersion())) {
            player.stopSound(faction.getAnthem());
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound " + player.getName() + " master " + faction.getAnthem());
        }
    }

    private String getRegionName(Player player, Region region) {
        String main = ParsingUtil.getRegionName(player, region);
        if (region == null || region.getType() != RegionType.WARZONE) {
            return main;
        } else {
            String warZone = ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "[ " + FMessage.REGION_WAR_ZONE.getMessage().toUpperCase() + "]";
            return warZone + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + main + SPACE + SPACE + SPACE + SPACE + SPACE + SPACE + warZone;
        }
    }

}
