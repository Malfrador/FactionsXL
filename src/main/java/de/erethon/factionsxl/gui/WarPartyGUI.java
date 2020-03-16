/*
 * Copyright (C) 2017-2018 Daniel Saukel
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
package de.erethon.factionsxl.gui;

import de.erethon.commons.gui.GUIButton;
import de.erethon.commons.gui.PageGUI;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.war.WarParty;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public abstract class WarPartyGUI implements Listener, StandardizedGUI {

    protected FactionsXL plugin;

    private String title;
    private List<WarParty> parties;

    public WarPartyGUI(FactionsXL plugin, WarParty... parties) {
        this(plugin, ChatColor.GOLD + parties[0].getName(), parties);
    }

    public WarPartyGUI(FactionsXL plugin, String title, WarParty... parties) {
        this.plugin = plugin;
        this.title = title;
        this.parties = Arrays.asList(parties);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void open(Player player) {
        PageGUI gui = new PageGUI(title);
        Set<Faction> factions = new HashSet<>();
        parties.forEach(p -> factions.addAll(p.getFactions()));
        factions.forEach(f -> gui.addButton(GUIButton.setDisplay(f.getBannerStack(), ChatColor.DARK_RED + f.getName())));
        gui.open(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }
        ItemStack button = event.getCurrentItem();
        if (button == null) {
            return;
        }
        Faction faction = plugin.getFactionCache().getByBanner(button);
        if (faction != null) {
            onButtonClick((Player) event.getWhoClicked(), faction);
        }
    }

    public abstract void onButtonClick(Player whoClicked, Faction buttonFaction);

    public void delete() {
        HandlerList.unregisterAll(this);
    }

}