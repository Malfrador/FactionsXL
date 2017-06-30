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
import io.github.dre2n.factionsxl.util.LazyChunk;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.dynmap.markers.AreaMarker;
import org.dynmap.utils.TileFlags;

/**
 * This is a modified version of mikeprimms plugin Dynmap-Factions taken from FactionsUUID by drtshock.
 *
 * @author Mike Primm, Trent Hensler, Daniel Saukel
 */
public class RegionMap extends EngineDynmap {

    public RegionMap() {
        layerName = "Regions";
        layerVisible = false;
        prefix = "regions_";
        markersetString = prefix + "markerset";
    }

    @Override
    public void init() {
        super.init();

        // Shedule non thread safe sync at the end!
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

                final Map<String, TempAreaMarker> areas = createAreas();

                if (!updateCore()) {
                    return;
                }

                // createLayer() is thread safe but it makes use of fields set in updateCore() so we must have it after.
                if (!updateLayer(createLayer())) {
                    return;
                }

                updateAreas(areas);
            }
        }, 100L, 100L);
    }

    @Override
    public TempMarkerSet createLayer() {
        TempMarkerSet ret = super.createLayer();
        ret.priority = config.getDynmapLayerPriorityRegion();
        return ret;
    }

    // -------------------------------------------- //
    // UPDATE: AREAS
    // -------------------------------------------- //
    // Thread Safe: YES
    public Map<String, TempAreaMarker> createAreas() {
        Map<String, Set<Region>> worldRegions = createWorldRegions();
        return createAreas(worldRegions);
    }

    // Thread Safe: YES
    public Map<String, Set<Region>> createWorldRegions() {
        // Create map "world name --> faction --> set of chunk coords"
        Map<String, Set<Region>> worldRegions = new HashMap<>();

        for (Region region : board.getRegions()) {
            String world = region.getWorld().getName();

            Set<Region> regions = worldRegions.get(world);
            if (regions == null) {
                regions = new HashSet<>();
                worldRegions.put(world, regions);
            }

            regions.add(region);
        }

        return worldRegions;
    }

    // Thread Safe: YES
    public Map<String, TempAreaMarker> createAreas(Map<String, Set<Region>> worldRegions) {
        Map<String, TempAreaMarker> ret = new HashMap<>();

        // For each world
        for (Map.Entry<String, Set<Region>> entry : worldRegions.entrySet()) {
            String world = entry.getKey();
            Set<Region> regions = entry.getValue();

            // For each regions and its chunks in that world
            for (Region region : regions) {
                Map<String, TempAreaMarker> worldFactionMarkers = createAreas(world, region);
                ret.putAll(worldFactionMarkers);
            }
        }

        return ret;
    }

    // Thread Safe: YES
    // Handle specific faction on specific world
    // "handle faction on world"
    public Map<String, TempAreaMarker> createAreas(String world, Region region) {
        Collection<LazyChunk> chunks = region.getChunks();
        Map<String, TempAreaMarker> ret = new HashMap<>();

        // ... and has any chunks ...
        if (chunks.isEmpty()) {
            return ret;
        }

        // Index of polygon for given faction
        int markerIndex = 0;

        // Create the info window
        String description = getDescription(region);

        // Fetch Style
        DynmapStyle style = this.getStyle(region);

        // Loop through chunks: set flags on chunk map
        TileFlags allChunkFlags = new TileFlags();
        LinkedList<LazyChunk> allChunks = new LinkedList<>();
        for (LazyChunk chunk : chunks) {
            allChunkFlags.setFlag(chunk.getX(), chunk.getZ(), true); // Set flag for chunk
            allChunks.addLast(chunk);
        }

        // Loop through until we don't find more areas
        while (allChunks != null) {
            TileFlags ourChunkFlags = null;
            LinkedList<LazyChunk> ourChunks = null;
            LinkedList<LazyChunk> newChunks = null;

            int minimumX = Integer.MAX_VALUE;
            int minimumZ = Integer.MAX_VALUE;
            for (LazyChunk chunk : allChunks) {
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
            String markerId = prefix + world + "__" + region.getId() + "__" + markerIndex;

            TempAreaMarker temp = new TempAreaMarker();
            temp.label = region.getName();
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
    // UTIL & SHARED
    // -------------------------------------------- //
    // Thread Safe / Asynchronous: Yes
    public String getDescription(Region region) {
        String ret = "<div class=\"regioninfo\">" + config.getDynmapDescriptionRegion() + "</div>";
        return ParsingUtil.replaceRegionPlaceholders(ret, region);
    }

    // Thread Safe / Asynchronous: Yes
    public DynmapStyle getStyle(Region region) {
        DynmapStyle ret = region.getDynmapStyle();
        return ret != null ? ret : DynmapStyle.DEFAULT_STYLE;
    }

}
