/*
 * Copyright (c) 2017-2019 Daniel Saukel
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
package de.erethon.factionsxl.war.demand;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.gui.GUIButton;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.LegalEntity;
import de.erethon.factionsxl.gui.AddItemsGUI;
import java.util.HashMap;
import java.util.Map;

import de.erethon.factionsxl.war.WarParty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class ItemDemand implements WarDemand {

    private ItemStack[] items;

    public ItemDemand(ItemStack[] items) {
        this.items = items;
    }

    public ItemDemand(Map<String, Object> args) {
        items = (ItemStack[]) args.get("items");
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setItems(ItemStack[] items) {
        this.items = items;
    }

    public static ItemStack getGUIButton() {
        return GUIButton.setDisplay(new ItemStack(Material.APPLE), FMessage.WAR_DEMAND_CREATION_MENU_ITEM.getMessage());
    }

    public static void openSetupGUI(Player player) {
<<<<<<< HEAD
        MessageUtil.sendMessage(player, "&cThis demand type is currently disabled.");
        /*new AddItemsGUI("Add Items", 54, true) {

=======
        new AddItemsGUI("Add Items", 54, true) {
>>>>>>> parent of 2f40fe8... Updated copyright notice 2018 -> 2020
            @Override
            public void onClose() {
                FactionsXL.getInstance().getFPlayerCache().getByPlayer(player).getPeaceOffer().getDemands().add(new ItemDemand(getStorageContents()));
                FactionsXL.getInstance().getWarCache().getWarDemandCreationMenu().open(player);
            }
        }.open(player);*/
    }

    @Override
    public void demand() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
<<<<<<< HEAD
    public boolean canAffordWP(WarParty wp) {
        return false;
    }

    @Override
    public boolean canAffordWP(Faction f) {
        return true;
    }

    @Override
=======
>>>>>>> parent of 2f40fe8... Updated copyright notice 2018 -> 2020
    public void pay(WarParty wp, WarParty wp2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
<<<<<<< HEAD
    public void pay(Faction f, Faction f2) {

    }

    @Override
    public double getWarscoreCost() {
        return 0;
    }

    @Override
=======
>>>>>>> parent of 2f40fe8... Updated copyright notice 2018 -> 2020
    public boolean canPay(WarParty f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canPay(Faction f) {
        return false;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> args = new HashMap<>();
        args.put("items", items);
        return args;
    }

}
