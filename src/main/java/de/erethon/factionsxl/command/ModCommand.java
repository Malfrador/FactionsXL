/*
 * Copyright (c) 2017-2019 Daniel Saukel
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
package de.erethon.factionsxl.command;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.util.ParsingUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ModCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public ModCommand() {
        setCommand("mod");
        setAliases("officer");
        setMinArgs(1);
        setMaxArgs(2);
        setHelp(FMessage.HELP_MOD.getMessage());
        setPermission(FPermission.MOD.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        i = 1;
        Faction faction = getSenderFactionOrFromArg(sender, args, 1, true);
        if (faction == null) {
            return;
        }
        if (sender instanceof Player && !faction.isAdmin((Player) sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[i]);
        if (!player.hasPlayedBefore()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_PLAYER.getMessage(args[i]));
            return;
        }

        if (!faction.getMods().contains(player)) {
            faction.getMods().add(player);
            if (player.isOnline()) {
                faction.getOnlineMods().add(player.getPlayer());
            }
            faction.sendMessage(FMessage.CMD_MOD_PROMOTE.getMessage(sender.getName(), player.getName()));

        } else {
            faction.getMods().remove(player);
            if (player.isOnline()) {
                faction.getOnlineMods().remove(player.getPlayer());
            }
            faction.sendMessage(FMessage.CMD_MOD_DEMOTE.getMessage(sender.getName(), player.getName()));
        }
    }

}
