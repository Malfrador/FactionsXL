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

import io.github.dre2n.commons.command.DRECommand;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.player.FPlayerCache;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import io.github.dre2n.factionsxl.war.WarCache;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public abstract class FCommand extends DRECommand {

    protected FactionsXL plugin;
    protected FactionCache factions;
    protected FPlayerCache fPlayers;
    protected Board board;
    protected FConfig config;
    protected Economy econ;
    protected WarCache wars;

    protected int i = 1;

    public FCommand(FactionsXL plugin) {
        this.plugin = plugin;
        factions = plugin.getFactionCache();
        fPlayers = fPlayers;
        board = plugin.getBoard();
        config = plugin.getFConfig();
        econ = plugin.getEconomyProvider();
        wars = plugin.getWarCache();
    }

    public FPlayer getFSender(CommandSender sender) {
        return fPlayers.getByName(sender.getName());
    }

    public FPlayer getFSenderOrFromArg(CommandSender sender, String[] args, int index) {
        FPlayer fSender = null;
        if (args.length > index) {
            fSender = fPlayers.getByName(args[index]);
            if (fSender == null) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_PLAYER.getMessage(), args[index]);
            }
        } else if (sender instanceof Player) {
            fSender = fPlayers.getByPlayer((Player) sender);
        } else {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_SPECIFY_PLAYER.getMessage());
        }
        return fSender;
    }

    public Faction getSenderFaction(CommandSender sender) {
        if (getFSender(sender) == null) {
            return null;
        }
        return factions.getByFPlayer(getFSender(sender));
    }

    public Faction getSenderFactionOrFromArg(CommandSender sender, String[] args, int index) {
        return getSenderFactionOrFromArg(sender, args, index, false);
    }

    public Faction getSenderFactionOrFromArg(CommandSender sender, String[] args, int index, boolean i) {
        Faction faction = null;
        if (args.length > index) {
            faction = factions.getByName(args[index]);
            if (faction == null) {
                int id = NumberUtil.parseInt(args[index], -1);
                if (id != -1) {
                    faction = factions.getById(id);
                }
            }
            if (faction != null && i) {
                this.i++;
            }

        }

        if (faction == null && sender instanceof Player) {
            faction = getSenderFaction(sender);
        }

        if (faction == null) {
            if (args.length > index) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[index]);
            } else {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_SPECIFY_FACTION.getMessage());
            }
        }
        return faction;
    }

}
