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
package io.github.dre2n.factionsxl.population;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.economy.Resource;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.util.PageGUI;
import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class DemandMenu implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();

    private Faction faction;
    private PageGUI gui;

    public DemandMenu(Faction faction) {
        this.faction = faction;
        gui = new PageGUI(FMessage.POPULATION_DEMANDS.getMessage());
        update();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void update() {
        gui.clear();
        for (Resource resource : Resource.values()) {
            ItemStack icon = new ItemStack(resource.getIcon());
            ItemMeta meta = icon.getItemMeta();
            int income = 0;
            ArrayList<String> lore = new ArrayList<>();
            for (Region region : faction.getRegions()) {
                Map<Resource, Integer> resources = region.getType().getResources(region.getLevel());
                if (resources.containsKey(resource)) {
                    int amount = resources.get(resource);
                    income += amount;
                    lore.add(ChatColor.GREEN + region.getName() + ": +" + amount);
                }
            }
            // TODO: IMPORT
            // TODO: EXPORT
            meta.setLore(lore);
            ChatColor color = ChatColor.YELLOW;
            if (income > 0) {
                color = ChatColor.GREEN;
            } else if (income < 0) {
                color = ChatColor.DARK_RED;
            }
            meta.setDisplayName(color + resource.getName());
            icon.setItemMeta(meta);
            gui.addButton(icon);
        }
    }

    public void open(HumanEntity player) {
        update();
        gui.open(player);
    }

}
