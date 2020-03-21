package de.erethon.factionsxl.war;

import de.erethon.commons.gui.PageGUI;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CasusBelliMenu implements Listener, InventoryHolder {
    FactionsXL plugin = FactionsXL.getInstance();
    Inventory gui;

    public CasusBelliMenu() {
        gui = Bukkit.createInventory(this, 27, ChatColor.GOLD + "Casus Belli");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        FPlayer fplayer = plugin.getFPlayerCache().getByPlayer(player);
        Faction faction = fplayer.getFaction();
        for (CasusBelli cb : faction.getCasusBelli()) {
            ItemStack guiItem = new ItemStack(Material.BEDROCK);
            ItemMeta guiMeta = guiItem.getItemMeta();
            // TODO: Descriptions for the CBs. Already in FMessage.
            switch (cb.getType()) {
                case CLAIM_ON_THRONE:
                    break;
                case CONQUEST:
                    guiMeta.setDisplayName(FMessage.WAR_CB_CONQUEST.getMessage());
                    guiItem.setType(Material.DIAMOND_SWORD);
                    guiItem.setItemMeta(guiMeta);
                    gui.addItem(guiItem);
                case IMPERIAL_BAN:
                    break;
                case INDEPENDENCE:
                    break;
                case LIBERATION:
                    guiMeta.setDisplayName(FMessage.WAR_CB_LIBERATION.getMessage());
                    guiItem.setType(Material.LEAD);
                    guiItem.setItemMeta(guiMeta);
                    gui.addItem(guiItem);
                case RAID:
                    guiMeta.setDisplayName(FMessage.WAR_CB_RAID.getMessage());
                    guiItem.setType(Material.GOLDEN_SWORD);
                    guiItem.setItemMeta(guiMeta);
                    gui.addItem(guiItem);
                case BORDER_FRICTION:
                    guiMeta.setDisplayName(FMessage.WAR_CB_BORDER.getMessage());
                    guiItem.setType(Material.STICKY_PISTON);
                    guiItem.setItemMeta(guiMeta);
                    gui.addItem(guiItem);
                case RESTORATION_OF_UNION:
                    break;
                case RECONQUEST:
                    break;
                case RESUBJAGATION:
                    break;
            }
        }
        player.openInventory(gui);
    }
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
    }

    @Override
    public Inventory getInventory() {
        return gui;
    }
}
