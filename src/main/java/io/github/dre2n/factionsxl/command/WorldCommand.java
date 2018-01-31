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

import io.github.dre2n.commons.misc.EnumUtil;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.board.RegionType;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.util.LazyChunk;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.Calendar;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class WorldCommand extends FCommand {

    private static final BlockFace[] AXIS = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public WorldCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("world");
        setAliases("w");
        setMinArgs(0);
        setMaxArgs(5);
        setHelp(FMessage.HELP_WORLD.getMessage());
        setPermission(FPermission.WORLD.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        String sub = new String();
        if (args.length != 1) {
            sub = args[1];
        }

        Player player = (Player) sender;
        Region region = board.getByLocation(player.getLocation());
        int i = 2;
        if (args.length >= 3 && board.getByName(args[2]) != null) {
            region = board.getByName(args[2]);
            i++;
        } else if (args.length >= 3 && board.getById(NumberUtil.parseInt(args[2], -1)) != null) {
            region = board.getById(NumberUtil.parseInt(args[2], -1));
            i++;
        }

        if (sub.equalsIgnoreCase("createProvince") || sub.equalsIgnoreCase("create") || sub.equalsIgnoreCase("c")) {
            create(player, region, args, i);

        } else if (sub.equalsIgnoreCase("deleteProvince") || sub.equalsIgnoreCase("delete") || sub.equalsIgnoreCase("d")) {
            delete(player, region);

        } else if (sub.equalsIgnoreCase("addChunk") || sub.equalsIgnoreCase("add") || sub.equalsIgnoreCase("a")) {
            addChunk(player, region, args, i);

        } else if (sub.equalsIgnoreCase("removeChunk") || sub.equalsIgnoreCase("r")) {
            removeChunk(player, region);

        } else if (sub.equalsIgnoreCase("renameProvince") || sub.equalsIgnoreCase("rename") || sub.equalsIgnoreCase("n")) {
            ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_RENAMED.getMessage(), region.getName(), args[i]);
            region.setName(args[i]);

        } else if (sub.equalsIgnoreCase("setType") || sub.equalsIgnoreCase("t")) {
            setType(player, region, args, i);

        } else if (sub.equalsIgnoreCase("setLevel") || sub.equalsIgnoreCase("l")) {
            region.setLevel(NumberUtil.parseInt(args[i], 1));
            ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_LEVEL.getMessage(), region, args[i]);

        } else if (sub.equalsIgnoreCase("addCore") || sub.equalsIgnoreCase("addClaim") || sub.equalsIgnoreCase("removeCore") || sub.equalsIgnoreCase("removeClaim")) {
            handleFaction(player, region, factions.getByName(args[i]), args, i);

        } else {
            sender.sendMessage("TODO: /f world help");
        }
    }

    private void create(Player player, Region region, String[] args, int i) {
        if (region == null && args.length >= i + 1) {
            board.create(args[i], player.getLocation().getChunk());
            ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_CREATE.getMessage(), args[2]);
        } else if (region != null) {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NOT_WILDERNESS.getMessage());
        } else if (args.length < i + 1) {
            ParsingUtil.sendMessage(player, ChatColor.RED + "/f world createProvince [name]");
        }
    }

    private void delete(Player player, Region region) {
        if (region != null) {
            board.getRegions().remove(region);
            ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_DELETE.getMessage(ParsingUtil.getRegionName(player, region)));
        } else {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_REGION.getMessage());
        }
    }

    private void addChunk(Player player, Region region, String[] args, int i) {
        World world = player.getWorld();
        Chunk chunk = player.getLocation().getChunk();
        int x = chunk.getX();
        int z = chunk.getZ();
        boolean override = args.length > i + 2 ? args[i + 2].equalsIgnoreCase("-override") : false;

        if (region == null) {
            ParsingUtil.sendMessage(player, ChatColor.RED + "/f world addChunk [region name] [radius|line|auto] [value] ([-override])");
            return;
        }
        if (args.length >= i + 1) {
            if (args[i].equalsIgnoreCase("radius") || args[i].equalsIgnoreCase("r")) {
                int r = args.length > i + 1 ? NumberUtil.parseInt(args[i + 1]) : 1;
                x += -1 * r;
                z += -1 * r;
                while (z <= chunk.getZ() + r) {
                    if (board.isWilderness(world.getChunkAt(x, z)) || override) {
                        addChunk(region, world.getChunkAt(x, z), override);
                    }
                    x++;
                    if (x > chunk.getX() + r) {
                        x = chunk.getX() + -1 * r;
                        z++;
                    }
                }

            } else if (args[i].equalsIgnoreCase("line") || args[i].equalsIgnoreCase("l")) {
                int l = args.length > i + 1 ? NumberUtil.parseInt(args[i + 1]) : 1;
                BlockFace face = AXIS[Math.round(player.getLocation().getYaw() / 90F) & 0x3];
                if (face == BlockFace.NORTH) {
                    while (l >= 0) {
                        if (board.isWilderness(world.getChunkAt(x, z + l)) || override) {
                            addChunk(region, world.getChunkAt(x, z + l), override);
                        }
                        l--;
                    }
                } else if (face == BlockFace.EAST) {
                    while (l >= 0) {
                        if (board.isWilderness(world.getChunkAt(x - l, z)) || override) {
                            addChunk(region, world.getChunkAt(x - l, z), override);
                        }
                        l--;
                    }
                } else if (face == BlockFace.WEST) {
                    while (l >= 0) {
                        if (board.isWilderness(world.getChunkAt(x + l, z)) || override) {
                            addChunk(region, world.getChunkAt(x + l, z), override);
                        }
                        l--;
                    }
                } else if (face == BlockFace.SOUTH) {
                    while (l >= 0) {
                        if (board.isWilderness(world.getChunkAt(x, z - l)) || override) {
                            addChunk(region, world.getChunkAt(x, z - l), override);
                        }
                        l--;
                    }
                }

            } else if (args[i].equalsIgnoreCase("auto") || args[i].equalsIgnoreCase("a")) {
                FPlayer fPlayer = fPlayers.getByPlayer(player);
                if (fPlayer.isAutoclaiming()) {
                    fPlayer.setAutoclaiming(null);
                    ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_AUTOCLAIM_END.getMessage());
                    return;
                } else {
                    fPlayer.setAutoclaiming(region);
                    ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_AUTOCLAIM_START.getMessage(), region);
                }
            }

        } else {
            addChunk(region, chunk, override);
        }
        ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_CHUNK_ADDED.getMessage(), region);
    }

    private void removeChunk(Player player, Region region) {
        if (region != null) {
            removeChunk(region, player.getLocation().getChunk());
            ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_CHUNK_REMOVED.getMessage(), region);
        } else {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_REGION.getMessage());
        }
    }

    private void addChunk(Region region, Chunk chunk, boolean override) {
        if (override) {
            Region old = board.getByChunk(chunk);
            if (old != null) {
                removeChunk(old, chunk);
            }
        }
        region.getChunks().add(new LazyChunk(chunk));
    }

    private void removeChunk(Region region, Chunk chunk) {
        LazyChunk remove = null;
        for (LazyChunk rChunk : region.getChunks()) {
            if (chunk.getX() == rChunk.getX() && chunk.getZ() == rChunk.getZ()) {
                remove = rChunk;
            }
        }
        region.getChunks().remove(remove);
    }

    private void setType(Player player, Region region, String[] args, int i) {
        if (args.length < i + 1) {
            ParsingUtil.sendMessage(player, ChatColor.RED + "/f world setType [region name] [type]");
        }
        if (region == null) {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_REGION.getMessage());
            return;
        }
        if (EnumUtil.isValidEnum(RegionType.class, args[i].toUpperCase())) {
            RegionType type = RegionType.valueOf(args[i].toUpperCase());
            region.setType(type);
            ParsingUtil.sendMessage(player, FMessage.CMD_WORLD_TYPE.getMessage(), region, type.getName());
        } else {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_REGION_TYPE.getMessage());
        }
    }

    private void handleFaction(Player player, Region region, Faction faction, String[] args, int i) {
        if (faction == null || region == null) {
            return;
        }
        if (args[i - 1].equalsIgnoreCase("addCore")) {
            region.getCoreFactions().put(faction, Calendar.getInstance().getTime());
            ParsingUtil.sendMessage(player, FMessage.FACTION_NEW_CLAIM.getMessage(), faction, region);
        } else if (args[i - 1].equalsIgnoreCase("addClaim")) {
            region.getClaimFactions().put(faction, Calendar.getInstance().getTime());
            ParsingUtil.sendMessage(player, FMessage.FACTION_NEW_CORE.getMessage(), faction, region);
        } else if (args[i - 1].equalsIgnoreCase("removeCore")) {
            region.getClaimFactions().remove(faction);
            ParsingUtil.sendMessage(player, FMessage.FACTION_LOST_CORE.getMessage(), faction, region);
        } else if (args[i - 1].equalsIgnoreCase("removeClaim")) {
            region.getClaimFactions().remove(faction);
            ParsingUtil.sendMessage(player, FMessage.FACTION_LOST_CLAIM.getMessage(), faction, region);
        }
    }

}
