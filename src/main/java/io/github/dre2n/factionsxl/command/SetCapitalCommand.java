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
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class SetCapitalCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public SetCapitalCommand() {
        setCommand("setCapital");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(FMessage.HELP_SET_CAPITAL.getMessage());
        setPermission(FPermission.SET_CAPITAL.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction faction = plugin.getFactionCache().getByMember(player);
        if (faction == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_JOIN_FACTION.getMessage());
            return;
        }
        if (!faction.isAdmin(player)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        Region region = plugin.getBoard().getByLocation(player.getLocation());
        if (region == null || region.isNeutral()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_LAND_WILDERNESS.getMessage());
            return;
        }
        if (!region.getOwner().equals(faction)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_LAND_NOT_FOR_SALE.getMessage());
            return;
        }

        faction.setCapital(region);
        ParsingUtil.broadcastMessage(FMessage.CMD_SET_CAPITAL_SUCCESS.getMessage(), faction, region);
    }

}
