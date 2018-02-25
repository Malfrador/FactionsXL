/*
 * Copyright (C) 2017-2018 Daniel Saukel
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
import io.github.dre2n.factionsxl.util.ParsingUtil;
import io.github.dre2n.factionsxl.war.War;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

/**
 * @author Daniel Saukel
 */
public class FWarSidebar extends FSidebarProvider {

    private War war;

    public FWarSidebar(War war) {
        this.war = war;
    }

    @Override
    public String getTitle(FPlayer fPlayer) {
        return replaceTags(war, fPlayer, ChatColor.GREEN + ParsingUtil.WAR_PLAYER_PARTY.getPlaceholder() + ChatColor.WHITE + " vs. " + ChatColor.RED + ParsingUtil.WAR_ENEMY.getPlaceholder());
    }

    @Override
    public List<String> getLines(FPlayer fPlayer) {
        List<String> lines = new ArrayList<>();
        FactionsXL.getInstance().getFConfig().getScoreboardWarInfo().forEach(l -> lines.add(replaceTags(war, fPlayer, l)));
        return lines;
    }

}
