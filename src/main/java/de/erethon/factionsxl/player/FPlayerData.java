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
package de.erethon.factionsxl.player;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.config.DREConfig;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.entity.Request;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

/**
 * @author Daniel Saukel
 */
public class FPlayerData extends DREConfig {

    FactionsXL plugin = FactionsXL.getInstance();

    public static final int CONFIG_VERSION = 4;

    private String lastName;
    private String title;
    private long timeLastPlayed;
    private double powerBase;
    private boolean scoreboardEnabled = plugin.getFConfig().isScoreboardEnabledByDefault();
    private boolean anthemsEnabled = true;
    private Location home;
    private List<Request> requests;

    public FPlayerData(File file) {
        super(file, CONFIG_VERSION);

        if (initialize) {
            initialize();
        }
        load();
    }

    /* Getters and setters */
    /**
     * @return
     * the name the player had when he was online for the last time
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param name
     * the name to set
     */
    public void setLastName(String name) {
        lastName = name;
    }

    /**
     * @return
     * the player's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     * the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return
     * the last time when the player played
     */
    public long getTimeLastPlayed() {
        return timeLastPlayed;
    }

    /**
     * @param time
     * the last time when the player played to set
     */
    public void setTimeLastPlayed(long time) {
        timeLastPlayed = time;
    }

    /**
     * @return
     * the power base value
     */
    public double getPowerBase() {
        return powerBase;
    }

    /**
     * @return
     * if the scoreboard is enabled
     */
    public boolean isScoreboardEnabled() {
        return scoreboardEnabled;
    }

    /**
     * @param enabled
     * if the scoreboard shall be enabled
     */
    public void setScoreboardEnabled(boolean enabled) {
        scoreboardEnabled = enabled;
    }

    /**
     * @return
     * if the scoreboard is enabled
     */
    public boolean areAnthemsEnabled() {
        return anthemsEnabled;
    }

    /**
     * @param enabled
     * if faction anthems shall be enabled
     */
    public void setAnthemsEnabled(boolean enabled) {
        anthemsEnabled = enabled;
    }

    /**
     * @return
     * the home location
     */
    public Location getHome() {
        return home;
    }

    /**
     * @param location
     * the location to set as the home location
     */
    public void setHome(Location location) {
        home = location;
    }

    /**
     * @return
     * the requests this player has
     */
    public List<Request> getRequests() {
        return requests;
    }

    /* Serialization */
    @Override
    public void initialize() {
        MessageUtil.log(plugin, FMessage.LOG_NEW_PLAYER_DATA.getMessage(file.getName()));
        save();
    }

    @Override
    public void load() {
        if (config.contains("lastName")) {
            lastName = config.getString("lastName");
        }
        title = config.getString("title", new String());
        if (config.contains("timeLastPlayed")) {
            timeLastPlayed = config.getLong("timeLastPlayed");
        }
        scoreboardEnabled = config.getBoolean("scoreboardEnabled", scoreboardEnabled);
        anthemsEnabled = config.getBoolean("anthemsEnabled", anthemsEnabled);
        home = (Location) config.get("home");
        requests = (List<Request>) config.getList("requests", new ArrayList<>());
        FactionsXL.debug("Loaded " + this);
    }

    @Override
    public void save() {
        config.set("lastName", lastName);
        config.set("title", title);
        config.set("timeLastPlayed", timeLastPlayed);
        config.set("scoreboardEnabled", scoreboardEnabled);
        config.set("anthemsEnabled", anthemsEnabled);
        config.set("home", home);
        config.set("requests", requests);
        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "FPlayerData{lastName=" + lastName + "}";
    }

}