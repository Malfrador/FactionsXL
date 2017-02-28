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

import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.relation.Relation;

/**
 * @author Daniel Saukel
 */
public class RelationUniteCommand extends RelationAliasCommand {

    public RelationUniteCommand() {
        setCommand("unite");
        setHelp(FMessage.HELP_CMD_UNITE.getMessage());
    }

    @Override
    public Relation getRelation() {
        return Relation.REAL_UNION;
    }

}
