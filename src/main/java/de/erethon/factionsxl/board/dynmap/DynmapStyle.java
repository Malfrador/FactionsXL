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

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FConfig;

/**
 * This is a modified version of mikeprimms plugin Dynmap-Factions taken from FactionsUUID by drtshock.
 *
 * @author Mike Primm, Trent Hensler, Daniel Saukel
 */
public class DynmapStyle {

    FConfig config = FactionsXL.getInstance().getFConfig();

    public static final String DEFAULT_LINE_COLOR = "#FFFFFF";
    public static final double DEFAULT_LINE_OPACITY = 0.8D;
    public static final int DEFAULT_LINE_WEIGHT = 3;
    public static final String DEFAULT_FILL_COLOR = "#E0E0E0";
    public static final double DEFAULT_FILL_OPACITY = 0.35D;
    public static final String DEFAULT_HOME_MARKER = "greenflag";
    public static final boolean DEFAULT_BOOST = false;
    public static final DynmapStyle DEFAULT_STYLE = new DynmapStyle()
            .setStrokeColor(DEFAULT_LINE_COLOR)
            .setLineOpacity(DEFAULT_LINE_OPACITY)
            .setLineWeight(DEFAULT_LINE_WEIGHT)
            .setFillColor(DEFAULT_FILL_COLOR)
            .setFillOpacity(DEFAULT_FILL_OPACITY)
            .setHomeMarker(DEFAULT_HOME_MARKER)
            .setBoost(DEFAULT_BOOST);

    public DynmapStyle() {
    }

    public DynmapStyle(DynmapStyle style) {
        lineColor = style.lineColor;
        lineOpacity = style.lineOpacity;
        lineWeight = style.lineWeight;
        fillColor = style.fillColor;
        fillOpacity = style.fillOpacity;
        homeMarker = style.homeMarker;
        boost = style.boost;
    }

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //
    public String lineColor = null;

    public int getLineColor() {
        return getColor(coalesce(this.lineColor, DEFAULT_STYLE.lineColor, DEFAULT_LINE_COLOR));
    }

    public DynmapStyle setStrokeColor(String strokeColor) {
        this.lineColor = strokeColor;
        return this;
    }

    public Double lineOpacity = null;

    public double getLineOpacity() {
        return coalesce(this.lineOpacity, DEFAULT_STYLE.lineOpacity, DEFAULT_LINE_OPACITY);
    }

    public DynmapStyle setLineOpacity(Double strokeOpacity) {
        this.lineOpacity = strokeOpacity;
        return this;
    }

    public Integer lineWeight = null;

    public int getLineWeight() {
        return coalesce(this.lineWeight, DEFAULT_STYLE.lineWeight, DEFAULT_LINE_WEIGHT);
    }

    public DynmapStyle setLineWeight(Integer strokeWeight) {
        this.lineWeight = strokeWeight;
        return this;
    }

    public String fillColor = null;

    public int getFillColor() {
        return getColor(coalesce(this.fillColor, DEFAULT_STYLE.fillColor, DEFAULT_FILL_COLOR));
    }

    public DynmapStyle setFillColor(String fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    public Double fillOpacity = null;

    public double getFillOpacity() {
        return coalesce(this.fillOpacity, DEFAULT_STYLE.fillOpacity, DEFAULT_FILL_OPACITY);
    }

    public DynmapStyle setFillOpacity(Double fillOpacity) {
        this.fillOpacity = fillOpacity;
        return this;
    }

    // NOTE: We just return the string here. We do not return the resolved Dynmap MarkerIcon object.
    // The reason is we use this class in the MConf. For serialization to work Dynmap would have to be loaded and we can't require that.
    // Using dynmap is optional.
    public String homeMarker = null;

    public String getHomeMarker() {
        return coalesce(this.homeMarker, DEFAULT_STYLE.homeMarker, DEFAULT_HOME_MARKER);
    }

    public DynmapStyle setHomeMarker(String homeMarker) {
        this.homeMarker = homeMarker;
        return this;
    }

    public Boolean boost = null;

    public boolean getBoost() {
        return coalesce(this.boost, DEFAULT_STYLE.boost, DEFAULT_BOOST);
    }

    public DynmapStyle setBoost(Boolean boost) {
        this.boost = boost;
        return this;
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //
    @SafeVarargs
    public static <T> T coalesce(T... items) {
        for (T item : items) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public static int getColor(String string) {
        int ret = 0x00FF00;
        try {
            ret = Integer.parseInt(string.substring(1), 16);
        } catch (NumberFormatException ignored) {
        }
        return ret;
    }

}
