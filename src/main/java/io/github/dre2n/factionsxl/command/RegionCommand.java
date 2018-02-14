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

import de.erethon.commons.chat.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.economy.Resource;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class RegionCommand extends FCommand {

    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();
    Board board = plugin.getBoard();

    public RegionCommand() {
        setCommand("region");
        setAliases("province");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_REGION.getMessage());
        setPermission(FPermission.REGION.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        if (!(sender instanceof Player) && args.length == 1) {
            displayHelp(sender);
            return;
        }

        Region region = null;
        if (args.length >= 2) {
            region = board.getByName(args[1]);
            if (region == null) {
                int id = 0;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException exception) {
                    ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_REGION.getMessage(), args[1]);
                    return;
                }
                region = board.getById(id);
            }
        } else {
            region = board.getByLocation(((Player) sender).getLocation());
        }
        if (region == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_LAND_WILDERNESS.getMessage());
            return;
        }

        Faction faction = region.getOwner();
        ChatColor c = faction != null && sender instanceof Player ? faction.getRelation(factions.getByMember((Player) sender)).getColor() : Relation.PEACE.getColor();

        MessageUtil.sendCenteredMessage(sender, c + "&l=== " + region.getName() + " ===");
        MessageUtil.sendCenteredMessage(sender, "&6____________________________________________________");
        MessageUtil.sendMessage(sender, FMessage.CMD_REGION_OWNER.getMessage() + c + (faction != null ? faction.getLongName() : "None"));
        if (region.isNeutral()) {
            Faction senderFaction = sender instanceof Player ? factions.getByMember((Player) sender) : null;
            MessageUtil.sendMessage(sender, FMessage.CMD_REGION_PRICE.getMessage() + c + region.getClaimPrice(senderFaction));
        }

        BaseComponent[] income1 = TextComponent.fromLegacyText(FMessage.CMD_REGION_TYPE.getMessage());
        BaseComponent[] income2 = TextComponent.fromLegacyText(c + region.getType().getName() + " (" + region.getLevel() + ")");
        BaseComponent[] incomeHover = new BaseComponent[]{};
        boolean first = true;
        for (Entry<Resource, Integer> entry : region.getResources().entrySet()) {
            String legacy = c + "+" + entry.getValue() + " " + entry.getKey().getName();
            incomeHover = (BaseComponent[]) ArrayUtils.addAll(incomeHover, TextComponent.fromLegacyText((first ? new String() : "\n") + legacy));
            first = false;
        }
        HoverEvent incomeHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, incomeHover);
        for (BaseComponent comp : income2) {
            comp.setHoverEvent(incomeHoverEvent);
        }
        BaseComponent[] income = (BaseComponent[]) ArrayUtils.addAll(income1, income2);
        MessageUtil.sendMessage(sender, income);

        MessageUtil.sendMessage(sender, FMessage.CMD_REGION_POPULATION.getMessage() + c + region.getPopulation() + "/" + region.getType().getMaxPopulation(region.getLevel()));

        if (sender instanceof Player) {
            ArrayList<BaseComponent> cores = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(FMessage.CMD_REGION_CORES.getMessage())));
            boolean first1 = true;
            for (Map.Entry<Faction, Date> core : region.getCoreFactions().entrySet()) {
                Faction coreFaction = core.getKey();
                Date date = core.getValue();
                Relation relation = faction != null ? faction.getRelation(coreFaction) : Relation.PEACE;
                if (!first1) {
                    cores.addAll(Arrays.asList(TextComponent.fromLegacyText(ChatColor.GOLD + ", ")));
                }
                first1 = false;
                BaseComponent[] relComps = TextComponent.fromLegacyText(relation.getColor() + coreFaction.getName());
                HoverEvent onHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + date.toString()));
                for (BaseComponent relComp : relComps) {
                    relComp.setHoverEvent(onHover);
                }
                cores.addAll(Arrays.asList(relComps));
            }
            ((Player) sender).spigot().sendMessage(cores.toArray(new BaseComponent[]{}));

            ArrayList<BaseComponent> claims = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(FMessage.CMD_REGION_CLAIMS.getMessage())));
            boolean first2 = true;
            for (Map.Entry<Faction, Date> core : region.getClaimFactions().entrySet()) {
                Faction claimFaction = core.getKey();
                Date date = core.getValue();
                Relation relation = faction != null ? faction.getRelation(claimFaction) : Relation.PEACE;
                if (!first2) {
                    claims.addAll(Arrays.asList(TextComponent.fromLegacyText(ChatColor.GOLD + ", ")));
                }
                first2 = false;
                BaseComponent[] relComps = TextComponent.fromLegacyText(relation.getColor() + claimFaction.getName());
                HoverEvent onHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + date.toString()));
                for (BaseComponent relComp : relComps) {
                    relComp.setHoverEvent(onHover);
                }
                claims.addAll(Arrays.asList(relComps));
            }
            ((Player) sender).spigot().sendMessage(claims.toArray(new BaseComponent[]{}));
        }
    }

}
