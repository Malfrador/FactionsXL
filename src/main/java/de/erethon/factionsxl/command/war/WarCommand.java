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
package de.erethon.factionsxl.command.war;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.command.FCommand;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.entity.Relation;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.CallToArmsMenu;
import de.erethon.factionsxl.war.CasusBelli;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.WarPartyRole;
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
        setMaxArgs(2);
        setHelp(FMessage.HELP_WAR.getMessage());
        setPermission(FPermission.WAR.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction object = plugin.getFactionCache().getByName(args[1]);
        if (object == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            return;
        }
        // TO DO!
        if (object.isInWar() && !(args.length == 3 && args[2].equalsIgnoreCase("-unsafe"))) {
            sender.sendMessage("Kriege gegen Fraktionen, die schon im Krieg sind, sind bis auf Weiteres deaktiviert!");
            return;
        }
        // TO DO!
        WarParty subject = null;
        Set<Faction> factions = plugin.getFactionCache().getByLeader(player);
        if (factions.isEmpty()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        for (Faction faction : factions) {
            Relation relation = object.getRelation(faction);
            switch (relation) {
                case ALLIANCE:
                case COALITION:
                    ParsingUtil.sendMessage(sender, FMessage.ERROR_CANNOT_ATTACK_ALLIED_FACTION.getMessage());
                    return;
                case PERSONAL_UNION:
                    ParsingUtil.sendMessage(sender, FMessage.ERROR_PERSONAL_UNION_WITH_FACTION.getMessage(), faction, object);
                    return;
                case OWN:
                    ParsingUtil.sendMessage(sender, FMessage.ERROR_OWN_FACTION.getMessage());
                    return;
                case ENEMY:
                    ParsingUtil.sendMessage(sender, FMessage.ERROR_IN_WAR.getMessage(), object);
            }
            if (faction.getMembers().contains(player)) {
                subject = new WarParty(faction, WarPartyRole.ATTACKER);
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
        new CallToArmsMenu(subject, object, new CasusBelli(CasusBelli.Type.RAID, object, null)).open(player);
    }

}
