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

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.config.FConfig;
import java.util.ArrayDeque;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;
import org.dynmap.utils.TileFlags;

/**
 * This is a modified version of mikeprimms plugin Dynmap-Factions taken from FactionsUUID by drtshock.
 *
 * @author Mike Primm, Trent Hensler, Daniel Saukel
 */
public abstract class EngineDynmap {

    FactionsXL plugin = FactionsXL.getInstance();
    Board board = plugin.getBoard();
    FConfig config = plugin.getFConfig();

    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //
    public final static int BLOCKS_PER_CHUNK = 16;
    public final static String DYNMAP_INTEGRATION = "\u00A7dDynmap Integration: \u00A7e";

    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //
    protected String layerName;
    protected boolean layerVisible;
    protected String prefix;
    protected String markersetString;

    public DynmapAPI dynmapApi;
    public MarkerAPI markerApi;
    public MarkerSet markerset;

    public void init() {
        // Should we even use dynmap?
        if (!config.isDynmapEnabled()) {
            if (this.markerset != null) {
                this.markerset.deleteMarkerSet();
                this.markerset = null;
            }
        }
    }

    // Thread Safe / Asynchronous: No
    public boolean updateCore() {
        // Get DynmapAPI
        this.dynmapApi = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
        if (this.dynmapApi == null) {
            severe("Could not retrieve the DynmapAPI.");
            return false;
        }

        // Get MarkerAPI
        this.markerApi = this.dynmapApi.getMarkerAPI();
        if (this.markerApi == null) {
            severe("Could not retrieve the MarkerAPI.");
            return false;
        }

        return true;
    }

    // Thread Safe / Asynchronous: Yes
    public TempMarkerSet createLayer() {
        TempMarkerSet ret = new TempMarkerSet();
        ret.label = layerName;
        ret.minimumZoom = config.getDynmapLayerMinimumZoom();
        ret.hideByDefault = !layerVisible;
        return ret;
    }

    // Thread Safe / Asynchronous: No
    public boolean updateLayer(TempMarkerSet temp) {
        this.markerset = this.markerApi.getMarkerSet(markersetString);
        if (this.markerset == null) {
            this.markerset = temp.create(this.markerApi, markersetString);
            if (this.markerset == null) {
                severe("Could not create the " + layerName + "Markerset/Layer");
                return false;
            }
        } else {
            temp.update(this.markerset);
        }
        return true;
    }

    // -------------------------------------------- //
    // UTIL & SHARED
    // -------------------------------------------- //
    public static String escapeHtml(String string) {
        StringBuilder out = new StringBuilder(Math.max(16, string.length()));
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    // Thread Safe / Asynchronous: Yes
    public static void info(String msg) {
        String message = DYNMAP_INTEGRATION + msg;
        MessageUtil.log(FactionsXL.getInstance(), message);
    }

    // Thread Safe / Asynchronous: Yes
    public static void severe(String msg) {
        String message = DYNMAP_INTEGRATION + ChatColor.RED.toString() + msg;
        MessageUtil.log(FactionsXL.getInstance(), message);
    }

    enum Direction {
        XPLUS, ZPLUS, XMINUS, ZMINUS
    }

    // Find all contiguous blocks, set in target and clear in source
    public int floodFillTarget(TileFlags source, TileFlags destination, int x, int y) {
        int cnt = 0;
        ArrayDeque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{x, y});

        while (!stack.isEmpty()) {
            int[] nxt = stack.pop();
            x = nxt[0];
            y = nxt[1];
            if (source.getFlag(x, y)) { // Set in src
                source.setFlag(x, y, false); // Clear source
                destination.setFlag(x, y, true); // Set in destination
                cnt++;
                if (source.getFlag(x + 1, y)) {
                    stack.push(new int[]{x + 1, y});
                }
                if (source.getFlag(x - 1, y)) {
                    stack.push(new int[]{x - 1, y});
                }
                if (source.getFlag(x, y + 1)) {
                    stack.push(new int[]{x, y + 1});
                }
                if (source.getFlag(x, y - 1)) {
                    stack.push(new int[]{x, y - 1});
                }
            }
        }
        return cnt;
    }

}
