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
import io.github.dre2n.factionsxl.faction.FBull;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class CreateBullCommand extends FCommand {

    public CreateBullCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("createBull");
        setMinArgs(-1);
        setMaxArgs(-1);
        setHelp(FMessage.HELP_CREATE_BULL.getMessage());
        setPermission(FPermission.CREATE_BULL.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.WRITTEN_BOOK) {
            displayHelp(player);
            return;
        }
        String author = new String();
        for (String arg : args) {
            if (args[0] != arg) {
                if (!author.isEmpty()) {
                    author += " ";
                }
                author += arg;
            }
        }

        player.getInventory().addItem(FBull.create(item, author));
        ParsingUtil.sendMessage(player, FMessage.CMD_CREATE_BULL_SUCCESS.getMessage(), author);
    }

}
