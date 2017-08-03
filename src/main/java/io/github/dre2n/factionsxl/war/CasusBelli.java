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
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.LegalEntity;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A reason to start a war.
 *
 * @author Daniel Saukel
 */
public class CasusBelli {

    public enum Type {
        // When two factions share a border
        BORDER_FRICTION,
        // When the leader of the attacker has a claim on the throne of the defender
        CLAIM_ON_THRONE,
        // When a factions has claims on foreign land
        CONQUEST,
        // When a member of a federation is disloyal
        IMPERIAL_BAN,
        // Vassals may declare war on their lord to gain independence
        INDEPENDENCE,
        // When an ally of the faction got vassalized
        LIBERATION,
        // Permanent CB with no possible demands 
        RAID,
        // When a PU broke
        RESTORATION_OF_UNION,
        // When a faction has cores on foreign land
        RECONQUEST,
        // When a vassal declared its independence
        RESUBJAGATION
    }

    private Type type;
    private LegalEntity target;
    private Faction liberate;

    public CasusBelli(Type type, LegalEntity target) {
        this.type = type;
        this.target = target;
    }

    public CasusBelli(LegalEntity target, Faction liberate) {
        type = Type.LIBERATION;
        this.target = target;
        this.liberate = liberate;
    }

    public CasusBelli(ConfigurationSection config) {
        type = Type.valueOf(config.getString("type"));
        target = FactionsXL.getInstance().getFactionCache().getById(config.getInt("target"));
        if (type == Type.LIBERATION) {
            liberate = FactionsXL.getInstance().getFactionCache().getById(config.getInt("liberate"));
        }
    }

    /**
     * @return
     * the CB type
     */
    public Type getType() {
        return type;
    }

    /**
     * @return
     * the enemy
     */
    public LegalEntity getTarget() {
        return target;
    }

    /**
     * @return
     * the faction to liberate if the CB is LIBERATION;
     * null if CB is anything else
     */
    public Faction getFactionToLiberate() {
        return liberate;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("target", target.getId());
        serialized.put("type", type.toString());
        serialized.put("liberate", liberate.getId());
        return serialized;
    }

}
