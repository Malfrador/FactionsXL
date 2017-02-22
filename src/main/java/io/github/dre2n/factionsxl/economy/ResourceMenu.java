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
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.util.ItemUtil;
import io.github.dre2n.factionsxl.util.PageGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
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
public class ResourceMenu implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();

    public static final ItemStack EXPORT_UP;
    public static final ItemStack EXPORT_DOWN;
    public static final ItemStack IMPORT_UP;
    public static final ItemStack IMPORT_DOWN;

    static {
        EXPORT_UP = ItemUtil.UP.clone();
        ItemMeta exUpMeta = EXPORT_UP.getItemMeta();
        exUpMeta.setDisplayName(FMessage.TRADE_EXPORT_UP.getMessage());
        EXPORT_UP.setItemMeta(exUpMeta);

        EXPORT_DOWN = ItemUtil.DOWN.clone();
        ItemMeta exDownMeta = EXPORT_DOWN.getItemMeta();
        exDownMeta.setDisplayName(FMessage.TRADE_EXPORT_DOWN.getMessage());
        EXPORT_DOWN.setItemMeta(exDownMeta);

        IMPORT_UP = ItemUtil.UP.clone();
        ItemMeta imUpMeta = IMPORT_UP.getItemMeta();
        imUpMeta.setDisplayName(FMessage.TRADE_IMPORT_UP.getMessage());
        IMPORT_UP.setItemMeta(imUpMeta);

        IMPORT_DOWN = ItemUtil.DOWN.clone();
        ItemMeta imDownMeta = IMPORT_DOWN.getItemMeta();
        imDownMeta.setDisplayName(FMessage.TRADE_IMPORT_DOWN.getMessage());
        IMPORT_DOWN.setItemMeta(imDownMeta);
    }

    private Faction faction;
    private Resource resource;
    private int income;
    private int consume;
    private Inventory gui;

    public ResourceMenu(Faction faction, Resource resource, int income, int consume) {
        this.faction = faction;
        this.resource = resource;
        this.income = income;
        this.consume = consume;
        setupGUI();
        update();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void setupGUI() {
        gui = Bukkit.createInventory(null, 27, FMessage.TRADE_RESOURCE_TITLE.getMessage(resource.getName(), faction.getName()));
        ItemStack banner = faction.getBannerStack();
        ItemMeta meta = banner.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + faction.getName());
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        banner.setItemMeta(meta);
        gui.setItem(0, ItemUtil.GUI_BACK);
        gui.setItem(1, banner);
        gui.setItem(2, banner);
        gui.setItem(3, banner);
        gui.setItem(5, banner);
        gui.setItem(6, banner);
        gui.setItem(7, banner);
        gui.setItem(8, banner);
        gui.setItem(11, IMPORT_UP);
        gui.setItem(20, IMPORT_DOWN);
        gui.setItem(15, EXPORT_UP);
        gui.setItem(24, EXPORT_DOWN);
    }

    public int getIncome() {
        return income;
    }

    public int getConsume() {
        return consume;
    }

    public int getProfit() {
        return income - consume;
    }

    public Inventory getGUI() {
        return gui;
    }

    public void update() {
        gui.setItem(4, TradeMenu.formButton(faction.getRegions(), resource));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !gui.getTitle().equals(inventory.getTitle())) {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
        ItemStack button = inventory.getItem(event.getSlot());
        if (button == null) {
            return;
        }
        if (button.equals(ItemUtil.GUI_BACK)) {
            faction.getTradeMenu().open(player);
        }
    }

}
