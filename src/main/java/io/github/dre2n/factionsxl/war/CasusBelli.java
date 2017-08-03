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

/**
 * A reason to start a war.
 *
 * @author Daniel Saukel
 */
public enum CasusBelli {

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
