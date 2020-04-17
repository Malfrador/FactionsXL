/*
 *
 *  * Copyright (C) 2017-2020 Daniel Saukel, Malfrador
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.erethon.factionsxl.command;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.board.Board;
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.faction.FactionCache;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.player.FPlayer;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.CasusBelli;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;

public class IndependenceCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();
    FConfig config = plugin.getFConfig();
    Board board = plugin.getBoard();

    public IndependenceCommand() {
        setCommand("independence");
        setAliases("i", "ind");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp("&c/f independence [Name]");
        setPermission(FPermission.CREATE_VASSAL.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        FPlayer fPlayer = plugin.getFPlayerCache().getByPlayer(player);
        if (fPlayer.getFaction() == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_JOIN_FACTION.getMessage());
            return;
        }
        
        Faction faction = fPlayer.getFaction();
        if ( (faction.getFAdmin() == fPlayer) && !faction.isVassal() ) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        if (plugin.getFactionCache().getByName(args[1]) != null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NAME_IN_USE.getMessage(), args[1]);
            return;
        }
        if (faction.isVassal() && faction.getLord().getStability() > config.getStabilityIndependenceVassal()) {
            ParsingUtil.sendMessage(sender, FMessage.STABILITY_HIGH.getMessage());
            return;
        }
        if (!faction.isVassal() && faction.getStability() > config.getStabilityIndependence()) {
            ParsingUtil.sendMessage(sender, FMessage.STABILITY_HIGH.getMessage());
            return;
        }
        if (faction.isVassal() || faction.isAllod()) {
            if (!(faction.getAdmin() == player)) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
                return;
            }
            // TODO: FMessage
            MessageUtil.broadcastMessage("&aDie Fraktion &e" + faction.getName() + "&a hat die Instabilität von &e" + faction.getLord().getName() + "&a genutzt und die Unabhängigkeit erklärt!" );
            Faction oldLord = faction.getLord();
            oldLord.getRelations().remove(faction);
            faction.getRelations().remove(oldLord);
            oldLord.getCasusBelli().add(new CasusBelli(CasusBelli.Type.RECONQUEST, faction, new Date(System.currentTimeMillis() + (config.getCBLiberationExp() * FConfig.DAY))));
        }
        else {
            Location location = player.getLocation();
            Region region = board.getByLocation(location);
            faction.getMembers().remove(player);
            factions.create(player, args[1]);
            region.getClaimFactions().put(faction, Calendar.getInstance().getTime());
            Faction newFaction = factions.getByMember(player);
            region.setOwner(newFaction);
            faction.getCasusBelli().add(new CasusBelli(CasusBelli.Type.RECONQUEST, newFaction, new Date(System.currentTimeMillis() + (config.getCBLiberationExp() * FConfig.DAY))));
            MessageUtil.broadcastMessage("Aufgrund der Instabilität von &e" + faction.getName() + "&a hat sich eine neue Fraktion abgespalten: &e" + newFaction.getName() );
        }
    }
}