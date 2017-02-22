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
import io.github.dre2n.factionsxl.player.FPlayerCache;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class PowerCommand extends BRCommand {

    FPlayerCache fPlayers = FactionsXL.getInstance().getFPlayerCache();

    public PowerCommand() {
        setCommand("power");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_CMD_POWER.getMessage());
        setPermission(FPermission.POWER.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        FPlayer fPlayer = null;
        if (args.length == 1) {
            if (sender instanceof Player) {
                fPlayer = fPlayers.getByPlayer((Player) sender);
            } else {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_SPECIFY_PLAYER.getMessage());
                return;
            }
        } else {
            fPlayer = fPlayers.getByName(args[1]);
            if (fPlayer == null) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_PLAYER.getMessage(), args[1]);
            }
        }

        ParsingUtil.sendMessage(sender, FMessage.CMD_POWER.getMessage(), fPlayer, fPlayer.getPower());
    }

}
