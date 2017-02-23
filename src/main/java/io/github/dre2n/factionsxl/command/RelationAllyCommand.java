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
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.faction.Relation;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class RelationAllyCommand extends BRCommand {

    FactionCache factions = FactionsXL.getInstance().getFactionCache();

    public RelationAllyCommand() {
        setCommand("ally");
        setMinArgs(1);
        setMaxArgs(2);
        setHelp(FMessage.HELP_CMD_ALLY.getMessage());
        setPermission(FPermission.RELATION.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        if (!(sender instanceof Player) && args.length < 3) {
            displayHelp(sender);
            return;
        }
        Faction faction = sender instanceof Player ? factions.getByMember((Player) sender) : null;
        String subject = args.length == 3 ? args[1] : (faction != null ? faction.getName() : null);
        if (subject == null) {
            ParsingUtil.sendMessage(sender, args.length < 3 ? FMessage.ERROR_SPECIFY_FACTION.getMessage() : FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            return;
        }
        String object = args.length == 3 ? args[2] : args[1];
        FCommandCache.RELATION.onExecute(new String[]{subject, object, Relation.ALLIANCE.toString()}, sender);
    }

}
