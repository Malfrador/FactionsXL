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

import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * This is a modified version of mikeprimms plugin Dynmap-Factions taken from FactionsUUID by drtshock.
 *
 * @author Mike Primm, Trent Hensler, Daniel Saukel
 */
public class TempMarkerSet {

    public String label;
    public int minimumZoom;
    public int priority;
    public boolean hideByDefault;

    public MarkerSet create(MarkerAPI markerApi, String id) {
        MarkerSet ret = markerApi.createMarkerSet(id, this.label, null, false); // ("null, false" at the end means "all icons allowed, not perisistent")

        if (ret == null) {
            return null;
        }

        // Minimum Zoom
        if (this.minimumZoom > 0) {
            ret.setMinZoom(this.minimumZoom);
        }

        // Priority
        ret.setLayerPriority(this.priority);

        // Hide by Default
        ret.setHideByDefault(this.hideByDefault);

        return ret;
    }

    public void update(MarkerSet markerset) {
        // Name
        if (!markerset.getMarkerSetLabel().equals(this.label)) {
            markerset.setMarkerSetLabel(this.label);
        }

        if (this.minimumZoom > 0) {
            if (markerset.getMinZoom() != this.minimumZoom) {
                markerset.setMinZoom(this.minimumZoom);
            }
        }

        if (markerset.getLayerPriority() != this.priority) {
            markerset.setLayerPriority(this.priority);
        }

        if (markerset.getHideByDefault() != this.hideByDefault) {
            markerset.setHideByDefault(this.hideByDefault);
        }
    }

}
