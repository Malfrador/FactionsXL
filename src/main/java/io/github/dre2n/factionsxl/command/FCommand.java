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

import io.github.dre2n.commons.command.DRECommand;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public abstract class FCommand extends DRECommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public FPlayer getFSender(CommandSender sender) {
        return plugin.getFPlayerCache().getByName(sender.getName());
    }

    public FPlayer getFSenderOrFromArg(CommandSender sender, String[] args, int index) {
        FPlayer fSender = null;
        if (args.length > index) {
            fSender = plugin.getFPlayerCache().getByName(args[index]);
            if (fSender == null) {
                ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_PLAYER.getMessage(), args[index]);
            }
        } else if (sender instanceof Player) {
            fSender = plugin.getFPlayerCache().getByPlayer((Player) sender);
        } else {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_SPECIFY_PLAYER.getMessage());
        }
        return fSender;
    }

    public Faction getSenderFaction(CommandSender sender) {
        if (getFSender(sender) == null) {
            return null;
        }
        return plugin.getFactionCache().getByFPlayer(getFSender(sender));
    }

    public Faction getSenderFactionOrFromArg(CommandSender sender, String[] args, int index) {
        Faction faction = null;
        if (args.length > index) {
            faction = plugin.getFactionCache().getByName(args[index]);
            if (faction == null) {
                int id = NumberUtil.parseInt(args[index], -1);
                if (id != -1) {
                    faction = plugin.getFactionCache().getById(id);
                }
            }

        } else if (sender instanceof Player) {
            faction = getFSender(sender) != null ? getFSender(sender).getFaction() : null;
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
