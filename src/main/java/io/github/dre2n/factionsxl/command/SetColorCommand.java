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
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class SetColorCommand extends BRCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public static final String RRGGBB = "#[0-9A-F]{6}";

    public SetColorCommand() {
        setCommand("setColor");
        setMinArgs(3);
        setMaxArgs(3);
        setHelp(FMessage.HELP_CMD_SET_COLOR.getMessage());
        setPermission(FPermission.SET_COLOR.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Faction faction = plugin.getFactionCache().getByName(args[1]);
        if (faction == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            return;
        }

        if (!faction.getAdmin().equals(sender) && !(sender instanceof Player)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        String fill = args[2];
        if (!fill.startsWith("#")) {
            fill = "#" + fill;
        }
        String line = args[3];
        if (!line.startsWith("#")) {
            line = "#" + line;
        }
        if (!fill.matches(RRGGBB) || !line.matches(RRGGBB)) {
            displayHelp(sender);
            return;
        }
        faction.setMapColor(fill, line);
        faction.sendMessage(FMessage.CMD_SET_COLOR_SUCCESS.getMessage(), sender, fill, line);
    }

}
