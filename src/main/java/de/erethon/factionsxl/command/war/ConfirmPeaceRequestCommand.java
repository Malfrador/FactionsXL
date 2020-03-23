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
package de.erethon.factionsxl.command.war;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.command.FCommand;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.entity.RelationRequest;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.player.FPlayer;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.WarCache;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.peaceoffer.PeaceOffer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.server.BroadcastMessageEvent;

import java.util.Collection;
import java.util.Set;

/**
 * @author Daniel Saukel
 */
public class ConfirmPeaceRequestCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    WarCache wars = FactionsXL.getInstance().getWarCache();

    public ConfirmPeaceRequestCommand() {
        setCommand("confirmPeace");
        setMinArgs(1);
        setMaxArgs(4);
        setHelp("Internal command to confirm a peace request");
        setPermission(FPermission.WAR.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        FPlayer fPlayer = getFSender(sender);
        Faction faction = getSenderFaction(sender);
        if (!(fPlayer.hasFaction())) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_PLAYER_NOT_IN_ANY_FACTION.getMessage());
            return;
        }
        if (!(faction.isAdmin(sender))) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        War war = plugin.getWarCache().getByDate(Long.parseLong(args[1]));
        if ( !(war.getAttacker().getFactions().contains(faction)) && !(war.getDefender().getFactions().contains(faction)) ) {
            MessageUtil.sendMessage(sender, "&cYou are not participating in this war.");
            return;
        }
        Set<WarParty> warParties = faction.getWarParties();
        WarParty wp = null;
        for (WarParty w : warParties) {
            if (w.getLeader() == faction) {
                wp = w;
                break;
            }
        }
        if (wp == null) {
            MessageUtil.sendMessage(sender, "&cYou are not the leader of this war.");
            return;
        }
        Collection<PeaceOffer> requests = wp.getRequests(PeaceOffer.class);
        PeaceOffer peace = null;
        for (PeaceOffer p : requests) {
            if (p.getWar() == war) {
                peace = p;
                break;
            }
        }
        if (!peace.canPay()) {
            MessageUtil.sendMessage(player, "&cYou can not afford this!");
            return;
        }
        peace.confirm();


    }

    }