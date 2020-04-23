package de.erethon.factionsxl.war;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.gui.PageGUI;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPlayer;
import de.erethon.factionsxl.util.ParsingUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

/**
 * @author Malfrador
 */

public class CasusBelliMenu implements Listener, InventoryHolder {
    FactionsXL plugin = FactionsXL.getInstance();
    Inventory gui;
    Faction object;

    public CasusBelliMenu() {
        gui = Bukkit.createInventory(this, 27, ChatColor.GOLD + "Casus Belli");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player, Faction f) {
        FPlayer fplayer = plugin.getFPlayerCache().getByPlayer(player);
        Faction faction = fplayer.getFaction();
        object = f;
        // Raid is always available
        ItemStack raidItem = new ItemStack(Material.STONE_SWORD);
        ItemMeta raidMeta = raidItem.getItemMeta();
        raidMeta.setDisplayName(FMessage.WAR_CB_RAID.getMessage());
        raidItem.setItemMeta(raidMeta);
        gui.addItem(raidItem);
        // Other CBs
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
                case BORDER_FRICTION:
                    guiMeta.setDisplayName(FMessage.WAR_CB_BORDER.getMessage());
                    guiItem.setType(Material.STICKY_PISTON);
                    guiItem.setItemMeta(guiMeta);
                    gui.addItem(guiItem);
                case RESTORATION_OF_UNION:
                    break;
                case RECONQUEST:
                    guiMeta.setDisplayName(FMessage.WAR_CB_RECONQUEST.getMessage());
                    guiItem.setType(Material.GOLDEN_SWORD);
                    guiItem.setItemMeta(guiMeta);
                    gui.addItem(guiItem);
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
        Player player = (Player) event.getWhoClicked();
        Faction faction = plugin.getFPlayerCache().getByPlayer(player).getFaction();
        event.setCancelled(true);
        PageGUI.playSound(event);
        ItemStack item = event.getCurrentItem();
        if (event.getCurrentItem() == null) {
            return;
        }
        String itemName = item.getItemMeta().getDisplayName();

        // Object
        if (object == null) {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), object);
            return;
        }
        if (object.isInWar(faction)) {
            MessageUtil.sendMessage(player,"&cDu bist bereits in diesem Krieg.");
            return;
        }

        // Subject:
        WarParty subject = null;
        Set<Faction> factions = plugin.getFactionCache().getByLeader(player);
        if (factions.isEmpty()) {
            ParsingUtil.sendMessage(player, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        for (Faction f : factions) {
            if (f.getMembers().contains(player)) {
                subject = new WarParty(f, WarPartyRole.ATTACKER);
                break;
            }
        }
        if (subject == null) {
            ParsingUtil.sendMessage(player, FMessage.ERROR_JOIN_FACTION.getMessage());
            return;
        }
        for (Faction f : factions) {
            subject.addParticipant(f);
        }

        // Type:
        CasusBelli casus = null;
        if (itemName.equals(FMessage.WAR_CB_RAID.getMessage())) {
            casus = new CasusBelli(CasusBelli.Type.RAID, object, null);
        }
        if (itemName.equals(FMessage.WAR_CB_CONQUEST.getMessage())) {
            for (CasusBelli cb : faction.getCasusBelli()) {
                if (cb.getType() == CasusBelli.Type.CONQUEST && cb.getTarget() == object) {
                    casus = cb;
                    break;
                }
            }
        }
        if (itemName.equals(FMessage.WAR_CB_LIBERATION.getMessage())) {
            for (CasusBelli cb : faction.getCasusBelli()) {
                if (cb.getType() == CasusBelli.Type.LIBERATION && cb.getTarget() == object) {
                    casus = cb;
                    break;
                }
            }
        }
        if (itemName.equals(FMessage.WAR_CB_RESUBJAGATION.getMessage())) {
            for (CasusBelli cb : faction.getCasusBelli()) {
                if (cb.getType() == CasusBelli.Type.RESUBJAGATION && cb.getTarget() == object) {
                    casus = cb;
                    break;
                }
            }
        }
        if (itemName.equals(FMessage.WAR_CB_RECONQUEST.getMessage())) {
            for (CasusBelli cb : faction.getCasusBelli()) {
                if (cb.getType() == CasusBelli.Type.RECONQUEST && cb.getTarget() == object) {
                    casus = cb;
                    break;
                }
            }
        }
        player.closeInventory();
        if (casus == null) {
            MessageUtil.sendMessage(player, FMessage.WAR_CB_INVALID.getMessage());
            return;
        }
        new CallToArmsMenu(subject, object, casus).open(player);
        HandlerList.unregisterAll(this);
    }

    @Override
    public Inventory getInventory() {
        return gui;
    }
}
