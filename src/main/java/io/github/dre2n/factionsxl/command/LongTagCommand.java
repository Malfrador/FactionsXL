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

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class LongTagCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public LongTagCommand() {
        setCommand("longTag");
        setAliases("setLongTag", "longName", "setLongName");
        setMinArgs(-1);
        setMaxArgs(-1);
        setHelp(FMessage.HELP_LONG_TAG.getMessage());
        setPermission(FPermission.TAG.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        if (args.length < 3) {
            displayHelp(sender);
            return;
        }

        Faction faction = plugin.getFactionCache().getByName(args[1]);
        if (faction == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            return;
        }

        if (!faction.isPrivileged(sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        String longName = new String();
        for (String arg : args) {
            if (args[0] != arg && args[1] != arg) {
                if (!longName.isEmpty()) {
                    longName += " ";
                }
                longName += arg;
            }
        }

        faction.setLongName(longName);
        ParsingUtil.broadcastMessage(FMessage.CMD_TAG_SUCCESS.getMessage(), faction, longName);
    }

}
