package de.erethon.factionsxl.war.demand;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.player.FPlayer;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.CasusBelli;
import de.erethon.factionsxl.war.WarParty;
import net.milkbowl.vault.chat.Chat;
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

import java.math.BigDecimal;
import java.util.*;

public class RegionDemand implements WarDemand, Listener, InventoryHolder {
    Inventory gui;
    FactionsXL plugin = FactionsXL.getInstance();
    Faction e;
    Player player;
    FPlayer fplayer;
    private List<Region> demandRegions = new ArrayList<>();


    public RegionDemand(List<Region> regions) {
        this.demandRegions = regions;
    }

    public RegionDemand() { }

    public RegionDemand(Map<String, Object> args) {
        demandRegions = (List<Region>) args.get("regions");
    }

    public List<Region> getDemandRegions() {
        return demandRegions;
    }

<<<<<<< HEAD
    public double getWarscoreCost() {
        return cost;
    }

    public double getRegionWarscore(Region r, Faction f) {
        double c = 20;
        if (r.getOccupant() == f) {
            c = c - 6;                        // Besetzt: 12 WP
        }
        if (r.getClaimFactions().containsKey(f)) {  // Claim: 10 WP - Claim & Besetzt: 6 WP
            c = c / 2;
        }
        if (r.getCoreFactions().containsKey(f)) {   // Core: 10 WP - Core & Claim: 5 - Core & Besetzt: 6 - Core, Claim & Besetzt: 3 WP
            c = c / 2;
        }
        return c;
    }

    public double getDemandCost(Faction f) {
        double cost = 20;
        for (Region r : demandRegions) {
            if (r.getOccupant() == f) {
                cost = cost - 6;                        // Besetzt: 12 WP
            }
            if (r.getClaimFactions().containsKey(f)) {  // Claim: 10 WP - Claim & Besetzt: 6 WP
                cost = cost / 2;
            }
            if (r.getCoreFactions().containsKey(f)) {   // Core: 10 WP - Core & Claim: 5 - Core & Besetzt: 6 - Core, Claim & Besetzt: 3 WP
                cost = cost / 2;
            }
        }
        return cost;
    }

=======
>>>>>>> parent of 2f40fe8... Updated copyright notice 2018 -> 2020
    @Override
    public void demand() {

    }

<<<<<<< HEAD
    @Override
    public boolean canAffordWP(WarParty wp) {
        return wp.getPoints() >= getWarscoreCost();
    }

