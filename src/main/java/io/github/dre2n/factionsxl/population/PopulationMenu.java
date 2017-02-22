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
package io.github.dre2n.factionsxl.population;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.util.ItemUtil;
import io.github.dre2n.factionsxl.util.PageGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class PopulationMenu implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();

    public static final ItemStack DEMANDS = ItemUtil.setDisplayName(new ItemStack(Material.BREAD), FMessage.POPULATION_DEMANDS.getMessage());
    public static final ItemStack MILITARY = ItemUtil.setDisplayName(ItemUtil.GUI_SWORD, FMessage.POPULATION_MILITARY.getMessage());

    private Faction faction;
    private Inventory gui;
    private DemandMenu demands;

    public PopulationMenu(Faction faction) {
        this.faction = faction;
        this.demands = new DemandMenu(faction);
        setupGUI();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setupGUI() {
        gui = Bukkit.createInventory(null, 9, FMessage.POPULATION_TITLE.getMessage(faction.getName()));
        gui.setItem(3, DEMANDS);
        gui.setItem(5, MILITARY);
    }

    public void open(HumanEntity player) {
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !gui.getTitle().equals(inventory.getTitle())) {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
        ItemStack button = event.getCurrentItem();
        if (DEMANDS.equals(button)) {
            demands.open(event.getWhoClicked());
        } else if (MILITARY.equals(button)) {
            // TODO: MILITARY STATUS
        }
    }

}
