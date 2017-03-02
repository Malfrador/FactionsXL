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
import io.github.dre2n.factionsxl.scoreboard.FTeamWrapper;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class JoinCommand extends BRCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public JoinCommand() {
        setCommand("join");
        setMinArgs(1);
        setMaxArgs(2);
        setHelp(FMessage.HELP_JOIN.getMessage());
        setPermission(FPermission.JOIN.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Faction faction = plugin.getFactionCache().getByName(args[1]);
        if (faction == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            return;
        }

        Player player = (Player) sender;
        if (plugin.getFactionCache().getByMember(player) != null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_LEAVE_FACTION.getMessage());
            return;
        }

        if (faction.getInvitedPlayers().contains(player) || faction.isPrivileged(player) || faction.isOpen()) {
            if (args.length == 2) {
                faction.getMembers().add(player);
                faction.sendMessage(FMessage.FACTION_JOIN_ACCEPT.getMessage(), player);
                FTeamWrapper.updatePrefixes(faction);
            } else {
                faction.sendMessage(FMessage.FACTION_JOIN_DENY.getMessage(), player);
                ParsingUtil.sendMessage(player, FMessage.FACTION_JOIN_DENY.getMessage(), player);
            }
            faction.getInvitedPlayers().remove(player);

        } else {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_PERMISSION.getMessage());
        }
    }

}
