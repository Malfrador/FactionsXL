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
package io.github.dre2n.factionsxl.command;

import io.github.dre2n.commons.command.BRCommand;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.scoreboard.FScoreboard;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ScoreboardCommand extends BRCommand {

    public ScoreboardCommand() {
        setCommand("scoreboard");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(FMessage.HELP_CMD_SCOREBOARD.getMessage());
        setPermission(FPermission.SCOREBOARD.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        FPlayer fPlayer = FactionsXL.getInstance().getFPlayerCache().getByPlayer((Player) sender);
        FScoreboard board = FScoreboard.get(fPlayer);

        board.setSidebarVisibility(!fPlayer.isScoreboardEnabled());
        fPlayer.setScoreboardEnabled(!fPlayer.isScoreboardEnabled());
    }

}
