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
package io.github.dre2n.factionsxl.economy;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.util.ItemUtil;
import io.github.dre2n.factionsxl.util.PageGUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class TradeMenu implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();

    private Faction faction;
    private Set<Region> regions = new HashSet<>();
    private PageGUI gui;
    private Map<Resource, ResourceMenu> resourceMenus = new HashMap<>();

    public TradeMenu(Faction faction) {
        this.faction = faction;
        regions = faction.getRegions();
        update();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /* Getters and setters */
    public void update() {
        if (!plugin.getFConfig().isEconomyEnabled()) {
            gui = new PageGUI(FMessage.ERROR_ECON_DISABLED.getMessage());
            for (int i = 0; i <= 44; i++) {
                gui.getPages().get(0).setItem(i, ItemUtil.DISABLED);
            }
        } else {
            gui = new PageGUI(FMessage.TRADE_TITLE.getMessage(faction.getName()));
            for (Resource resource : Resource.values()) {
                gui.addButton(formButton(regions, resource));
                if (!resourceMenus.containsKey(resource)) {
                    resourceMenus.put(resource, new ResourceMenu(faction, resource, /*income*/ 1, 2/*consume*/));
                } else {
                    resourceMenus.get(resource).update();
                }
            }
        }
    }

    public static ItemStack formButton(Set<Region> regions, Resource resource) {
        ItemStack icon = new ItemStack(resource.getIcon());
        ItemMeta meta = icon.getItemMeta();
        int income = 0;
        ArrayList<String> lore = new ArrayList<>();
        for (Region region : regions) {
            Map<Resource, Integer> resources = region.getType().getResources(region.getLevel());
            if (resources.containsKey(resource)) {
                int amount = resources.get(resource);
                income += amount;
                lore.add(ChatColor.GREEN + region.getName() + ": +" + amount);
            }
        }
        // TODO: IMPORT
        // TODO: EXPORT
        meta.setLore(lore);
        ChatColor color = ChatColor.YELLOW;
        if (income > 0) {
            color = ChatColor.GREEN;
        } else if (income < 0) {
            color = ChatColor.DARK_RED;
        }
        meta.setDisplayName(color + resource.getName() + " (" + (income > 0 ? "+" : new String()) + income + ")");
        icon.setItemMeta(meta);
        return icon;
    }

    public void open(HumanEntity player) {
        update();
        gui.open(player);
    }

    public void openResourceMenu(HumanEntity player, Resource resource) {
        player.openInventory(resourceMenus.get(resource).getGUI());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !gui.getTitle().equals(inventory.getTitle()) || event.getSlot() >= 45) {
            return;
        }
        ItemStack button = inventory.getItem(event.getSlot());
        if (button == null || button.equals(ItemUtil.DISABLED)) {
            return;
        }
        if (!faction.isPrivileged(event.getWhoClicked())) {
            return;
        }
        Resource resource = Resource.getByIcon(button);
        openResourceMenu(player, resource);
    }

}
