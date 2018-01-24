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
package io.github.dre2n.factionsxl.population;

import io.github.dre2n.commons.misc.ProgressBar;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.economy.Resource;
import io.github.dre2n.factionsxl.economy.ResourceSubcategory;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.util.GUIButton;
import io.github.dre2n.factionsxl.util.PageGUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
public class PopulationMenu implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();

    public static final ItemStack DEMANDS = GUIButton.setDisplayName(new ItemStack(Material.BREAD), FMessage.POPULATION_DEMANDS_BUTTON.getMessage());
    public static final ItemStack MILITARY = GUIButton.setDisplayName(GUIButton.GUI_SWORD, FMessage.POPULATION_MILITARY_BUTTON.getMessage());

    private Faction faction;
    private Inventory main;
    private Inventory demands;
    private DemandMenu demandResources;
    private MilitaryMenu military;

    public PopulationMenu(Faction faction) {
        this.faction = faction;
        demandResources = new DemandMenu(faction);
        military = new MilitaryMenu(faction);
        setupGUI();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void setupGUI() {
        main = Bukkit.createInventory(null, 9, FMessage.POPULATION_TITLE.getMessage(faction.getName()));
        main.setItem(3, DEMANDS);
        main.setItem(5, MILITARY);
        demands = Bukkit.createInventory(null, 27, FMessage.POPULATION_DEMANDS_TITLE.getMessage(faction.getName()));
        PageGUI.addHeader(demands);
        update();
    }

    public void openMain(HumanEntity player) {
        player.openInventory(main);
    }

    public void openDemands(HumanEntity player) {
        update();
        player.openInventory(demands);
    }

    public void update() {
        PageGUI.clearHeaderGUI(demands);
        for (ResourceSubcategory subcategory : ResourceSubcategory.values()) {
            HashMap<SaturationLevel, Integer> saturation = new HashMap<>();
            int percentage = 0;
            for (Resource resource : subcategory.getResources()) {
                SaturationLevel level = faction.isResourceSaturated(resource, subcategory.isBasic());
                saturation.put(level, (saturation.get(level) != null ? saturation.get(level) : 0) + 1);
                percentage += faction.getSaturatedResources().get(resource);
            }
            percentage = percentage / subcategory.getResources().length;
            ItemStack icon = subcategory.getIcon();
            ItemMeta meta = icon.getItemMeta();
            int max = subcategory.getResources().length;
            List<String> lore = new ArrayList<>();
            lore.add(ProgressBar.getBar((double) percentage));
            SaturationLevel level = SaturationLevel.getByPercentage(percentage, subcategory.isBasic());
            lore.add(level.getColor().toString() + percentage + "%");
            for (SaturationLevel sLevel : SaturationLevel.values()) {
                if (!(subcategory.isBasic() & sLevel == SaturationLevel.NOT_SATURATED || !subcategory.isBasic() & sLevel == SaturationLevel.NOT_SATURATED_BASIC)) {
                    lore.add(sLevel.getColor() + sLevel.getName() + ": " + (saturation.get(sLevel) != null ? saturation.get(sLevel) : 0) + "/" + max);
                }
            }
            meta.setLore(lore);
            if (level == SaturationLevel.SURPLUS) {
                meta.addEnchant(Enchantment.LUCK, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.setDisplayName(level.getColor() + subcategory.getName());
            icon.setItemMeta(meta);
            demands.addItem(icon);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        HumanEntity player = event.getWhoClicked();
        if (inventory == null || !main.getTitle().equals(inventory.getTitle()) & !demands.getTitle().equals(inventory.getTitle())) {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
        ItemStack button = event.getCurrentItem();
        if (GUIButton.BACK.equals(button)) {
            openMain(player);
        } else if (DEMANDS.equals(button)) {
            openDemands(player);
        } else if (MILITARY.equals(button)) {
            military.open(player);
        } else {
            ResourceSubcategory category = ResourceSubcategory.getByIcon(button);
            if (category != null) {
                demandResources.open(player, category);
            }
        }
    }

}
