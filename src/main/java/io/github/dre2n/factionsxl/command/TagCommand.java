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
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TagCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public TagCommand() {
        setCommand("tag");
        setAliases("setTag", "name", "setName");
        setMinArgs(1);
        setMaxArgs(2);
        setHelp(FMessage.HELP_TAG.getMessage());
        setPermission(FPermission.TAG.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        i = 1;
        Faction faction = null;
        if (args.length == 3) {
            faction = getSenderFactionOrFromArg(sender, args, 1, true);
            if (faction == null) {
                return;
            }
        } else {
            faction = getSenderFaction(sender);
            if (faction == null) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_SPECIFY_FACTION.getMessage());
                return;
            }
        }

        if (sender instanceof Player && !faction.isAdmin(sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        String tag = args[i].replace("&", new String());
        ParsingUtil.broadcastMessage(FMessage.CMD_TAG_SUCCESS.getMessage(), faction, tag);
        faction.setName(tag);
    }

}
