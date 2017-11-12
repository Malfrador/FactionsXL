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
package io.github.dre2n.factionsxl.util;

import io.github.dre2n.factionsxl.FactionsXL;
import static io.github.dre2n.factionsxl.util.GUIButton.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class PageGUI {

    private String title;
    private Stack<Inventory> pages = new Stack<>();

    private int partitions;
    private ItemStack[] base;
    private Stack<List<ItemStack>> pages1;
    private Stack<List<ItemStack>> pages2;
    private Stack<List<ItemStack>> pages3;

    public PageGUI(String title) {
        this.title = title;
        newPage();
        FactionsXL.getInstance().getPageGUIs().guis.add(this);
    }

    /**
     * partitions must be between 0 and 2
     */
    public PageGUI(String title, int partitions) {
        if (partitions < 0) {
            partitions = 0;
        } else if (partitions > 2) {
            partitions = 2;
        }
        this.title = title;
        this.partitions = partitions;
        if (partitions > 0) {
            pages1 = new Stack<>();
            pages1.add(new ArrayList<>());
            pages2 = new Stack<>();
            pages2.add(new ArrayList<>());
            if (partitions == 2) {
                pages3 = new Stack<>();
                pages3.add(new ArrayList<>());
            }
        }
        newPage();
        FactionsXL.getInstance().getPageGUIs().guis.add(this);
    }

    public Inventory newPage() {
        Inventory gui = Bukkit.createInventory(null, 54, title);
        gui.setItem(45, PREVIOUS_PAGE);
        gui.setItem(46, PLACEHOLDER);
        gui.setItem(47, PLACEHOLDER);
        gui.setItem(48, PLACEHOLDER);
        gui.setItem(49, PLACEHOLDER);
        gui.setItem(50, PLACEHOLDER);
        gui.setItem(51, PLACEHOLDER);
        gui.setItem(52, PLACEHOLDER);
        gui.setItem(53, NEXT_PAGE);
        if (partitions == 1) {
            gui.setItem(18, PREVIOUS_PAGE);
            gui.setItem(19, PLACEHOLDER);
            gui.setItem(20, PLACEHOLDER);
            gui.setItem(21, PLACEHOLDER);
            gui.setItem(22, PLACEHOLDER);
            gui.setItem(23, PLACEHOLDER);
            gui.setItem(24, PLACEHOLDER);
            gui.setItem(25, PLACEHOLDER);
            gui.setItem(26, NEXT_PAGE);
        } else if (partitions == 2) {
            gui.setItem(9, PREVIOUS_PAGE);
            gui.setItem(10, PLACEHOLDER);
            gui.setItem(11, PLACEHOLDER);
            gui.setItem(12, PLACEHOLDER);
            gui.setItem(13, PLACEHOLDER);
            gui.setItem(14, PLACEHOLDER);
            gui.setItem(15, PLACEHOLDER);
            gui.setItem(16, PLACEHOLDER);
            gui.setItem(17, NEXT_PAGE);
            gui.setItem(27, PREVIOUS_PAGE);
            gui.setItem(28, PLACEHOLDER);
            gui.setItem(29, PLACEHOLDER);
            gui.setItem(30, PLACEHOLDER);
            gui.setItem(31, PLACEHOLDER);
            gui.setItem(32, PLACEHOLDER);
            gui.setItem(33, PLACEHOLDER);
            gui.setItem(34, PLACEHOLDER);
            gui.setItem(35, NEXT_PAGE);
        }
        if (partitions == 0) {
            pages.add(gui);
        } else {
            base = gui.getContents();
        }
        return gui;
    }

    public void addButton(ItemStack button) {
        if (pages.size() > 0 && hasSpace(pages.peek())) {
            pages.peek().addItem(button);
        } else {
            newPage().addItem(button);
        }
    }

    public void addButton1(ItemStack button) {
        if (pages1.size() > 0 && hasSpace(pages1.peek())) {
            pages1.peek().add(button);
        } else {
            pages1.add(new ArrayList<>(Arrays.asList(button)));
        }
    }

    public void removeButton1(ItemStack button) {
        pages1.forEach(p -> p.remove(button));
    }

    public void clearButtons1() {
        pages1.clear();
    }

    public void addButton2(ItemStack button) {
        if (pages2.size() > 0 && hasSpace(pages2.peek())) {
            pages2.peek().add(button);
        } else {
            pages2.add(new ArrayList<>(Arrays.asList(button)));
        }
    }

    public void removeButton2(ItemStack button) {
        pages2.forEach(p -> p.remove(button));
    }

    public void clearButtons2() {
        pages2.clear();
    }

    public void addButton3(ItemStack button) {
        if (pages3.size() > 0 && hasSpace(pages3.peek())) {
            pages3.peek().add(button);
        } else {
            pages3.add(new ArrayList<>(Arrays.asList(button)));
        }
    }

    public void removeButton3(ItemStack button) {
        pages3.forEach(p -> p.remove(button));
    }

    public void clearButtons3() {
        pages3.clear();
    }

    public String getTitle() {
        return title;
    }

    public Stack<Inventory> getPages() {
        return pages;
    }

    public Inventory open(HumanEntity player) {
        Inventory gui = pages.get(0);
        player.openInventory(gui);
        return gui;
    }

    public Inventory open(HumanEntity player, int page) {
        if (pages.size() - 1 >= page && page >= 0) {
            Inventory gui = pages.get(page);
            player.openInventory(gui);
            return gui;
        }
        return null;
    }

    public Inventory open(HumanEntity player, int page1, int page2) {
        boolean check1 = pages1.size() - 1 >= page1;
        boolean check2 = pages2.size() - 1 >= page2;
        if (!check1 || !check2) {
            return null;
        }
        Inventory gui = generateBase();
        fillWithItems(gui, pages1.get(page1), 0, 17);
        fillWithItems(gui, pages2.get(page2), 27, 44);
        player.openInventory(gui);
        return gui;
    }

    public Inventory open(HumanEntity player, int page1, int page2, int page3) {
        boolean check1 = pages1.size() - 1 >= page1;
        boolean check2 = pages2.size() - 1 >= page2;
        boolean check3 = pages3.size() - 1 >= page3;
        if (!check1 || !check2 || !check3) {
            return null;
        }
        Inventory gui = generateBase();
        fillWithItems(gui, pages1.get(page1), 0, 8);
        fillWithItems(gui, pages2.get(page2), 18, 27);
        fillWithItems(gui, pages3.get(page3), 36, 44);
        player.openInventory(gui);
        return gui;
    }

    private Inventory generateBase() {
        Inventory gui = Bukkit.createInventory(null, 54, title);
        gui.setContents(base);
        return gui;
    }

    public void clear() {
        pages.clear();
        newPage();
    }

    private boolean hasSpace(List<ItemStack> buttons) {
        if ((getPartitions() == 1 && buttons.size() > 18) || (getPartitions() == 2 && buttons.size() > 9)) {
            return false;
        } else if ((getPartitions() == 1 && buttons.size() < 18) || (getPartitions() == 2 && buttons.size() < 9)) {
            return true;
        } else if ((getPartitions() == 1 && buttons.size() == 18) || (getPartitions() == 2 && buttons.size() == 9)) {
            for (ItemStack stack : buttons) {
                if (stack == null || stack.getType() == Material.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getPartitions() {
        if (pages1 == null) {
            return 0;
        } else {
            return pages3 == null ? 1 : 2;
        }
    }

    /* Statics */
    public static void fillWithItems(Inventory inventory, List<ItemStack> items, int start, int stop) {
        if (start > stop || items.isEmpty()) {
            return;
        }
        int i = 0;
        do {
            if (inventory.getItem(start + i) == null || inventory.getItem(start + i).getType() == Material.AIR) {
                inventory.setItem(start + i, items.get(i));
            }
            i++;
        } while (start + i <= stop && items.size() > i);
    }

    public static boolean hasSpace(Inventory inventory) {
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                return true;
            }
        }
        return false;
    }

    public static void playSound(InventoryClickEvent event) {
        if (event.getSlot() == -999) {
            return;
        }

        HumanEntity human = event.getWhoClicked();
        if (!(human instanceof Player)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            ((Player) human).playSound(human.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
        } else if (clicked != null && !clicked.equals(PLACEHOLDER)) {
            ((Player) human).playSound(human.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }
    }

    public static boolean isPageGUI(Inventory inventory) {
        return inventory != null && inventory.getSize() == 54 && PREVIOUS_PAGE.equals(inventory.getItem(45)) && PLACEHOLDER.equals(inventory.getItem(49));
    }

    public static PageGUI getByInventory(Inventory inventory) {
        for (PageGUI gui : FactionsXL.getInstance().getPageGUIs().guis) {
            if (gui.title.equals(inventory.getTitle())) {
                return gui;
            }
        }
        return null;
    }

    public static void addHeader(Inventory gui) {
        gui.setItem(0, BACK);
        gui.setItem(1, PLACEHOLDER);
        gui.setItem(2, PLACEHOLDER);
        gui.setItem(3, PLACEHOLDER);
        gui.setItem(4, PLACEHOLDER);
        gui.setItem(5, PLACEHOLDER);
        gui.setItem(6, PLACEHOLDER);
        gui.setItem(7, PLACEHOLDER);
        gui.setItem(8, PLACEHOLDER);
    }

    public static void clearHeaderGUI(Inventory gui) {
        int i = 9;
        do {
            gui.clear(i);
            i++;
        } while (i < gui.getSize());
    }

}
