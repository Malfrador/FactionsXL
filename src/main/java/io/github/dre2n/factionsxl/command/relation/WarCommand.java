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
package io.github.dre2n.factionsxl.command.relation;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.command.FCommand;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import io.github.dre2n.factionsxl.war.CallToArmsMenu;
import io.github.dre2n.factionsxl.war.WarParty;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class WarCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public WarCommand() {
        setCommand("war");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(FMessage.HELP_WAR.getMessage());
        setPermission(FPermission.WAR.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        WarParty subject = null;
        Set<Faction> factions = plugin.getFactionCache().getByLeader(player);
        if (factions.isEmpty()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        for (Faction faction : factions) {
            if (faction.getMembers().contains(player)) {
                subject = new WarParty(faction);
                break;
            }
        }
        if (subject == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_JOIN_FACTION.getMessage());
            return;
        }
        for (Faction faction : factions) {
            subject.addParticipant(faction);
        }
        Faction object = plugin.getFactionCache().getByName(args[1]);
        if (object == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            return;
        }
        new CallToArmsMenu(subject, object).open(player);
    }

}
