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
package io.github.dre2n.factionsxl.command.war;

import io.github.dre2n.commons.misc.SimpleDateUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.command.FCommand;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import io.github.dre2n.factionsxl.war.War;
import io.github.dre2n.factionsxl.war.WarCache;
import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class WarStatusCommand extends FCommand {

    WarCache wars = FactionsXL.getInstance().getWarCache();

    private String PLACEHOLDER = ChatColor.GOLD + " | ";

    public WarStatusCommand() {
        setCommand("warStatus");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_WAR_STATUS.getMessage());
        setPermission(FPermission.WAR_STATUS.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        War war = wars.getByDate(Long.parseLong(args[1]));
        if (war != null) {
            showWarInfo(sender, war);
            return;
        }
        Faction faction = getSenderFactionOrFromArg(sender, args, 1, true);
        if (faction == null) {
            return;
        }
        Set<War> wars = this.wars.getByFaction(faction);
        if (wars.isEmpty()) {
            ParsingUtil.sendMessage(sender, FMessage.CMD_WAR_STATUS_NO_WARS.getMessage(), faction);
            return;
        }
        ParsingUtil.sendMessage(sender, FMessage.CMD_WAR_STATUS_HEADLINE.getMessage(), faction);
        for (War fWar : wars) {
            String line = ChatColor.DARK_BLUE + "> &v1" + PLACEHOLDER + "&v2" + PLACEHOLDER + ChatColor.DARK_BLUE + fWar.getCasusBelli() + PLACEHOLDER + SimpleDateUtil.ddMMyyyyhhmm(war.getStartDate());
            BaseComponent[] comps = TextComponent.fromLegacyText(line);
            ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factionsxl warStatus " + fWar.getStartDate().getTime());
            for (BaseComponent comp : comps) {
                comp.setClickEvent(onClick);
            }
        }
    }

    public void showWarInfo(CommandSender sender, War war) {
    }

}
