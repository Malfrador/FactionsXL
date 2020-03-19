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
package de.erethon.factionsxl.command;
import de.erethon.vignette.api.*;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.faction.GovernmentType;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.util.ParsingUtil;
import java.util.Arrays;
import java.util.Set;

import de.erethon.vignette.api.component.InventoryButton;
import de.erethon.vignette.api.component.InventoryButtonBuilder;
import de.erethon.vignette.api.layout.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static de.erethon.vignette.api.layout.PaginatedInventoryLayout.PaginationButtonPosition.BOTTOM;

/**
 * @author Daniel Saukel
 */
public class ListCommand extends FCommand implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();

    public ListCommand() {
        setCommand("list");
        setAliases("l");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(FMessage.HELP_LIST.getMessage());
        setPermission(FPermission.LIST.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Set<Faction> factions = null;
        if (args.length == 2 && args[1].equalsIgnoreCase("disbanded")) {
            factions = plugin.getFactionCache().getInactive();
        } else {
            factions = plugin.getFactionCache().getActive();
        }
        if (factions.isEmpty()) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_FACTIONS.getMessage());
            return;
        }
        for (GUI g : List) {
            g.register();
            g.clear();
        }
        GUI list = List[0];

        for (Faction faction : factions) {
            int members = faction.getMembers().contains(faction.getAdmin()) ? faction.getMembers().size() : faction.getMembers().size() + 1;
            ItemStack banner = new ItemStack(faction.getBannerType(), members);
            ItemMeta meta = faction.getBanner() != null ? faction.getBanner().clone() : banner.getItemMeta();
            meta.setDisplayName(ParsingUtil.getFactionName(player, faction));

            ChatColor c = faction.getRelation(plugin.getFactionCache().getByMember(player)).getColor();
            String leader = faction.getAdmin() != null ? faction.getAdmin().getName() : ChatColor.ITALIC + "Interregnum";
            String govType = faction.getGovernmentType().getName();
            if (faction.getGovernmentType() == GovernmentType.MONARCHY) {
                govType += " (" + (faction.getDynasty() != null ? faction.getDynasty().getName() : ChatColor.ITALIC + "Interregnum") + c + ")";
            }
            String power = String.valueOf(faction.getPower());
            String provinces = String.valueOf(faction.getRegions().size());
            meta.setLore(Arrays.asList(FMessage.CMD_SHOW_GOVERNMENT_TYPE.getMessage() + c + govType,
                    FMessage.CMD_SHOW_LEADER.getMessage() + c + leader,
                    FMessage.CMD_SHOW_INFO.getMessage(c.toString(), power, provinces)
            ));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            banner.setItemMeta(meta);
            list.add(new InventoryButtonBuilder()
                    .icon(faction.getBannerType())
                    .number(members)
                    .lines(FMessage.CMD_SHOW_GOVERNMENT_TYPE.getMessage() + c + govType,
                            FMessage.CMD_SHOW_LEADER.getMessage() + c + leader,
                            FMessage.CMD_SHOW_INFO.getMessage(c.toString(), power, provinces))
                    .title(ParsingUtil.getFactionName(player, faction))
                    .onInteract(e -> showFaction(faction, player) )
                    .build()
                    );
        }
        list.open((Player) sender);
    }

    public static final InventoryGUI[] List = new InventoryGUI[] {
            new PaginatedInventoryGUI(FMessage.CMD_LIST_TITLE.getMessage()),

    };
    static {
        List[0].setLayout(new PaginatedFlowInventoryLayout((PaginatedInventoryGUI) List[0], 54, BOTTOM));
    }

    public void showFaction(Faction f, Player p) {
        if (f == null) {
            String fName = f.getName();
            f = plugin.getFactionCache().getInactiveByName(fName);
        }
        p.closeInventory();
        plugin.getCommandCache().show.showFaction(p, f);
        }
    }


