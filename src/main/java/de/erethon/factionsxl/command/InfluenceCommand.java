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
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.economy.FAccount;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.util.ParsingUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class InfluenceCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    Economy econ = plugin.getEconomyProvider();

    public InfluenceCommand() {
        setCommand("influx");
        setAliases("influence");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp("Pay money to restore full influence - /f influx [Amount]");
        setPermission(FPermission.CLAIM.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction faction = getSenderFaction(sender);
        Region region = plugin.getBoard().getByLocation(player.getLocation());
        if (faction == null) {
            return;
        }
        if (args.length <= i) {
            displayHelp(sender);
            return;
        }
        if (faction.isInWar()) {
            ParsingUtil.sendMessage(player, "&cYou can not pay for influence while at war!");
            return;
        }
        if (!(region.getOwner() == faction)) {
            ParsingUtil.sendMessage(player, "&cYou can only pay for influence in your own regions!");
            return;
        }
        double amount = 0;
        if (args.length == 2) {
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException exception) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NOT_NUMERIC.getMessage(), args[1]);
                return;
            }
        }
        if (region.getInfluence() + (amount / 10) <= 100) {
            withdraw(player, faction, amount);
            region.setInfluence((int) (region.getInfluence() + (amount / 10)));
            ParsingUtil.sendMessage(sender, "&aInfluence added");
        }
        else {
            ParsingUtil.sendMessage(sender, "&cYou are adding too much influence!");
        }
    }
    public void withdraw(CommandSender sender, Faction faction, double amount) {
        if (!faction.isPrivileged(sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        FAccount acc = faction.getAccount();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (acc.getBalance() >= amount) {
                acc.withdraw(amount);
                econ.depositPlayer(player, amount);
                faction.sendMessage(FMessage.CMD_MONEY_WITHDRAW_SUCCESS.getMessage(), player, econ.format(amount), faction);
                faction.sendMessage(FMessage.CMD_MONEY_BALANCE.getMessage(), faction, acc.getFormatted());
            } else {
                ParsingUtil.sendMessage(sender, FMessage.CMD_MONEY_WITHDRAW_FAIL.getMessage(), faction, econ.format(amount));
            }
        } else {
            acc.withdraw(amount);
            faction.sendMessage(FMessage.CMD_MONEY_WITHDRAW_SUCCESS.getMessage(), sender, econ.format(amount), faction);
            ParsingUtil.sendMessage(sender, FMessage.CMD_MONEY_BALANCE.getMessage(), faction, acc.getFormatted());
        }
    }
}