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

import static io.github.dre2n.commons.chat.FatLetter.*;
import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.player.FPermission;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class MainCommand extends FCommand {

    public MainCommand(FactionsXL plugin) {
        super(plugin);
        setCommand("main");
        setHelp(FMessage.HELP_MAIN.getMessage());
        setPermission(FPermission.MAIN.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        MessageUtil.sendCenteredMessage(sender, "&4" + F[0] + "&f" + X[0] + L[0]);
        MessageUtil.sendCenteredMessage(sender, "&4" + F[1] + "&f" + X[1] + L[1]);
        MessageUtil.sendCenteredMessage(sender, "&4" + F[2] + "&f" + X[2] + L[2]);
        MessageUtil.sendCenteredMessage(sender, "&4" + F[3] + "&f" + X[3] + L[3]);
        MessageUtil.sendCenteredMessage(sender, "&4" + F[4] + "&f" + X[4] + L[4]);
        MessageUtil.sendCenteredMessage(sender, "&b&l######## " + FMessage.CMD_MAIN_WELCOME.getMessage() + " &av" + plugin.getDescription().getVersion() + " &b&l########");
        MessageUtil.sendCenteredMessage(sender, FMessage.CMD_MAIN_HELP.getMessage());
        MessageUtil.sendCenteredMessage(sender, "&7\u00a92017-2018 Daniel Saukel; licensed under GPLv3.");
    }

}
