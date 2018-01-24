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

import io.github.dre2n.factionsxl.chat.ChatChannel;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.player.FPermission;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class ChatCommand extends FCommand {

    public ChatCommand() {
        setCommand("chat");
        setAliases("c");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(FMessage.HELP_CHAT.getMessage());
        setPermission(FPermission.CHAT.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        getFSender(sender).setChatChannel(ChatChannel.fromString(args[1]));
    }

}
