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
package io.github.dre2n.factionsxl.command;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.scoreboard.FScoreboard;
import io.github.dre2n.factionsxl.scoreboard.sidebar.FDefaultSidebar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ScoreboardCommand extends FCommand {

    public ScoreboardCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("scoreboard");
        setAliases("sb");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(FMessage.HELP_SCOREBOARD.getMessage());
        setPermission(FPermission.SCOREBOARD.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        FPlayer fPlayer = fPlayers.getByPlayer((Player) sender);
        FScoreboard board = FScoreboard.get(fPlayer);

        if (board == null) {
            FScoreboard.init(fPlayer);
            board = FScoreboard.get(fPlayer);
            board.setDefaultSidebar(new FDefaultSidebar(), config.getScoreboardUpdateInterval());
            board.setSidebarVisibility(fPlayer.isScoreboardEnabled());
        }

        board.setSidebarVisibility(!fPlayer.isScoreboardEnabled());
        fPlayer.setScoreboardEnabled(!fPlayer.isScoreboardEnabled());
    }

}
