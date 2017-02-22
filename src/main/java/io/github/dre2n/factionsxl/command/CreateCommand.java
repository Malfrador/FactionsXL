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
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class CreateCommand extends BRCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public CreateCommand() {
        setCommand("create");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(FMessage.HELP_CMD_CREATE.getMessage());
        setPermission(FPermission.CREATE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        FPlayer fPlayer = plugin.getFPlayerCache().getByPlayer(player);
        if (fPlayer.getFaction() != null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_LEAVE_FACTION.getMessage());
            return;
        }

        Location location = player.getLocation();
        Board board = plugin.getBoard();
        if (!board.isNeutral(location)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_LAND_NOT_FOR_SALE.getMessage());
            return;
        }

        if (plugin.getFactionCache().getByName(args[1]) != null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NAME_IN_USE.getMessage(), args[1]);
            return;
        }

        plugin.getFactionCache().create(player, args[1]);
        ParsingUtil.broadcastMessage(FMessage.CMD_CREATE_SUCCESS.getMessage(), sender.getName(), args[1]);
    }

}
