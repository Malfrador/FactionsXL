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
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class OpenCommand extends FCommand {

    public OpenCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("open");
        setAliases("setOpen");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_OPEN.getMessage());
        setPermission(FPermission.OPEN.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Faction faction = getSenderFactionOrFromArg(sender, args, 1);
        if (faction == null) {
            return;
        }

        if (!faction.isPrivileged(sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        faction.setOpen(!faction.isOpen());
        if (faction.isOpen()) {
            faction.sendMessage(FMessage.CMD_OPEN_OPENED.getMessage(), sender);
        } else {
            faction.sendMessage(FMessage.CMD_OPEN_CLOSED.getMessage(), sender);
        }
    }

}
