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
package io.github.dre2n.factionsxl.player;

import io.github.dre2n.commons.player.PlayerUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.scoreboard.FScoreboard;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * FPlayer instance manager.
 *
 * @author Daniel Saukel
 */
public class FPlayerCache {

    FactionsXL plugin;

    private Set<FPlayer> fPlayers = new HashSet<>();

    public FPlayerCache(FactionsXL plugin) {
        this.plugin = plugin;

        for (Player player : Bukkit.getOnlinePlayers()) {
            fPlayers.add(new FPlayer(plugin, player));
        }
    }

    public FPlayer create(Player player) {
        FPlayer fPlayer = new FPlayer(plugin, player);
        fPlayers.add(fPlayer);
        return fPlayer;
    }

    /* Getters and setters */
    /**
     * @return
     * the FPlayer instances of all online players
     */
    public Set<FPlayer> getFPlayers() {
        return fPlayers;
    }

    /**
     * @return
     * the FPlayer that represents the player
     */
    public FPlayer getByPlayer(OfflinePlayer player) {
        for (FPlayer fPlayer : fPlayers) {
            if (player.equals(fPlayer.getPlayer())) {
                return fPlayer;
            }
        }

        if (hasPlayedBefore(player)) {
            return new FPlayer(plugin, player.getUniqueId());
        } else {
            return null;
        }
    }

    /**
     * @return
     * the FPlayer that has this UUID
     */
    public FPlayer getByUniqueId(UUID uuid) {
        return getByPlayer(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * @return
     * the FPlayer that has this name
     */
    public FPlayer getByName(String name) {
        return getByPlayer(Bukkit.getOfflinePlayer(name));
    }

    /**
     * @param player
     * an instance of FPlayer to add
     */
    public void addPlayer(FPlayer player) {
        for (FPlayer fPlayer : fPlayers) {
            if (fPlayer.getPlayer().equals(player.getPlayer())) {
                fPlayers.remove(fPlayer);
            }
        }

        fPlayers.add(player);
    }

    /**
     * @param player
     * an instance of FPlayer to remove
     */
    public void removePlayer(FPlayer player) {
        fPlayers.remove(player);
        FPlayerData data = player.getData();
        data.setLastName(player.getName());
        data.setTimeLastPlayed(System.currentTimeMillis());
        data.save();
        FScoreboard.remove(player);
    }

    /* Persistence */
    /**
     * Saves all players
     */
    public void saveAll() {
        for (FPlayer fPlayer : fPlayers) {
            fPlayer.getData().save();
        }
    }

    /**
     * Loads all players
     */
    public void loadAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            new FPlayer(plugin, player);
        }
    }

    /**
     * @param player
     * an OfflinePlayer
     * @return
     * the player's dynasty
     */
    public Dynasty getDynastyByPlayer(OfflinePlayer player) {
        /*for (Dynasty dynasty : dynasties) {
            if (dynasty.getMembers().contains(player)) {
                return dynasty;
            }
        }*/
        return null;
    }

    /**
     * @param uuid
     * a player's uuid
     * @return
     * the player's dynasty
     */
    public Dynasty getDynastyByUUID(UUID uuid) {
        return getDynastyByPlayer(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * Automatically kick players who haven't been online for the time specified in the config
     */
    public void autoKick() {
        for (Faction faction : plugin.getFactionCache().getActive()) {
            for (OfflinePlayer player : faction.getMembers().getOfflinePlayers()) {
                if (System.currentTimeMillis() > player.getLastPlayed() + plugin.getFConfig().getAutoKickTime()) {
                    FactionsXL.debug("Kicking " + player + " / Last played: " + new java.util.Date(player.getLastPlayed()));
                    faction.kick(player);
                    if (faction.getAdmin().getUniqueId().equals(player.getUniqueId())) {
                        faction.doSuccession();
                    }
                }
            }
        }
    }

    /**
     * @param player
     * the player to check
     * @return
     * if the player has data
     */
    public static boolean hasPlayedBefore(OfflinePlayer player) {
        return getFile(player).exists();
    }

    public static File getFile(UUID uuid) {
        return new File(FactionsXL.PLAYERS, uuid.toString() + ".yml");
    }

    public static File getFile(OfflinePlayer player) {
        return getFile(player.getUniqueId());
    }

    public static File getFile(String name) {
        return getFile(PlayerUtil.getUniqueIdFromName(name));
    }

}
