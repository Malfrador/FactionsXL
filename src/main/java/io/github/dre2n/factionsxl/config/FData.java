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
package io.github.dre2n.factionsxl.config;

import io.github.dre2n.factionsxl.FactionsXL;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Daniel Saukel
 */
public class FData {

    public static final File FILE = new File(FactionsXL.getInstance().getDataFolder(), "data.yml");

    public FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
    public long lastNewDay;

    public FData() {
        lastNewDay = config.getLong("lastNewDay");
    }

    public void save() {
        config.set("lastNewDay", lastNewDay);

        try {
            config.save(FILE);
        } catch (IOException exception) {
        }
    }

}
