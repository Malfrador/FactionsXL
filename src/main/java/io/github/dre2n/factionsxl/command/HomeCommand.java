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

import io.github.dre2n.commons.player.PlayerUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.CooldownTeleportationTask;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class HomeCommand extends FCommand {

    public HomeCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("home");
        setAliases("spawn");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_HOME.getMessage());
        setPermission(FPermission.HOME.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction faction;
        if (args.length == 2 && FPermission.hasPermission(sender, FPermission.HOME_OTHERS)) {
            faction = factions.getByName(args[1]);
        } else {
            faction = factions.getByMember(player);
        }

        if (faction == null) {
            if (args.length == 1) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_JOIN_FACTION.getMessage());
            } else if (args.length == 2) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            }
            return;
        }

        if (FPermission.hasPermission(sender, FPermission.BYPASS)) {
            PlayerUtil.secureTeleport(player, faction.getHome());
            return;
        }

        new CooldownTeleportationTask(player, faction.getHome(), true).runTaskTimer(plugin, 0L, 20L);
    }

}
