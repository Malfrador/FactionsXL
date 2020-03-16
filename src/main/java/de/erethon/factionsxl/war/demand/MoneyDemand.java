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
package de.erethon.factionsxl.war.demand;

import de.erethon.commons.gui.GUIButton;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.gui.AmountSelectionGUI;
import de.erethon.factionsxl.gui.AmountSelectionGUI.Max;
import de.erethon.factionsxl.gui.AmountSelectionGUI.Min;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class MoneyDemand implements WarDemand {

    private BigDecimal amount;

    public MoneyDemand(BigDecimal amount) {
        this.amount = amount;
    }

    public MoneyDemand(Map<String, Object> args) {
        amount = new BigDecimal((Double) args.get("amount"));
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public static ItemStack getGUIButton() {
        return GUIButton.setDisplay(new ItemStack(Material.GOLD_INGOT), FMessage.WAR_DEMAND_CREATION_MENU_MONEY.getMessage());
    }

    public static void openSetupGUI(Player player) {
        new AmountSelectionGUI<BigDecimal>(Min.MIN_0_01, Max.MAX_1000000, new BigDecimal(0)) {
            @Override
            public void onClickBack(Player player) {
                FactionsXL.getInstance().getWarCache().getWarDemandCreationMenu().open(player);
            }

            @Override
            public void onClickContinue(Player player, BigDecimal amount) {
                FactionsXL.getInstance().getFPlayerCache().getByPlayer(player).getPeaceOffer().getDemands().add(new MoneyDemand(amount));
                FactionsXL.getInstance().getWarCache().getWarDemandCreationMenu().open(player);
            }
        }.open(player);
    }

    @Override
    public void demand() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean pay() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canPay() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> args = new HashMap<>();
        args.put("amount", amount);
        return args;
    }

}
