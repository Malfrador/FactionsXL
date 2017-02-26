/*
 * Copyright (C) 2014-2017 Trent Hensler
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
package io.github.dre2n.factionsxl.scoreboard;

import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.List;

/**
 * Adapted from FactionsUUID by drtshock.
 *
 * @author Trent Hensler, Daniel Saukel
 */
public abstract class FSidebarProvider {

    public abstract String getTitle(FPlayer fplayer);

    public abstract List<String> getLines(FPlayer fplayer);

    public String replaceTags(FPlayer fPlayer, String s) {
        return ParsingUtil.replaceScoreboardPlaceholders(fPlayer, s);
    }

    public String replaceTags(Faction faction, FPlayer fPlayer, String s) {
        return ParsingUtil.replaceScoreboardPlaceholders(faction, fPlayer, s);
    }

}
