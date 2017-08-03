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
package io.github.dre2n.factionsxl.war;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.faction.LegalEntity;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Daniel Saukel
 */
public class War {

    private File file;
    private FileConfiguration config;
    private WarParty attacker;
    private WarParty defender;
    private CasusBelli cb;
    private Date startDate;

    public War(WarParty attacker, LegalEntity defender, CasusBelli cb) {
        this.attacker = attacker;
        this.defender = new WarParty(defender);
        this.cb = cb;
        startDate = Calendar.getInstance().getTime();
        this.file = new File(FactionsXL.WARS, System.currentTimeMillis() + ".yml");
        try {
            file.createNewFile();
        } catch (IOException exception) {
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public War(LegalEntity attacker, LegalEntity defender, CasusBelli cb) {
        this(new WarParty(attacker), defender, cb);
    }

    public War(File file) {
        this.file = file;
        config = YamlConfiguration.loadConfiguration(file);
        attacker = new WarParty(config.getConfigurationSection("attacker"));
        defender = new WarParty(config.getConfigurationSection("defender"));
        cb = new CasusBelli(config.getConfigurationSection("casusBelli"));
        startDate = new Date(config.getLong("startDate"));
    }

    /* Getters */
    public WarParty getAttacker() {
        return attacker;
    }

    public WarParty getDefender() {
        return defender;
    }

    public CasusBelli getCasusBelli() {
        return cb;
    }

    public Date getStartDate() {
        return startDate;
    }

    /* Serialization */
    public void save() {
        config.set("attacker", attacker.serialize());
        config.set("defender", defender.serialize());
        config.set("casusBelli", cb.serialize());
        config.set("startDate", startDate.getTime());
        try {
            config.save(file);
        } catch (IOException exception) {
        }
    }

}
