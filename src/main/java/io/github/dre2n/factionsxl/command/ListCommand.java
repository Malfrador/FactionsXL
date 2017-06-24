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

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.GovernmentType;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.PageGUI;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.Arrays;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class ListCommand extends FCommand implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();

    public ListCommand() {
        setCommand("list");
        setAliases("l");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(FMessage.HELP_LIST.getMessage());
        setPermission(FPermission.LIST.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Set<Faction> factions = plugin.getFactionCache().getActive();

        int size = (int) (9 * Math.ceil(((double) factions.size() / 9)));
        Inventory gui = Bukkit.createInventory(null, size, FMessage.CMD_LIST_TITLE.getMessage());
        for (Faction faction : plugin.getFactionCache().getActive()) {
            int members = faction.getMembers().contains(faction.getAdmin()) ? faction.getMembers().size() : faction.getMembers().size() + 1;
            ItemStack banner = new ItemStack(Material.BANNER, members, faction.getBannerColor());
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
            gui.addItem(banner);
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(FMessage.CMD_LIST_TITLE.getMessage())) {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
    }

}
