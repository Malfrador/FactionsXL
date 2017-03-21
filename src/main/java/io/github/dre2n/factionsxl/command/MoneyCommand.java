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
import io.github.dre2n.factionsxl.economy.FAccount;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class MoneyCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    Economy econ = plugin.getEconomyProvider();

    public MoneyCommand() {
        setCommand("money");
        setAliases("m", "econ", "economy");
        setMinArgs(2);
        setMaxArgs(3);
        setHelp(FMessage.HELP_MONEY.getMessage());
        setPermission(FPermission.MONEY.getNode());
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

        double amount = 0;
        if (args.length >= 4) {
            try {
                amount = Double.parseDouble(args[3]);
            } catch (NumberFormatException exception) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NOT_NUMERIC.getMessage(), args[3]);
                return;
            }
        }

        switch (args[2].toLowerCase()) {
            case "b":
            case "balance":
                ParsingUtil.sendMessage(sender, FMessage.CMD_MONEY_BALANCE.getMessage(), faction, faction.getAccount().getFormatted());
                break;
            case "d":
            case "deposit":
                deposit(sender, faction, amount);
                break;
            case "w":
            case "withdraw":
                withdraw(sender, faction, amount);
                break;
        }
    }

    public void deposit(CommandSender sender, Faction faction, double amount) {
        if (!faction.isPrivileged(sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        FAccount acc = faction.getAccount();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (econ.has(player, amount)) {
                econ.withdrawPlayer(player, amount);
                acc.deposit(amount);
                faction.sendMessage(FMessage.CMD_MONEY_DEPOSIT_SUCCESS.getMessage(), player, econ.format(amount), faction);
                faction.sendMessage(FMessage.CMD_MONEY_BALANCE.getMessage(), faction, acc.getFormatted());
            } else {
                ParsingUtil.sendMessage(sender, FMessage.CMD_MONEY_DEPOSIT_FAIL.getMessage(), econ.format(amount));
            }
        } else {
            acc.deposit(amount);
            faction.sendMessage(FMessage.CMD_MONEY_DEPOSIT_SUCCESS.getMessage(), sender, econ.format(amount), faction);
            ParsingUtil.sendMessage(sender, FMessage.CMD_MONEY_BALANCE.getMessage(), faction, acc.getFormatted());
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
