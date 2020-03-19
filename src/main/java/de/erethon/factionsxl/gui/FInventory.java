package de.erethon.factionsxl.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

abstract class FInventory implements InventoryHolder {
    private Inventory inv;

    FInventory(Inventory inv) {
        this.inv = inv;
    }
    Inventory getInv() {
        return inv;
    }
}
