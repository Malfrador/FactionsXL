/*
 *
 *  * Copyright (C) 2017-2020 Daniel Saukel, Malfrador
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.erethon.factionsxl.war;

import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;

// TODO: Needs to be added to the config file

public class WarPoints {
    public void updateScore(WarParty warParty, WarAction action) {
        switch (warParty.getWar().getCasusBelli().getType()) {
            case RAID:
                switch (action) {
                    case OCCUPY:
                    case OCCUPY_CORE:
                    case OCCUPY_WAR_TARGET:
                    case OCCUPY_CLAIM:
                        break;
                    case KILL:
                        setScore(warParty, 1);
                }
            case CONQUEST:
                switch (action) {
                    case OCCUPY:
                        setScore(warParty, 5);
                    case OCCUPY_CLAIM:
                        setScore(warParty, 10);
                    case OCCUPY_CORE:
                        setScore(warParty, 15);
                    case OCCUPY_WAR_TARGET:
                        setScore(warParty, 15);
                    case OCCUPY_WAR_TARGET_CORE:
                        setScore(warParty, 20);
                    case KILL:
                        setScore(warParty, 1);
                }
            case RECONQUEST:
                switch (action) {
                    case OCCUPY:
                        setScore(warParty, 5);
                    case OCCUPY_CLAIM:
                        setScore(warParty, 10);
                    case OCCUPY_CORE:
                    case OCCUPY_WAR_TARGET:
                        setScore(warParty, 15);
                    case OCCUPY_WAR_TARGET_CORE:
                        setScore(warParty, 20);
                    case KILL:
                        setScore(warParty, 1);
                }
            case INDEPENDENCE:
                switch (action) {
                    case OCCUPY:
                    case OCCUPY_CLAIM:
                    case OCCUPY_CORE:
                        break;
                    case OCCUPY_WAR_TARGET:
                        setScore(warParty, 20);
                    case KILL:
                        setScore(warParty, 1);
                }
            case RESUBJAGATION:
                switch (action) {
                    case OCCUPY:
                    case OCCUPY_CLAIM:
                    case OCCUPY_CORE:
                        break;
                    case OCCUPY_WAR_TARGET:
                        setScore(warParty, 25);
                    case KILL:
                        setScore(warParty, 1);
                }
            case BORDER_FRICTION:
            case CLAIM_ON_THRONE:
            case IMPERIAL_BAN:
            case LIBERATION:
            case RESTORATION_OF_UNION:
                break;
        }
    }
    public void setScore(WarParty wp, int score) {
        wp.addPoints(score);
        wp.getEnemy().removePoints(score);

        for (Faction f : wp.getFactions()) {
            f.sendMessage(FMessage.WAR_SCORE_CHANGED.getMessage(String.valueOf(wp.getPoints()), String.valueOf(wp.getEnemy().getPoints())));
        }
        for (Faction f : wp.getEnemy().getFactions()) {
            f.sendMessage(FMessage.WAR_SCORE_CHANGED.getMessage(String.valueOf(wp.getPoints()), String.valueOf(wp.getEnemy().getPoints())));
        }
    }
}
