/*
 *
 *  * Copyright (C) 2017-2020 Daniel Saukel, Malfrador
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
    double cost;
    private List<Region> demandRegions = new ArrayList<>();


    public RegionDemand(List<Region> regions, double warscore) {
        this.demandRegions = regions;
        this.cost = warscore;
    }

    public RegionDemand() { }

    public RegionDemand(Map<String, Object> args) {
        demandRegions = (List<Region>) args.get("regions");
    }

    public List<Region> getDemandRegions() {
        return demandRegions;
    }

    public double getWarscoreCost() {
        return cost;
    }

    public double getDemandCost(Faction f) {
        double cost = 0;
        for (Region r : demandRegions) {
            if (r.getOccupant() == f) {
                cost = cost + 6;
            }
            else {
                cost = cost + 10;
            }
        }
        return cost;
    }

    @Override
    public void demand() {

    }

    @Override
    public boolean canAffordWP(WarParty wp) {
        return wp.getPoints() >= getWarscoreCost();
    }

    public void openSetupGUI(Player p, Faction enemy) {
        gui = Bukkit.createInventory(this, 54, "§5Select Enemy Regions...");
        Bukkit.getPluginManager().registerEvents(this, plugin);
        FPlayer fp =  plugin.getFPlayerCache().getByPlayer(p);
        Faction faction = fp.getFaction();
        player = p;
        e = enemy;
        ItemStack doneItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta doneMeta = doneItem.getItemMeta();
        doneMeta.setDisplayName("§aDone.");
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
            if (r.getOccupant() == faction) {
                lore.add("§c§oRegion occupied by your faction!");
                lore.add("§7Warscore§8: §5-" + 6);
            }
            else {
                lore.add("§7Warscore§8: §5-" + 10);
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
        if (item.getItemMeta().getDisplayName().contains("Done.")) {
            double cost = getDemandCost(faction);
            WarDemand war = (WarDemand) new RegionDemand(demandRegions, cost);
            FactionsXL.getInstance().getFPlayerCache().getByPlayer(player).getPeaceOffer().getDemands().add(war);
            FactionsXL.getInstance().getWarCache().getWarDemandCreationMenu().open(player);
            MessageUtil.sendMessage(player, "&aDemand added");
            FactionsXL.getInstance().getFPlayerCache().getByPlayer(player).listWarDemands();
            return;
        }
        int ID = Integer.parseInt(item.getItemMeta().getDisplayName());
        for (Region r : e.getRegions()) {
            if (r.getId() == ID) {
                if (demandRegions.contains(r)) {
                    demandRegions.remove(r);
                    MessageUtil.sendMessage(player, "&aRegion &e" + r.getName() + " &aremoved.");
                }
                else {
                    demandRegions.add(r);
                    MessageUtil.sendMessage(player, "&aRegion &e" + r.getName() + " &aadded.");
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
                r.getOwner().sendMessage("&aYou now have a &6RECONQUEST &aCasus Belli against " + f.getLeader() + "&a!");
            }
            ParsingUtil.broadcastMessage(FMessage.CMD_GIVE_REGION_SUCCESS.getMessage(), r.getOwner(), r.getName(), f.getLeader());
            r.setOwner((Faction) f.getLeader());
        }
    }

    @Override
    public boolean canPay(WarParty wp) {
        return true;

    }

    @Override
    public String toString() {
        StringBuilder list = new StringBuilder("&aRegions: ");
        for (Region r : getDemandRegions()) {
            list.append(ChatColor.YELLOW).append(r.getName()).append(ChatColor.DARK_GRAY).append(", ");

        }
        list.append("&8(&7Total&8: &5" + getWarscoreCost() + "&8)");
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
