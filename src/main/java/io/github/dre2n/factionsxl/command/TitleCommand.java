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
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TitleCommand extends FCommand {

    public TitleCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("title");
        setMinArgs(2);
        setMaxArgs(2);
        setHelp(FMessage.HELP_TITLE.getMessage());
        setPermission(FPermission.TITLE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        FPlayer fSender = getFSender(sender);
        if (sender instanceof Player && !fSender.isMod()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        FPlayer fPlayer = fPlayers.getByName(args[1]);
        if (fPlayer == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_PLAYER.getMessage(), args[1]);
            return;
        }
        Faction faction = fPlayer.getFaction();
        if (sender instanceof Player && faction != fSender.getFaction() && !faction.isAdmin(fSender.getUniqueId())) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_PLAYER_NOT_IN_FACTION.getMessage(), args[1], fSender.getFaction().getName());
            return;
        }
        String title = args[2].replace("&", new String());
        fPlayer.setTitle(title);
        fPlayer.getFaction().sendMessage(FMessage.CMD_TITLE_SUCCESS.getMessage(), sender, fPlayer, title);
    }

}
