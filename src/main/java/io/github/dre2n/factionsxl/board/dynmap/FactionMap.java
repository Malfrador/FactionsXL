/*
 * Copyright 2011-2017 mikeprimm.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.dre2n.factionsxl.board.dynmap;

import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.FPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.PlayerSet;
import org.dynmap.utils.TileFlags;

/**
 * This is a modified version of mikeprimms plugin Dynmap-FactionCache taken from FactionsUUID by drtshock.
 *
 * @author Mike Primm, Trent Hensler, Daniel Saukel
 */
public class FactionMap extends EngineDynmap {

    FactionCache factions = plugin.getFactionCache();

    private String factionsHome;
    protected String playersetString;

    public FactionMap() {
        layerName = "Factions";
        layerVisible = true;
        prefix = "factions_";
        markersetString = prefix + "markerset";
        factionsHome = prefix + "home_";
        playersetString = prefix + "playerset_";
    }

    @Override
    public void init() {
        super.init();

        // Shedule non thread safe sync at the end!
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

                final Map<String, TempMarker> homes = createHomes();
                final Map<String, TempAreaMarker> areas = createAreas();
                final Map<String, Set<String>> playerSets = createPlayersets();

                if (!updateCore()) {
                    return;
                }

                // createLayer() is thread safe but it makes use of fields set in updateCore() so we must have it after.
                if (!updateLayer(createLayer())) {
                    return;
                }

                updateHomes(homes);
                updateAreas(areas);
                updatePlayersets(playerSets);
            }
        }, 100L, 100L);
    }

    // -------------------------------------------- //
    // UPDATE: AREAS
    // -------------------------------------------- //
    // Thread Safe: YES
    public Map<String, TempAreaMarker> createAreas() {
        Map<String, Map<Faction, Set<Region>>> worldFactionChunks = createWorldFactionChunks();
        return createAreas(worldFactionChunks);
    }

    // Thread Safe: YES
    public Map<String, Map<Faction, Set<Region>>> createWorldFactionChunks() {
        // Create map "world name --> faction --> set of chunk coords"
        Map<String, Map<Faction, Set<Region>>> worldFactionChunks = new HashMap<>();

        for (Region region : board.getRegions()) {
            String world = region.getWorld().getName();
            Faction owner = region.getOwner();

            Map<Faction, Set<Region>> factionChunks = worldFactionChunks.get(world);
            if (factionChunks == null) {
                factionChunks = new HashMap<>();
                worldFactionChunks.put(world, factionChunks);
            }

            Set<Region> factionTerritory = factionChunks.get(owner);
            if (factionTerritory == null) {
                factionTerritory = new HashSet<>();
                factionChunks.put(owner, factionTerritory);
            }

            factionTerritory.add(region);
        }

        return worldFactionChunks;
    }

    // Thread Safe: YES
    public Map<String, TempAreaMarker> createAreas(Map<String, Map<Faction, Set<Region>>> worldFactionChunks) {
        Map<String, TempAreaMarker> ret = new HashMap<>();

        // For each world
        for (Map.Entry<String, Map<Faction, Set<Region>>> entry : worldFactionChunks.entrySet()) {
            String world = entry.getKey();
            Map<Faction, Set<Region>> factionChunks = entry.getValue();

            // For each faction and its chunks in that world
            for (Map.Entry<Faction, Set<Region>> entry1 : factionChunks.entrySet()) {
                Faction faction = entry1.getKey();
                Set<Region> chunks = entry1.getValue();
                Map<String, TempAreaMarker> worldFactionMarkers = createAreas(world, faction, chunks);
                ret.putAll(worldFactionMarkers);
            }
        }

        return ret;
    }

    // Thread Safe: YES
    // Handle specific faction on specific world
    // "handle faction on world"
    public Map<String, TempAreaMarker> createAreas(String world, Faction faction, Set<Region> regions) {
        Set<Chunk> chunks = new HashSet<>();
        for (Region region : regions) {
            chunks.addAll(region.getChunks());
        }

        Map<String, TempAreaMarker> ret = new HashMap<>();

        // If the faction is visible ...
        if (!isVisible(faction, world)) {
            return ret;
        }

        // ... and has any chunks ...
        if (chunks.isEmpty()) {
            return ret;
        }

        // Index of polygon for given faction
        int markerIndex = 0;

        // Create the info window
        String description = getDescription(faction);

        // Fetch Style
        DynmapStyle style = faction.getDynmapStyle();

        // Loop through chunks: set flags on chunk map
        TileFlags allChunkFlags = new TileFlags();
        LinkedList<Chunk> allChunks = new LinkedList<>();
        for (Chunk chunk : chunks) {
            allChunkFlags.setFlag(chunk.getX(), chunk.getZ(), true); // Set flag for chunk
            allChunks.addLast(chunk);
        }

        // Loop through until we don't find more areas
        while (allChunks != null) {
            TileFlags ourChunkFlags = null;
            LinkedList<Chunk> ourChunks = null;
            LinkedList<Chunk> newChunks = null;

            int minimumX = Integer.MAX_VALUE;
            int minimumZ = Integer.MAX_VALUE;
            for (Chunk chunk : allChunks) {
                int chunkX = chunk.getX();
                int chunkZ = chunk.getZ();

                // If we need to start shape, and this block is not part of one yet
                if (ourChunkFlags == null && allChunkFlags.getFlag(chunkX, chunkZ)) {
                    ourChunkFlags = new TileFlags(); // Create map for shape
                    ourChunks = new LinkedList<>();
                    floodFillTarget(allChunkFlags, ourChunkFlags, chunkX, chunkZ); // Copy shape
                    ourChunks.add(chunk); // Add it to our chunk list
                    minimumX = chunkX;
                    minimumZ = chunkZ;
                } // If shape found, and we're in it, add to our node list
                else if (ourChunkFlags != null && ourChunkFlags.getFlag(chunkX, chunkZ)) {
                    ourChunks.add(chunk);
                    if (chunkX < minimumX) {
                        minimumX = chunkX;
                        minimumZ = chunkZ;
                    } else if (chunkX == minimumX && chunkZ < minimumZ) {
                        minimumZ = chunkZ;
                    }
                } // Else, keep it in the list for the next polygon
                else {
                    if (newChunks == null) {
                        newChunks = new LinkedList<>();
                    }
                    newChunks.add(chunk);
                }
            }

            // Replace list (null if no more to process)
            allChunks = newChunks;

            if (ourChunkFlags == null) {
                continue;
            }

            // Trace outline of blocks - start from minx, minz going to x+
            int initialX = minimumX;
            int initialZ = minimumZ;
            int currentX = minimumX;
            int currentZ = minimumZ;
            Direction direction = Direction.XPLUS;
            ArrayList<int[]> linelist = new ArrayList<>();
            linelist.add(new int[]{initialX, initialZ}); // Add start point
            while ((currentX != initialX) || (currentZ != initialZ) || (direction != Direction.ZMINUS)) {
                switch (direction) {
                    case XPLUS: // Segment in X+ direction
                        if (!ourChunkFlags.getFlag(currentX + 1, currentZ)) { // Right turn?
                            linelist.add(new int[]{currentX + 1, currentZ}); // Finish line
                            direction = Direction.ZPLUS; // Change direction
                        } else if (!ourChunkFlags.getFlag(currentX + 1, currentZ - 1)) { // Straight?
                            currentX++;
                        } else { // Left turn
                            linelist.add(new int[]{currentX + 1, currentZ}); // Finish line
                            direction = Direction.ZMINUS;
                            currentX++;
                            currentZ--;
                        }
                        break;
                    case ZPLUS: // Segment in Z+ direction
                        if (!ourChunkFlags.getFlag(currentX, currentZ + 1)) { // Right turn?
                            linelist.add(new int[]{currentX + 1, currentZ + 1}); // Finish line
                            direction = Direction.XMINUS; // Change direction
                        } else if (!ourChunkFlags.getFlag(currentX + 1, currentZ + 1)) { // Straight?
                            currentZ++;
                        } else { // Left turn
                            linelist.add(new int[]{currentX + 1, currentZ + 1}); // Finish line
                            direction = Direction.XPLUS;
                            currentX++;
                            currentZ++;
                        }
                        break;
                    case XMINUS: // Segment in X- direction
                        if (!ourChunkFlags.getFlag(currentX - 1, currentZ)) { // Right turn?
                            linelist.add(new int[]{currentX, currentZ + 1}); // Finish line
                            direction = Direction.ZMINUS; // Change direction
                        } else if (!ourChunkFlags.getFlag(currentX - 1, currentZ + 1)) { // Straight?
                            currentX--;
                        } else { // Left turn
                            linelist.add(new int[]{currentX, currentZ + 1}); // Finish line
                            direction = Direction.ZPLUS;
                            currentX--;
                            currentZ++;
                        }
                        break;
                    case ZMINUS: // Segment in Z- direction
                        if (!ourChunkFlags.getFlag(currentX, currentZ - 1)) { // Right turn?
                            linelist.add(new int[]{currentX, currentZ}); // Finish line
                            direction = Direction.XPLUS; // Change direction
                        } else if (!ourChunkFlags.getFlag(currentX - 1, currentZ - 1)) { // Straight?
                            currentZ--;
                        } else { // Left turn
                            linelist.add(new int[]{currentX, currentZ}); // Finish line
                            direction = Direction.XMINUS;
                            currentX--;
                            currentZ--;
                        }
                        break;
                }
            }

            int sz = linelist.size();
            double[] x = new double[sz];
            double[] z = new double[sz];
            for (int i = 0; i < sz; i++) {
                int[] line = linelist.get(i);
                x[i] = (double) line[0] * (double) BLOCKS_PER_CHUNK;
                z[i] = (double) line[1] * (double) BLOCKS_PER_CHUNK;
            }

            // Build information for specific area
            String markerId = prefix + world + "__" + faction.getId() + "__" + markerIndex;

            TempAreaMarker temp = new TempAreaMarker();
            temp.label = faction.getName();
            temp.world = world;
            temp.x = x;
            temp.z = z;
            temp.description = description;

            temp.lineColor = style.getLineColor();
            temp.lineOpacity = style.getLineOpacity();
            temp.lineWeight = style.getLineWeight();

            temp.fillColor = style.getFillColor();
            temp.fillOpacity = style.getFillOpacity();

            temp.boost = style.getBoost();

            ret.put(markerId, temp);

            markerIndex++;
        }

        return ret;
    }

    // Thread Safe: NO
    public void updateAreas(Map<String, TempAreaMarker> areas) {
        // Map Current
        Map<String, AreaMarker> markers = new HashMap<>();
        for (AreaMarker marker : this.markerset.getAreaMarkers()) {
            markers.put(marker.getMarkerID(), marker);
        }

        // Loop New
        for (Map.Entry<String, TempAreaMarker> entry : areas.entrySet()) {
            String markerId = entry.getKey();
            TempAreaMarker temp = entry.getValue();

            // Get Creative
            // NOTE: I remove from the map created just in the beginning of this method.
            // NOTE: That way what is left at the end will be outdated markers to remove.
            AreaMarker marker = markers.remove(markerId);
            if (marker == null) {
                marker = temp.create(this.markerset, markerId);
                if (marker == null) {
                    severe("Could not get/create the area marker " + markerId);
                }
            } else {
                temp.update(marker);
            }
        }

        // Only old/outdated should now be left. Delete them.
        for (AreaMarker marker : markers.values()) {
            marker.deleteMarker();
        }
    }

    // -------------------------------------------- //
    // UPDATE: PLAYERSET
    // -------------------------------------------- //
    // Thread Safe / Asynchronous: Yes
    public String createPlayersetId(Faction faction) {
        if (faction == null) {
            return null;
        }
        return playersetString + faction.getId();
    }

    // Thread Safe / Asynchronous: Yes
    public Set<String> createPlayerset(Faction faction) {
        if (faction == null) {
            return null;
        }
        Set<String> ret = new HashSet<>();

        for (OfflinePlayer player : faction.getMembers()) {
            // NOTE: We add both UUID and name. This might be a good idea for future proofing.
            ret.add(player.getUniqueId().toString());
            ret.add(player.getName());
        }

        return ret;
    }

    // Thread Safe / Asynchronous: Yes
    public Map<String, Set<String>> createPlayersets() {
        if (!Conf.dynmapVisibilityByFaction) {
            return null;
        }

        Map<String, Set<String>> ret = new HashMap<>();

        for (Faction faction : factions.getActive()) {
            String playersetId = createPlayersetId(faction);
            if (playersetId == null) {
                continue;
            }
            Set<String> playerIds = createPlayerset(faction);
            if (playerIds == null) {
                continue;
            }
            ret.put(playersetId, playerIds);
        }

        return ret;
    }

    // Thread Safe / Asynchronous: No
    public void updatePlayersets(Map<String, Set<String>> playersets) {
        if (playersets == null) {
            return;
        }

        // Remove
        for (PlayerSet set : this.markerApi.getPlayerSets()) {
            if (!set.getSetID().startsWith(playersetString)) {
                continue;
            }

            // (Null means remove all)
            if (playersets.containsKey(set.getSetID())) {
                continue;
            }

            set.deleteSet();
        }

        // Add / Update
        for (Map.Entry<String, Set<String>> entry : playersets.entrySet()) {
            // Extract from Entry
            String setId = entry.getKey();
            Set<String> playerIds = entry.getValue();

            // Get Creatively
            PlayerSet set = this.markerApi.getPlayerSet(setId);
            if (set == null) {
                set = this.markerApi.createPlayerSet(setId, // id
                        true, // symmetric
                        playerIds, // players
                        false // persistent
                );
            }
            if (set == null) {
                severe("Could not get/create the player set " + setId);
                continue;
            }

            // Set Content
            set.setPlayers(playerIds);
        }
    }

    // -------------------------------------------- //
    // UPDATE: HOMES
    // -------------------------------------------- //
    // Thread Safe / Asynchronous: Yes
    public Map<String, TempMarker> createHomes() {
        Map<String, TempMarker> ret = new HashMap<>();

        // Loop current factions
        for (Faction faction : factions.getActive()) {
            Location ps = faction.getHome();
            if (ps == null) {
                continue;
            }

            DynmapStyle style = faction.getDynmapStyle();

            String markerId = factionsHome + faction.getId();

            TempMarker temp = new TempMarker();
            temp.label = ChatColor.stripColor(faction.getName());
            temp.world = ps.getWorld().toString();
            temp.x = ps.getX();
            temp.y = ps.getY();
            temp.z = ps.getZ();
            temp.iconName = style.getHomeMarker();
            temp.description = getDescription(faction);

            ret.put(markerId, temp);
        }

        return ret;
    }

    // Thread Safe / Asynchronous: No
    // This method places out the faction home markers into the factions markerset.
    public void updateHomes(Map<String, TempMarker> homes) {
        // Put all current faction markers in a map
        Map<String, Marker> markers = new HashMap<>();
        for (Marker marker : this.markerset.getMarkers()) {
            markers.put(marker.getMarkerID(), marker);
        }

        // Loop homes
        for (Map.Entry<String, TempMarker> entry : homes.entrySet()) {
            String markerId = entry.getKey();
            TempMarker temp = entry.getValue();

            // Get Creative
            // NOTE: I remove from the map created just in the beginning of this method.
            // NOTE: That way what is left at the end will be outdated markers to remove.
            Marker marker = markers.remove(markerId);
            if (marker == null) {
                marker = temp.create(this.markerApi, this.markerset, markerId);
                if (marker == null) {
                    EngineDynmap.severe("Could not get/create the home marker " + markerId);
                }
            } else {
                temp.update(this.markerApi, marker);
            }
        }

        // Delete Deprecated Markers
        // Only old markers should now be left
        for (Marker marker : markers.values()) {
            marker.deleteMarker();
        }
    }

    // -------------------------------------------- //
    // UTIL & SHARED
    // -------------------------------------------- //
    // Thread Safe / Asynchronous: Yes
    public String getDescription(Faction faction) {
        String ret = "<div class=\"regioninfo\">" + Conf.dynmapDescription + "</div>";

        // Name
        String name = faction.getName();
        name = ChatColor.stripColor(name);
        name = escapeHtml(name);
        ret = ret.replace("%name%", name);

        /*// Description
        String description = faction.getDescription();
        description = ChatColor.stripColor(description);
        description = escapeHtml(description);
        ret = ret.replace("%description%", description);

        // Money
        String money = "unavailable";
        if (Conf.bankEnabled && Conf.dynmapDescriptionMoney) {
            money = String.format("%.2f", Econ.getBalance(faction.getAccountId()));
        }
        ret = ret.replace("%money%", money);
         
        // Players
        Set<FPlayer> playersList = faction.getFPlayers();
        String playersCount = String.valueOf(playersList.size());
        String players = getHtmlPlayerString(playersList);

        FPlayer playersLeaderObject = faction.getFPlayerAdmin();
        String playersLeader = getHtmlPlayerName(playersLeaderObject);

        ArrayList<FPlayer> playersAdminsList = faction.getFPlayersWhereRole(Role.ADMIN);
        String playersAdminsCount = String.valueOf(playersAdminsList.size());
        String playersAdmins = getHtmlPlayerString(playersAdminsList);

        ArrayList<FPlayer> playersModeratorsList = faction.getFPlayersWhereRole(Role.MODERATOR);
        String playersModeratorsCount = String.valueOf(playersModeratorsList.size());
        String playersModerators = getHtmlPlayerString(playersModeratorsList);

        ArrayList<FPlayer> playersNormalsList = faction.getFPlayersWhereRole(Role.NORMAL);
        String playersNormalsCount = String.valueOf(playersNormalsList.size());
        String playersNormals = getHtmlPlayerString(playersNormalsList);

        ret = ret.replace("%players%", players);
        ret = ret.replace("%players.count%", playersCount);
        ret = ret.replace("%players.leader%", playersLeader);
        ret = ret.replace("%players.admins%", playersAdmins);
        ret = ret.replace("%players.admins.count%", playersAdminsCount);
        ret = ret.replace("%players.moderators%", playersModerators);
        ret = ret.replace("%players.moderators.count%", playersModeratorsCount);
        ret = ret.replace("%players.normals%", playersNormals);
        ret = ret.replace("%players.normals.count%", playersNormalsCount);*/
        return ret;
    }

    public static String getHtmlPlayerString(Collection<FPlayer> playersOfficersList) {
        String ret = "";
        for (FPlayer fplayer : playersOfficersList) {
            if (ret.length() > 0) {
                ret += ", ";
            }
            ret += getHtmlPlayerName(fplayer);
        }
        return ret;
    }

    public static String getHtmlPlayerName(FPlayer fplayer) {
        if (fplayer == null) {
            return "none";
        }
        return escapeHtml(fplayer.getName());
    }

    // Thread Safe / Asynchronous: Yes
    public boolean isVisible(Faction faction, String world) {
        if (faction == null) {
            return false;
        }
        final String factionId = String.valueOf(faction.getId());
        if (factionId == null) {
            return false;
        }
        final String factionName = faction.getName();
        if (factionName == null) {
            return false;
        }

        Set<String> visible = Conf.dynmapVisibleFactions;
        Set<String> hidden = Conf.dynmapHiddenFactions;

        if (!visible.isEmpty() && !visible.contains(factionId) && !visible.contains(factionName) && !visible.contains("world:" + world)) {
            return false;
        }

        if (hidden.contains(factionId) || hidden.contains(factionName) || hidden.contains("world:" + world)) {
            return false;
        }

        return true;
    }

}
