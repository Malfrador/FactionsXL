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
package de.erethon.factionsxl.war.demand;

import de.erethon.commons.gui.GUIButton;
import de.erethon.commons.gui.PageGUI;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.gui.StandardizedGUI;
import de.erethon.factionsxl.player.FPlayer;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.peaceoffer.PeaceOffer;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class WarDemandCreationGUI implements Listener, StandardizedGUI {

    private FactionsXL plugin;

    private Inventory gui = Bukkit.createInventory(null, 18, FMessage.WAR_DEMAND_CREATION_MENU_TITLE.getMessage());

    private ItemStack listDemands = GUIButton.setDisplay(new ItemStack(Material.BOOK), FMessage.WAR_DEMAND_CREATION_MENU_LIST.getMessage());
    private ItemStack send = GUIButton.setDisplay(StandardizedGUI.MAILBOX, FMessage.WAR_DEMAND_CREATION_MENU_SEND.getMessage());

    public WarDemandCreationGUI(FactionsXL plugin) {
        this.plugin = plugin;
        StandardizedGUI.addHeader(gui);
        gui.addItem(listDemands);
        PeaceOffer.getDemandTypes().forEach(t -> {
            try {
                gui.addItem((ItemStack) t.getDeclaredMethod("getGUIButton").invoke(null));
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
                FactionsXL.debug("[Warning] The demand type " + t.getName() + " lacks a GUI button.");
                gui.addItem(GUIButton.setDisplay(new ItemStack(Material.ANVIL), t.getSimpleName()));
            }
        });
        gui.addItem(send);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void open(Player player) {
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !gui.equals(inventory)) {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
        ItemStack button = inventory.getItem(event.getSlot());
        if (button == null) {
            return;
        }
        FPlayer player = plugin.getFPlayerCache().getByPlayer((Player) event.getWhoClicked());
        if (GUIButton.BACK.equals(button)) {
            Set<Faction> ownFactions = plugin.getFactionCache().getByLeader(player.getPlayer());
            Set<WarParty> parties = new HashSet<>();
            ownFactions.forEach(f -> f.getWarParties().forEach(p -> parties.add(p.getEnemy())));
            new WarDemandWarPartyGUI(plugin, ownFactions, parties.toArray(new WarParty[]{})).open(player.getPlayer());
        } else if (listDemands.equals(button)) {
            player.listWarDemands();
        } else if (send.equals(button)) {
            player.getPeaceOffer().send();
        } else {
            PeaceOffer.openSetupGUI(PeaceOffer.getDemandType(button), player.getPlayer());
        }
    }

}
