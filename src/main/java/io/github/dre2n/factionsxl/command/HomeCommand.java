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

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.misc.ProgressBar;
import io.github.dre2n.commons.player.PlayerUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class HomeCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    FConfig config = plugin.getFConfig();
    Economy econ = plugin.getEconomyProvider();

    public HomeCommand() {
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
        Faction faction = plugin.getFactionCache().getByMember(player);
        if (args.length == 2 && FPermission.hasPermission(sender, FPermission.HOME_OTHERS)) {
            faction = plugin.getFactionCache().getByName(args[1]);
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

        new HomeTask(player, faction).runTaskTimer(plugin, 0L, 20L);
    }

    public class HomeTask extends ProgressBar {

        private Player player;
        private Location location;
        private Faction target;
        private boolean teleport;

        public HomeTask(Player player, Faction target) {
            super(player, 10);
            this.player = player;
            this.target = target;
            location = player.getLocation();
        }

        @Override
        public void run() {
            super.run();
            if (player.getLocation().getBlockX() != location.getBlockX() || player.getLocation().getBlockY() != location.getBlockY() || player.getLocation().getBlockZ() != location.getBlockZ()) {
                cancel();
                MessageUtil.sendActionBarMessage(player, FMessage.ERROR_DO_NOT_MOVE.getMessage());
                return;
            }

            if (teleport) {
                if (config.isEconomyEnabled()) {
                    if (!econ.has(player, config.getPriceHomeWarp())) {
                        ParsingUtil.sendMessage(player, FMessage.ERROR_NOT_ENOUGH_MONEY.getMessage(), String.valueOf(config.getPriceHomeWarp()));
                        return;
                    } else {
                        econ.withdrawPlayer(player, config.getPriceHomeWarp());
                    }
                }
                PlayerUtil.secureTeleport(player, target.getHome());
            }

            if (secondsLeft == 0) {
                teleport = true;
            }
        }

    }

}
