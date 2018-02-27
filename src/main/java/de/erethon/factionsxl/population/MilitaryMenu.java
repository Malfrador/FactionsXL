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
package de.erethon.factionsxl.population;

import de.erethon.commons.gui.PageGUI;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.util.GUIButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class MilitaryMenu implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();

    public static final ItemStack SOLDIERS = GUIButton.setDisplay(GUIButton.SOLDIER, FMessage.POPULATION_MILITARY_SOLDIERS.getMessage());

    private Faction faction;
    private Inventory gui;

    public MilitaryMenu(Faction faction) {
        this.faction = faction;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        setupGUI();
    }

    private void setupGUI() {
        gui = Bukkit.createInventory(null, 27, FMessage.POPULATION_MILITARY_TITLE.getMessage(faction.getName()));
        GUIButton.addHeader(gui);
        gui.setItem(9, SOLDIERS);
        update();
    }

    public void update() {
    }

    public void open(HumanEntity player) {
        update();
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || gui == null || !gui.getTitle().equals(inventory.getTitle())) {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
        ItemStack button = event.getCurrentItem();
        if (GUIButton.BACK.equals(button)) {
            faction.getPopulationMenu().openMain(player);
            return;
        }
    }

}
