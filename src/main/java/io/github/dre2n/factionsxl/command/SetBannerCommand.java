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
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class SetBannerCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public SetBannerCommand() {
        setCommand("setbanner");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(FMessage.HELP_SET_BANNER.getMessage());
        setPermission(FPermission.SET_BANNER.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction faction = plugin.getFactionCache().getByName(args[1]);
        if (faction == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            return;
        }

        if (!faction.getAdmin().equals(player)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.BANNER) {
            displayHelp(player);
            return;
        }

        faction.setBanner(item);
        faction.sendMessage(FMessage.CMD_SET_BANNER_SUCCESS.getMessage(), player, faction);
    }

}
