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
import io.github.dre2n.factionsxl.faction.Faction;
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
public class FInfoSidebar extends FSidebarProvider {

    FactionsXL plugin = FactionsXL.getInstance();

    private final Faction faction;

    public FInfoSidebar(Faction faction) {
        this.faction = faction;
    }

    @Override
    public String getTitle(FPlayer fplayer) {
        return faction.getRelation(fplayer).getColor() + faction.getName();
    }

    @Override
    public List<String> getLines(FPlayer fplayer) {
        List<String> lines = new ArrayList<>(plugin.getFConfig().getScoreboardFactionInfo());

        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            it.set(replaceTags(faction, fplayer, it.next()));
        }

        return lines;
    }

}
