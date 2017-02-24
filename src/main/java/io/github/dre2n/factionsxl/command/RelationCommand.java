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
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.Relation;
import io.github.dre2n.factionsxl.faction.Relation.Request;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class RelationCommand extends BRCommand {

    FactionsXL plugin = FactionsXL.getInstance();

    private Set<Request> requests = new HashSet<>();

    public RelationCommand() {
        setCommand("relation");
        setMinArgs(3);
        setMaxArgs(4);
        setHelp(FMessage.HELP_CMD_RELATION.getMessage());
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
        if (!subjectFaction.isPrivileged(sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        Faction objectFaction = plugin.getFactionCache().getByName(args[2]);
        if (objectFaction == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[2]);
            return;
        }

        if (args.length == 5 && args[4].equals("-deny")) {
            HashSet<Request> toRemove = new HashSet<>();
            for (Request request : requests) {
                if (request.getSubject().equals(subjectFaction) && request.getObject().equals(objectFaction)) {
                    toRemove.add(request);
                    objectFaction.sendMessage(FMessage.RELATION_DENIED.getMessage(), objectFaction, request.getRelation().getName());
                }
            }
            toRemove.removeAll(toRemove);
            return;
        }

        Relation relation = Relation.fromString(args[3]);
        if (subjectFaction.getRelation(objectFaction) == Relation.PERSONAL_UNION && relation != Relation.REAL_UNION) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_PERSONAL_UNION_WITH_FACTION.getMessage(), subjectFaction, objectFaction);
            return;
        }
        Request matching = null;
        for (Request request : requests) {
            if (request.getSubject().equals(objectFaction) && request.getObject().equals(subjectFaction) && request.getRelation() == relation) {
                matching = request;
                break;
            }
        }
        if (matching != null) {
            matching.confirm();
            requests.remove(matching);
            return;
        }
        if (relation != null && relation != Relation.OWN && relation != Relation.PERSONAL_UNION) {
            Request request = new Request(subjectFaction, objectFaction, relation);
            if (!requestExists(subjectFaction, objectFaction)) {
                requests.add(request);
            }
            subjectFaction.sendMessage(FMessage.RELATION_WISH_OWN.getMessage(), sender, objectFaction, relation.getName());
            request.send();

        } else {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_RELATION.getMessage(), args[3]);
        }
    }

    private boolean requestExists(Faction subjectFaction, Faction objectFaction) {
        for (Request check : requests) {
            if (check.getSubject().equals(objectFaction) && check.getObject().equals(subjectFaction)) {
                return true;
            }
        }
        return false;
    }

}
