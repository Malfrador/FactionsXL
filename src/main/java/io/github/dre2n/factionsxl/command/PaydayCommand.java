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
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class PaydayCommand extends BRCommand {

    public PaydayCommand() {
        setCommand("payday");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_CMD_PAYDAY.getMessage());
        setPermission(FPermission.PAYDAY.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        int i = args.length > 1 ? NumberUtil.parseInt(args[1], 1) : 1;
        do {
            for (Faction faction : FactionsXL.getInstance().getFactionCache().getActive()) {
                faction.getStorage().payday();
            }
            i--;
        } while (i > 0);
    }

}