    @Override
    public boolean canAffordWP(Faction f) {
        return true;
    }

=======
>>>>>>> parent of 2f40fe8... Updated copyright notice 2018 -> 2020
    public void openSetupGUI(Player p, Faction enemy) {
        gui = Bukkit.createInventory(this, 54, FMessage.WAR_DEMAND_REGION_TITLE.getMessage());
        Bukkit.getPluginManager().registerEvents(this, plugin);
        FPlayer fp =  plugin.getFPlayerCache().getByPlayer(p);
        Faction faction = fp.getFaction();
        player = p;
        if (fp.getPeaceOffer().isOffer()) {
            enemy = faction;
        }
        e = enemy;
        ItemStack doneItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta doneMeta = doneItem.getItemMeta();
        doneMeta.setDisplayName(FMessage.WAR_DEMAND_REGION_DONE.getMessage());
        doneItem.setItemMeta(doneMeta);
        int i = 0;
        while (i <= 7) {
            gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            i++;
        }
        gui.addItem(doneItem);
        for (Region r : enemy.getRegions()) {
            ItemStack guiItem = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta guiMeta = guiItem.getItemMeta();
            List<String> lore = new ArrayList<>();
            guiMeta.setDisplayName(String.valueOf(r.getId()));

            lore.add(r.getName());
<<<<<<< HEAD
            if (!fp.getPeaceOffer().isOffer()) {
                lore.add(FMessage.WAR_DEMAND_REGION_WARSCORE.getMessage(String.valueOf(getRegionWarscore(r, faction))));
=======
            if (r.getOccupant() == faction) {
                lore.add("§c§oRegion occupied by your faction!");
                lore.add("§7Warscore§8: &5" + 6);
            }
            else {
                lore.add("§7Warscore§8: &5" + 10);
>>>>>>> parent of 2f40fe8... Updated copyright notice 2018 -> 2020
            }

            guiMeta.setLore(lore);
            guiItem.setItemMeta(guiMeta);
            gui.addItem(guiItem);
            p.openInventory(gui);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Faction faction = plugin.getFPlayerCache().getByPlayer(player).getFaction();
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
<<<<<<< HEAD
        if (item.getItemMeta().getDisplayName().contains(FMessage.WAR_DEMAND_REGION_DONE.getMessage())) {
            double cost = getDemandCost(faction);
            WarDemand war = (WarDemand) new RegionDemand(demandRegions, cost);
            FactionsXL.getInstance().getFPlayerCache().getByPlayer(player).getPeaceOffer().getDemands().add(war);
            FactionsXL.getInstance().getWarCache().getWarDemandCreationMenu().open(player);
            MessageUtil.sendMessage(player, FMessage.WAR_DEMAND_REGION_SUCCESS.getMessage());
            FactionsXL.getInstance().getFPlayerCache().getByPlayer(player).listWarDemands();
=======
        if (item.getItemMeta().getDisplayName().contains("Done.")) {
            WarDemand war = (WarDemand) new RegionDemand(demandRegions);
            FactionsXL.getInstance().getFPlayerCache().getByPlayer(player).getPeaceOffer().getDemands().add(war);
            FactionsXL.getInstance().getWarCache().getWarDemandCreationMenu().open(player);
            MessageUtil.sendMessage(player, "&aDemand added");
>>>>>>> parent of 2f40fe8... Updated copyright notice 2018 -> 2020
            return;
        }
        int ID = Integer.parseInt(item.getItemMeta().getDisplayName());
        for (Region r : e.getRegions()) {
            if (r.getId() == ID) {
                if (demandRegions.contains(r)) {
                    demandRegions.remove(r);
                    MessageUtil.sendMessage(player, FMessage.WAR_DEMAND_REGION_REMOVED.getMessage(r.getName()));
                }
                else {
                    demandRegions.add(r);
                    MessageUtil.sendMessage(player, FMessage.WAR_DEMAND_REGION_ADDED.getMessage(r.getName()));
                }
            }
        }
        for (Region r : demandRegions) {
            MessageUtil.sendMessage(player, "&8 - &7" + r.getName());
        }

    }


    @Override
    public void pay(WarParty f, WarParty f2) {
        for (Region r : demandRegions) {
            if (r.getCoreFactions().containsKey(r.getOwner())) {
                r.getOwner().getCasusBelli().add(new CasusBelli(CasusBelli.Type.RECONQUEST, f.getLeader(), new Date(System.currentTimeMillis() + (FConfig.MONTH * 3) )));
                r.getOwner().sendMessage(FMessage.WAR_DEMAND_REGION_CB_ADDED.getMessage(), f.getLeader());
            }
            ParsingUtil.broadcastMessage(FMessage.CMD_GIVE_REGION_SUCCESS.getMessage(), r.getOwner(), r.getName(), f.getLeader());
            r.setOwner((Faction) f.getLeader());
        }
    }

    @Override
    public void pay(Faction f, Faction f2) {
        for (Region r : demandRegions) {
            if (r.getCoreFactions().containsKey(r.getOwner())) {
                r.getOwner().getCasusBelli().add(new CasusBelli(CasusBelli.Type.RECONQUEST, f, new Date(System.currentTimeMillis() + (FConfig.MONTH * 3) )));
                r.getOwner().sendMessage(FMessage.WAR_DEMAND_REGION_CB_ADDED.getMessage(), f);
            }
            ParsingUtil.broadcastMessage(FMessage.CMD_GIVE_REGION_SUCCESS.getMessage(), r.getOwner(), r.getName(), f);
            r.setOwner(f);
        }
    }

    @Override
    public boolean canPay(WarParty wp) {
        return true;

    }

    @Override
    public boolean canPay(Faction f) {
        return true;

    }

    @Override
    public String toString() {
        StringBuilder list = new StringBuilder(FMessage.WAR_DEMAND_REGION_REGIONS_CHAT.getMessage());
        for (Region r : getDemandRegions()) {
            list.append(ChatColor.YELLOW).append(r.getName()).append(ChatColor.DARK_GRAY).append(", ");

        }
<<<<<<< HEAD
        list.append(FMessage.WAR_DEMAND_REGION_TOTAL_WARSCORE_CHAT.getMessage(String.valueOf(getWarscoreCost())));
=======
>>>>>>> parent of 2f40fe8... Updated copyright notice 2018 -> 2020
        return list.toString();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> args = new HashMap<>();
        args.put("regions", demandRegions);
        return args;
    }
    @Override
    public Inventory getInventory() {
        return gui;
    }
}
