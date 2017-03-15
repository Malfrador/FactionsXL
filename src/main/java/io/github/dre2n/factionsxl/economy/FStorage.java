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
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.util.PageGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class FStorage {

    private Faction faction;
    private Map<Resource, Integer> goods = new HashMap<>();
    private PageGUI gui;

    public FStorage(Faction faction) {
        this.faction = faction;
        for (Resource resource : Resource.values()) {
            if (!goods.containsKey(resource) && resource.isPhysical()) {
                goods.put(resource, 0);
            }
        }
        update();
    }

    public FStorage(Faction faction, Map<String, Object> storageMap) {
        this.faction = faction;
        for (Entry<String, Object> entry : storageMap.entrySet()) {
            goods.put(Resource.valueOf(entry.getKey()), (int) entry.getValue());
        }
        for (Resource resource : Resource.values()) {
            if (!goods.containsKey(resource) && resource.isPhysical()) {
                goods.put(resource, 0);
            }
        }
        update();
    }

    /* Getters and setters */
    /**
     * @return
     * the goods in this storage
     */
    public Map<Resource, Integer> getGoods() {
        return goods;
    }

    /* Actions */
    public void payday() {
        for (Region region : faction.getRegions()) {
            for (Entry<Resource, Integer> entry : region.getResources().entrySet()) {
                if (entry.getKey() == Resource.TAXES) {
                    faction.getAccount().deposit(entry.getValue());
                } else if (entry.getKey() == Resource.MANPOWER) {
                    // TODO
                } else {
                    goods.put(entry.getKey(), goods.get(entry.getKey()) + entry.getValue());
                }
            }
        }

        double importModifier = FactionsXL.getInstance().getFConfig().getImportModifier();
        double exportModifier = FactionsXL.getInstance().getFConfig().getExportModifier();
        HashMap<Resource, Integer> importActions = new HashMap<>();
        for (Entry<Resource, Integer> entry : faction.getGroceryList().entrySet()) {
            Resource resource = entry.getKey();
            int amount = entry.getValue();
            if (amount > 0) {
                importActions.put(resource, amount);
            } else if (amount < 0) {
                faction.chargeResource(resource, -1 * amount);
                faction.getAccount().deposit(-1 * amount * resource.getValue() * exportModifier);
            }
        }
        // Perform import actions later sothat they don't fail if the money generated from exports makes them possible
        for (Entry<Resource, Integer> entry : importActions.entrySet()) {
            faction.chargeMoneyForResource(entry.getKey(), entry.getValue(), importModifier);
        }

        for (Player player : faction.getOnlineMembers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        faction.sendMessage(FMessage.STORAGE_PAYDAY.getMessage());
    }

    public Map<String, Integer> serialize() {
        Map<String, Integer> serialized = new HashMap<>();
        for (Entry<Resource, Integer> entry : goods.entrySet()) {
            serialized.put(entry.getKey().toString(), entry.getValue());
        }
        return serialized;
    }

    public void update() {
        gui = new PageGUI(FMessage.STORAGE_TITLE.getMessage(faction.getName()));
        for (Resource resource : Resource.values()) {
            ItemStack icon = resource.getIcon();
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + resource.getName());
            if (resource.isPhysical()) {
                meta.setLore(Arrays.asList(FMessage.STORAGE_STOCK.getMessage(String.valueOf(goods.get(resource)))));
            } else {
                List<String> lore = new ArrayList<>(Arrays.asList(FMessage.STORAGE_NON_PHYSICAL.getMessage(resource.getName())));
                if (resource == Resource.MANPOWER) {
                    lore.add(FMessage.STORAGE_NON_PHYSICAL_MANPOWER.getMessage());
                } else if (resource == Resource.TAXES) {
                    lore.add(FMessage.STORAGE_NON_PHYSICAL_TAXES.getMessage());
                }
                meta.setLore(lore);
            }
            icon.setItemMeta(meta);
            gui.addButton(icon);
        }
    }

    public void open(HumanEntity player) {
        update();
        gui.open(player);
    }

}
