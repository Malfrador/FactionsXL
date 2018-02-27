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
package de.erethon.factionsxl.board.dynmap;

import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

/**
 * This is a modified version of mikeprimms plugin Dynmap-Factions taken from FactionsUUID by drtshock.
 *
 * @author Mike Primm, Trent Hensler, Daniel Saukel
 */
public class TempMarker {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //
    public String label;
    public String world;
    public double x;
    public double y;
    public double z;
    public String iconName;
    public String description;

    // -------------------------------------------- //
    // CREATE
    // -------------------------------------------- //
    public Marker create(MarkerAPI markerApi, MarkerSet markerset, String markerId) {
        Marker ret = markerset.createMarker(markerId, this.label, this.world, this.x, this.y, this.z, getMarkerIcon(markerApi, this.iconName), false // not persistent
        );

        if (ret == null) {
            return null;
        }

        ret.setDescription(this.description);

        return ret;
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //
    public void update(MarkerAPI markerApi, Marker marker) {
        if (!this.world.equals(marker.getWorld()) || this.x != marker.getX() || this.y != marker.getY() || this.z != marker.getZ()) {
            marker.setLocation(this.world, this.x, this.y, this.z);
        }

        if (!marker.getLabel().equals(this.label)) {
            marker.setLabel(this.label);
        }

        MarkerIcon icon = getMarkerIcon(markerApi, this.iconName);
        if (marker.getMarkerIcon() == null || marker.getMarkerIcon().equals(icon)) {
            marker.setMarkerIcon(icon);
        }

        if (!marker.getDescription().equals(this.description)) {
            marker.setDescription(this.description);
        }
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //
    public static MarkerIcon getMarkerIcon(MarkerAPI markerApi, String name) {
        MarkerIcon ret = markerApi.getMarkerIcon(name);
        if (ret == null) {
            ret = markerApi.getMarkerIcon(DynmapStyle.DEFAULT_HOME_MARKER);
        }
        return ret;
    }

}
