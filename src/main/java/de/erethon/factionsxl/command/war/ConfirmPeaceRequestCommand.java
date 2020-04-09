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
import de.erethon.factionsxl.entity.Relation;
import de.erethon.factionsxl.entity.RelationRequest;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.player.FPlayer;
import de.erethon.factionsxl.scoreboard.FScoreboard;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.WarCache;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.peaceoffer.PeaceOffer;
import de.erethon.factionsxl.war.peaceoffer.SeparatePeaceOffer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.server.BroadcastMessageEvent;

import java.security.SecurityPermission;
import java.util.*;

/**
 * @author Daniel Saukel
 */
public class ConfirmPeaceRequestCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    WarCache wars = FactionsXL.getInstance().getWarCache();

    public ConfirmPeaceRequestCommand() {
        setCommand("confirmPeace");
        setMinArgs(1);
        setMaxArgs(5);
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
        if (!(war.getAttacker().getFactions().contains(faction)) && !(war.getDefender().getFactions().contains(faction))) {
            MessageUtil.sendMessage(sender, "&cYou are not participating in this war.");
            return;
        }
        if (args.length > 3) {
            Faction subjectFaction = plugin.getFactionCache().getByName(args[4]);

            Faction objectFaction = plugin.getFactionCache().getByName(args[3]);
            if (objectFaction == null) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[3]);
                return;
            }

            if (subjectFaction == objectFaction) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_OWN_FACTION.getMessage());
                return;
            }
            Collection<SeparatePeaceOffer> requests = subjectFaction.getRequests(SeparatePeaceOffer.class);
            for (SeparatePeaceOffer request : requests) {
                HashSet<SeparatePeaceOffer> toRemove = new HashSet<>();
                if (request.getSubject().equals(subjectFaction) && request.getObject().equals(objectFaction)) {
                    toRemove.add(request);
                    objectFaction.sendMessage(FMessage.RELATION_DENIED.getMessage(), subjectFaction, objectFaction);
                    subjectFaction.sendMessage(FMessage.RELATION_DENIED.getMessage(), subjectFaction, objectFaction);
                }
                subjectFaction.getRequests().removeAll(toRemove);
                return;
            }
            SeparatePeaceOffer matching = null;
            for (SeparatePeaceOffer request : requests) {
                if (request.getSubject().equals(objectFaction) && request.getObject().equals(subjectFaction)) {
                    matching = request;
                    break;
                }
            }
            if (args[2].equals("-deny") && matching.canPay() && !matching.isOffer()) {
                MessageUtil.broadcastMessage("&aDie Fraktion &e" + objectFaction + "&a hat ein Friedensangebot abgelehnt. Die Kriegsermüdung ist leicht gestiegen.");
                 objectFaction.setExhaustion(objectFaction.getExhaustion() + 5);

            }
            if (matching != null) {
                matching.confirm();
                return;
            }

            MessageUtil.broadcastMessage("&aDie beiden Fraktionen &e" + subjectFaction.getName() + "&a und &e" + objectFaction.getName() + "&a haben ihren Krieg beendet.");

        } else {
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
            Collection<PeaceOffer> requests = null;
            try {
                requests = wp.getRequests(PeaceOffer.class);
            } catch (NullPointerException e) {
                MessageUtil.sendMessage(player, "&cNo peace requests or peace request is empty.");
            }
            if (requests == null) {
                return;
            }
            PeaceOffer peace = null;
            for (PeaceOffer p : requests) {
                if (p.getWar() == war) {
                    peace = p;
                    break;
                }
            }
            if (args[2].equals("-deny") && peace.canPay() && !peace.isOffer()) {
                MessageUtil.broadcastMessage("&aDie Kriegspartei &e" + wp.getName() + "&a hat ein Friedensangebot abgelehnt. Ihre Kriegsermüdung ist gestiegen.");
                for (Faction f : wp.getFactions()) {
                    f.setExhaustion(f.getExhaustion() + 5);
                }
            }
            if (!peace.canPay()) {
                MessageUtil.sendMessage(player, "&cYou or the other faction can not afford this.");
                return;
            }
            peace.confirm();
            FScoreboard.updateAllProviders();


        }
    }
}