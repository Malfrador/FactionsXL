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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.entity.Relation;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.WarPartyRole;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * @author Daniel Saukel
 */
public class AnnexCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    FConfig config = plugin.getFConfig();

    public AnnexCommand() {
        setCommand("annex");
        setAliases("a");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp("Annex land for your faction. Can only be used with influence below 20.");
        setPermission(FPermission.CLAIM.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction faction = getSenderFactionOrFromArg(sender, args, 1);
        Region region = plugin.getBoard().getByLocation(player.getLocation());
        Faction annexFrom = plugin.getBoard().getByLocation(player.getLocation()).getOwner();
        Bukkit.broadcastMessage("" + faction.getCapital().toString());
        Bukkit.broadcastMessage("" + region.toString());
        if (faction == null) {
            return;
        }
        if (!faction.isPrivileged(player)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        if (faction.isInWar() && annexFrom.isInWar()) {
            double price;
            if (faction.getRelation(annexFrom) == Relation.ENEMY) {
                if (annexFrom.getPower() < annexFrom.getTerritoryWorth()) {
                    if (region.getInfluence() <= 10) {
                        price = region.getClaimPrice(faction) * (region.getInfluence() + 1); // Multiply base price by influence. You can annex earlier, but its more expensive
                        sender.sendMessage("Preis:" + region.getClaimPrice(faction));
                        if (faction.getAccount().getBalance() < price) {
                            ParsingUtil.sendMessage(player, FMessage.ERROR_NOT_ENOUGH_MONEY_FACTION.getMessage(), faction, String.valueOf(price));
                            return;
                        } else {
                            ParsingUtil.sendMessage(player, FMessage.FACTION_PAID.getMessage(), faction, String.valueOf(price));
                            faction.getAccount().withdraw(price);
                            region.setOwner(faction);
                            faction.sendMessage("&aIhr habt erfolgeich eine Region eingenommen: &7" + region.getName());
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                        }
                    } else {
                        MessageUtil.sendMessage(player, "&cEnemy influence to high! Needs to be less then 10.");
                        return;
                    }
                }
                else {
                    MessageUtil.sendMessage(player, "&cEnemy Power to high. Needs to be less than &e" + annexFrom.getTerritoryWorth() + "&c.");
                    return;
                    }

            } else {
                MessageUtil.sendMessage(player, "&cYou can only annex enemy regions.");
                return;
            }
        }
        else {
                MessageUtil.sendMessage(player, "&cYou and the owner of this region are not at war!");
                return;
            }


    }
}