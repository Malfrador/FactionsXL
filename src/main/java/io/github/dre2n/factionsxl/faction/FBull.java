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
package io.github.dre2n.factionsxl.faction;

import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.util.FireworkUtil;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

/**
 * @author Daniel Saukel
 */
public class FBull implements Listener {

    FactionsXL plugin;

    public FBull(FactionsXL plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        ItemStack item = event.getItem();
        if (isBull(item) && event.getAction() == Action.RIGHT_CLICK_AIR | event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!plugin.getBoard().isAnnexable(location)) {
                ParsingUtil.sendMessage(player, FMessage.ERROR_LAND_NOT_FOR_SALE.getMessage());
                return;
            }
            FactionCache factions = plugin.getFactionCache();
            BookMeta meta = ((BookMeta) item.getItemMeta());
            String title = meta.getTitle().replace(" ", "-");
            if (factions.getByName(title) != null) {
                title += NumberUtil.generateRandomInt(0, 100);
            }
            FireworkUtil.spawnRandom(location);
            plugin.getFactionCache().create(player, title);
            player.getInventory().remove(item);
        }
    }

    private static boolean isValid(ItemStack item, int lines) {
        return item != null && item.hasItemMeta() && item.getItemMeta() instanceof BookMeta && item.getItemMeta().hasLore() && item.getItemMeta().getLore().size() > lines;
    }

    public static boolean isBull(ItemStack item) {
        return isValid(item, 0) && item.getItemMeta().getLore().get(0).equals(FMessage.BULL_ID.getMessage()) && ((BookMeta) item.getItemMeta()).getGeneration() == Generation.ORIGINAL;
    }

    public static ItemStack create(ItemStack item, String name) {
        if (!(item.getItemMeta() instanceof BookMeta)) {
            return null;
        }
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setAuthor(name);
        meta.setTitle(meta.getTitle());
        meta.setLore(Arrays.asList(FMessage.BULL_ID.getMessage(), FMessage.BULL_RIGHT_KLICK.getMessage()));
        meta.setGeneration(Generation.ORIGINAL);
        ItemStack bull = new ItemStack(Material.WRITTEN_BOOK);
        bull.setItemMeta(meta);
        return bull;
    }

}
