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

import io.github.dre2n.commons.misc.PlayerUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * FPlayer instance manager.
 *
 * @author Daniel Saukel
 */
public class FPlayerCache {

    private CopyOnWriteArrayList<FPlayer> fPlayers = new CopyOnWriteArrayList<>();

    public FPlayerCache() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            fPlayers.add(new FPlayer(player));
        }
    }

    /* Getters and setters */
    /**
     * @return
     * the FPlayer that represents the player
     */
    public FPlayer getByPlayer(OfflinePlayer player) {
        for (FPlayer fPlayer : fPlayers) {
            if (fPlayer.getPlayer() == player) {
                return fPlayer;
            }
        }

        if (hasPlayedBefore(player)) {
            return new FPlayer(player.getUniqueId());
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
            if (player.getPlayer().equals(player.getPlayer())) {
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
            new FPlayer(player);
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
