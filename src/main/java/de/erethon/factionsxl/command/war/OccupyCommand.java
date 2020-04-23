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
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.command.FCommand;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.WarParty;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Set;

import static de.erethon.factionsxl.war.CasusBelli.Type.*;

/**
 * @author Malfrador
 */
public class OccupyCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    FConfig config = plugin.getFConfig();

    public OccupyCommand() {
        setCommand("occupy");
        setAliases("a", "annex", "o");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.WAR_OCCUPY_HELP.getMessage());
        setPermission(FPermission.CLAIM.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction faction = getSenderFactionOrFromArg(sender, args, 1);
        Region region = plugin.getBoard().getByLocation(player.getLocation());
        if (region == null) {
            MessageUtil.sendMessage(player, FMessage.ERROR_LAND_WILDERNESS.getMessage());
            return;
        }
        if (region.getOwner() == null) {
            MessageUtil.sendMessage(player, FMessage.ERROR_LAND_WILDERNESS.getMessage());
            return;
        }

        Faction annexFrom = region.getOwner();
        if (region.getOccupant() != null) {
            annexFrom = region.getOccupant();
        }
        if (!faction.isPrivileged(player)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        if ( !(faction.isInWar() && annexFrom.isInWar()) ) {
            MessageUtil.sendMessage(player, FMessage.WAR_OCCUPY_NOT_AT_WAR.getMessage());
            return;
        }

        if (annexFrom == faction) {
            ParsingUtil.sendMessage(sender, FMessage.WAR_OCCUPY_ALREADY_OCCUPIED.getMessage());
            return;
        }

        Set<War> warSet = plugin.getWarCache().getByFaction(faction);
        War war = null;
        for (War w : warSet) {
            if ((w.getAttacker().getFactions().contains(annexFrom)) || (w.getDefender().getFactions().contains(annexFrom))) {
                war = w;
            }
        }
        if (war == null) {
            ParsingUtil.sendMessage(sender, FMessage.WAR_OCCUPY_NOT_AT_WAR.getMessage());
            return;
        }

        if (war.getCasusBelli().getType() == RAID) {
            ParsingUtil.sendMessage(sender, FMessage.WAR_OCCUPY_RAID.getMessage());
            return;
        }

        double price;
        if (!(war.getTruce())) {
            if (region.getInfluence() <= config.getInfluenceNeeded() || ( ( region.getCoreFactions().containsKey(faction) ) && (config.getInfluenceNeeded() * 2 >= region.getInfluence()) ) ) {
                price = region.getClaimPrice(faction) * (region.getInfluence() + 1); // Multiply base price by influence. You can annex earlier, but its more expensive
                // Price for region with cores of owner is price * 2
                if (region.getCoreFactions().containsKey(annexFrom)) {
                    price = price * 2;
                }
                // Price for regions with own claims is price/4
                if (region.getClaimFactions().containsKey(faction)) {
                    price = price / 4;
                }
                if (faction.getAccount().getBalance() < price) {
                    ParsingUtil.sendMessage(player, FMessage.ERROR_NOT_ENOUGH_MONEY_FACTION.getMessage(), faction, String.valueOf(price));
                } else {
                    ParsingUtil.sendMessage(player, FMessage.FACTION_PAID.getMessage(), faction, String.valueOf(price));
                    faction.getAccount().withdraw(price);
                    for (WarParty wp : faction.getWarParties()) {
                        if (wp.getFactions().contains(faction)) {
                            if (region.getCoreFactions().containsKey(region.getOwner()) && wp.getWar().getCasusBelli().getType() == (RECONQUEST)) {
                                wp.addPoints(20);
                                wp.getEnemy().removePoints(20);
                            }
                            else if ( (wp.getWar().getCasusBelli().getType() == (CONQUEST)) && wp.getWar().getCasusBelli().getTarget() == region.getOwner() ) {
                                wp.addPoints(15);
                                wp.getEnemy().removePoints(15);
                            }
                            else {
                                wp.addPoints(5);
                                wp.getEnemy().removePoints(5);
                            }
                            faction.sendMessage(FMessage.WAR_SCORE_CHANGED.getMessage(String.valueOf(wp.getPoints()), String.valueOf(wp.getEnemy().getPoints())));
                        }
                    }
                    region.setOccupant(faction);
                    if (region.getOwner() == region.getOccupant()) {
                        region.clearOccupant();
                        region.setOwner(faction);
                        faction.setExhaustion(faction.getExhaustion() - 4);
                    }

                    region.getClaimFactions().putIfAbsent(annexFrom, Calendar.getInstance().getTime());
                    region.setInfluence((int) (config.getInfluenceNeeded() + 10));
                    annexFrom.setExhaustion(annexFrom.getExhaustion() + 5);

                    faction.sendMessage(FMessage.WAR_OCCUPY_SUCCESS.getMessage(), region);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                }
            } else {
                MessageUtil.sendMessage(player, FMessage.WAR_OCCUPY_INFLUENCE_TOO_HIGH.getMessage());
            }
        } else {
            MessageUtil.sendMessage(player, FMessage.WAR_OCCUPY_TRUCE.getMessage());
        }



    }
}