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
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.scoreboard.FTeamWrapper;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class CreateVassalCommand extends FCommand {

    public CreateVassalCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("createVassal");
        setAliases("newVassal");
        setMinArgs(2);
        setMaxArgs(2);
        setHelp(FMessage.HELP_CREATE_VASSAL.getMessage());
        setPermission(FPermission.CREATE_VASSAL.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        FPlayer fPlayer = fPlayers.getByPlayer(player);
        Faction mother = fPlayer.getFaction();
        if (mother == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_JOIN_FACTION.getMessage());
            return;
        } else if (!mother.isAdmin(player)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        Location location = player.getLocation();
        Region region = board.getByLocation(location);
        if (region.getOwner() == null || !region.getOwner().equals(mother)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_CANNOT_PASS_LAND.getMessage());
            return;
        } else if (region.equals(mother.getCapital()) || mother.getRegions().size() <= 1) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_CANNOT_PASS_CAPITAL.getMessage());
            return;
        }

        if (factions.getByName(args[1]) != null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NAME_IN_USE.getMessage(), args[1]);
            return;
        }

        if (config.isEconomyEnabled()) {
            if (mother.getAccount().getBalance() < config.getPriceCreateVassal()) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NOT_ENOUGH_MONEY_FACTION.getMessage(), mother, String.valueOf(config.getPriceCreateVassal()));
                return;
            } else {
                mother.getAccount().withdraw(config.getPriceCreateVassal());
            }
        }

        OfflinePlayer leader = Bukkit.getOfflinePlayer(args[2]);
        if (!leader.hasPlayedBefore()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_PLAYER.getMessage(), args[2]);
            return;
        }
        if (mother.isAdmin(leader.getUniqueId())) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_VASSAL_IS_MOTHER_ADMIN.getMessage(), fPlayer);
            return;
        }

        mother.getMods().remove(leader);
        mother.getMembers().remove(leader);
        if (leader.isOnline()) {
            mother.getOnlineMods().remove(leader.getPlayer());
            mother.getOnlineMembers().remove(leader.getPlayer());
        }
        FTeamWrapper.updatePrefixes(mother);
        Faction vassal = factions.create(leader, location, args[1]);
        mother.getRelations().put(vassal, Relation.VASSAL);
        vassal.getRelations().put(mother, Relation.LORD);
        vassal.setAllod(false);
        ParsingUtil.broadcastMessage(FMessage.CMD_CREATE_SUCCESS.getMessage(), sender.getName(), args[1]);
        ParsingUtil.broadcastMessage(FMessage.RELATION_VASSALIZED.getMessage(), mother, vassal);
    }

}
