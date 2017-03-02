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

import io.github.dre2n.commons.command.BRCommand;
import io.github.dre2n.commons.util.EnumUtil;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.board.RegionType;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.Calendar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class RegionsCommand extends BRCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();
    Board board = plugin.getBoard();

    public RegionsCommand() {
        setCommand("regions");
        setMinArgs(0);
        setMaxArgs(2);
        setHelp(FMessage.HELP_CMD_REGIONS.getMessage());
        setPermission(FPermission.REGIONS.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        if (args.length == 1 || !(sender instanceof Player)) {
            for (Region region : plugin.getBoard().getRegions()) {
                sender.sendMessage(region.getId() + " / " + region.getName());
            }
            return;
        }

        if (args.length < 3 || !(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;
        Region region = board.getByLocation(player.getLocation());

        if (args[1].equalsIgnoreCase("create")) {
            create(player, region, args);

        } else if (args[1].equalsIgnoreCase("delete")) {
            delete(player, region);

        } else if (args[1].equalsIgnoreCase("addChunk") && region == null) {
            addChunk(player, args);

        } else if (args[1].equalsIgnoreCase("removeChunk") && region == null) {
            removeChunk(player, region);

        } else if (args[1].equalsIgnoreCase("rename")) {
            region.setName(args[2]);

        } else if (args[1].equalsIgnoreCase("setType")) {
            if (EnumUtil.isValidEnum(RegionType.class, args[2].toUpperCase())) {
                region.setType(RegionType.valueOf(args[2].toUpperCase()));
            }

        } else if (args[1].equalsIgnoreCase("setLevel")) {
            region.setLevel(NumberUtil.parseInt(args[2], 1));

        } else if (args[1].equalsIgnoreCase("addCore") || args[1].equalsIgnoreCase("addClaim") || args[1].equalsIgnoreCase("removeCore") || args[1].equalsIgnoreCase("removeClaim")) {
            handleFaction(player, region, factions.getByName(args[2]), args);
        }
    }

    private void create(Player player, Region region, String[] args) {
        if (region == null) {
            board.getRegions().add(new Region(args[2], player.getLocation().getChunk()));
            ParsingUtil.sendMessage(player, FMessage.CMD_REGIONS_CREATE.getMessage(), args[2]);
        } else {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_REGION.getMessage());
        }
    }

    private void delete(Player player, Region region) {
        if (region != null) {
            board.getRegions().remove(region);
            ParsingUtil.sendMessage(player, FMessage.CMD_REGIONS_DELETE.getMessage(ParsingUtil.getRegionName(player, region)));
        } else {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_REGION.getMessage());
        }
    }

    private void addChunk(Player player, String[] args) {
        Region regionAdd = board.getById(NumberUtil.parseInt(args[2]));
        if (regionAdd == null) {
            regionAdd = board.getByName(args[2]);
        }
        if (regionAdd == null) {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_REGION.getMessage());
            return;
        }
        regionAdd.getChunks().add(player.getLocation().getChunk());
        ParsingUtil.sendMessage(player, FMessage.CMD_REGIONS_CHUNK_ADDED.getMessage(), regionAdd);
    }

    private void removeChunk(Player player, Region region) {
        if (region != null) {
            board.getRegions().remove(region);
            ParsingUtil.sendMessage(player, FMessage.CMD_REGIONS_CHUNK_REMOVED.getMessage(), region);
        } else {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_REGION.getMessage());
        }
    }

    private void handleFaction(Player player, Region region, Faction faction, String[] args) {
        if (faction == null || region == null) {
            return;
        }
        if (args[1].equalsIgnoreCase("addCore")) {
            region.getCoreFactions().put(faction, Calendar.getInstance().getTime());
            ParsingUtil.sendMessage(player, FMessage.FACTION_NEW_CLAIM.getMessage(), faction, region);
        } else if (args[1].equalsIgnoreCase("addClaim")) {
            region.getClaimFactions().put(faction, Calendar.getInstance().getTime());
            ParsingUtil.sendMessage(player, FMessage.FACTION_NEW_CORE.getMessage(), faction, region);
        } else if (args[1].equalsIgnoreCase("removeCore")) {
            region.getClaimFactions().remove(faction);
            ParsingUtil.sendMessage(player, FMessage.FACTION_LOST_CORE.getMessage(), faction, region);
        } else if (args[1].equalsIgnoreCase("removeClaim")) {
            region.getClaimFactions().remove(faction);
            ParsingUtil.sendMessage(player, FMessage.FACTION_LOST_CLAIM.getMessage(), faction, region);
        }
    }

}
