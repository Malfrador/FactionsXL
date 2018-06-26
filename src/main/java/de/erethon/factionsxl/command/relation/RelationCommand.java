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
package de.erethon.factionsxl.command.relation;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.command.FCommand;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.entity.Relation;
import de.erethon.factionsxl.entity.RelationRequest;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.util.ParsingUtil;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class RelationCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    public RelationCommand() {
        setCommand("relation");
        setMinArgs(3);
        setMaxArgs(4);
        setHelp(FMessage.HELP_RELATION.getMessage());
        setPermission(FPermission.RELATION.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Faction subjectFaction = plugin.getFactionCache().getByName(args[1]);
        if (subjectFaction == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[1]);
            return;
        }
        if (!subjectFaction.isAdmin(sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        if (subjectFaction.isVassal()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_VASSAL.getMessage(), subjectFaction);
            return;
        }

        Faction objectFaction = plugin.getFactionCache().getByName(args[2]);
        if (objectFaction == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[2]);
            return;
        }

        if (subjectFaction == objectFaction) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_OWN_FACTION.getMessage());
            return;
        }
        Collection<RelationRequest> requests = subjectFaction.getRequests(RelationRequest.class);

        if (args.length == 5 && args[4].equals("-deny")) {
            HashSet<RelationRequest> toRemove = new HashSet<>();
            for (RelationRequest request : requests) {
                if (request.getSubject().equals(objectFaction) && request.getObject().equals(subjectFaction)) {
                    toRemove.add(request);
                    objectFaction.sendMessage(FMessage.RELATION_DENIED.getMessage(), subjectFaction, objectFaction, request.getRelation().getName());
                    subjectFaction.sendMessage(FMessage.RELATION_DENIED.getMessage(), subjectFaction, objectFaction, request.getRelation().getName());
                }
            }
            subjectFaction.getRequests().removeAll(toRemove);
            return;
        }

        Relation relation = Relation.fromString(args[3]);
        if (subjectFaction.getRelation(objectFaction) == Relation.PERSONAL_UNION && relation != Relation.REAL_UNION) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_PERSONAL_UNION_WITH_FACTION.getMessage(), subjectFaction, objectFaction);
            return;
        }
        if (subjectFaction.getRelation(objectFaction) != Relation.PERSONAL_UNION && relation == Relation.REAL_UNION) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_PERSONAL_UNION_WITH_FACTION_REQUIRED.getMessage(), subjectFaction, objectFaction);
            return;
        }
        RelationRequest matching = null;
        for (RelationRequest request : requests) {
            if (request.getSubject().equals(objectFaction) && request.getObject().equals(subjectFaction) && request.getRelation() == relation) {
                matching = request;
                break;
            }
        }
        if (matching != null) {
            matching.confirm();
            return;
        }

        if (relation != null && relation != Relation.OWN && relation != Relation.PERSONAL_UNION) {
            new RelationRequest(sender, subjectFaction, objectFaction, relation).send();
        } else {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_RELATION.getMessage(), args[3]);
        }
    }

}