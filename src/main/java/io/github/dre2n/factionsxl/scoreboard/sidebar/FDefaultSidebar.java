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
package io.github.dre2n.factionsxl.scoreboard.sidebar;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.scoreboard.FSidebarProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Adapted from FactionsUUID by drtshock.
 *
 * @author Trent Hensler, Daniel Saukel
 */
public class FDefaultSidebar extends FSidebarProvider {

    FactionsXL plugin = FactionsXL.getInstance();

    @Override
    public String getTitle(FPlayer fplayer) {
        return replaceTags(fplayer, plugin.getFConfig().getDefaultScoreboardTitle());
    }

    @Override
    public List<String> getLines(FPlayer fplayer) {
        if (fplayer.hasFaction()) {
            return getOutput(fplayer, "scoreboard.default");

        } else if (plugin.getFConfig().isScoreboardFactionlessEnabled()) {
            return getOutput(fplayer, "scoreboard.factionless");
        }

        return getOutput(fplayer, "scoreboard.default"); // no faction, factionless-board disabled
    }

    public List<String> getOutput(FPlayer fplayer, String list) {
        List<String> lines = plugin.getConfig().getStringList(list);

        if (lines == null || lines.isEmpty()) {
            return new ArrayList<>();
        }

        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            it.set(replaceTags(fplayer, it.next()));
        }

        return lines;
    }

}
