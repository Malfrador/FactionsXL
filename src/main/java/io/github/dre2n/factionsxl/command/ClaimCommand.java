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
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ClaimCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    FConfig config = plugin.getFConfig();

    public ClaimCommand() {
        setCommand("claim");
        setAliases("annex");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_CLAIM.getMessage());
        setPermission(FPermission.CLAIM.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction faction = getSenderFactionOrFromArg(sender, args, 1);
        if (faction == null) {
            return;
        }

        if (!faction.isPrivileged(player)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        Region region = plugin.getBoard().getByLocation(player.getLocation());
        if (region == null || !region.isAnnexable()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_LAND_NOT_FOR_SALE.getMessage());
            if (region != null && region.getCoreFactions().containsKey(faction)) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_REGION_IS_CORE.getMessage(), region, faction);
            } else if (region != null && region.getClaimFactions().containsKey(faction)) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_REGION_IS_ALREADY_CLAIMED.getMessage(), region, faction);
            }
            return;
        }

        if (plugin.getFConfig().isEconomyEnabled()) {
            double price = region.getClaimPrice(faction);
            if (faction.getAccount().getBalance() < price) {
                ParsingUtil.sendMessage(player, FMessage.ERROR_NOT_ENOUGH_MONEY_FACTION.getMessage(), faction, String.valueOf(price));
                return;
            } else {
                ParsingUtil.sendMessage(player, FMessage.FACTION_PAID.getMessage(), faction, String.valueOf(price));
                faction.getAccount().withdraw(price);
            }
        }

        region.setOwner(faction);
        faction.getRegions().add(region);
        ParsingUtil.sendMessage(sender, FMessage.CMD_CLAIM_SUCCESS.getMessage(), region);
    }

}
