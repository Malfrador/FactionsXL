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
package io.github.dre2n.factionsxl.command;

import io.github.dre2n.commons.command.BRCommand;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.economy.Resource;
import io.github.dre2n.factionsxl.economy.TradeOffer;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.util.ItemUtil;
import io.github.dre2n.factionsxl.util.PageGUI;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
public class TradeOfferCommand extends BRCommand implements Listener {

    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();

    public final String TRADE_OFFER_CHOOSE_PRICE_AND_AMOUNT = ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
            + FMessage.TRADE_OFFER_AMOUNT.getMessage() + ": " + ChatColor.DARK_AQUA.toString() + "&v1 " + ChatColor.GREEN.toString()
            + ChatColor.BOLD.toString() + FMessage.TRADE_OFFER_PRICE.getMessage() + ": " + ChatColor.DARK_AQUA.toString() + "&v2";
    public final String PLUS = ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "+";
    public final String MINUS = ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "-";
    public final String AMOUNT = ChatColor.WHITE + FMessage.TRADE_OFFER_AMOUNT.getMessage() + " ";
    public final String PRICE = ChatColor.WHITE + FMessage.TRADE_OFFER_PRICE.getMessage() + " ";

    private Inventory chooseResource;
    private Inventory chooseExport;
    private ItemStack[] choosePriceAndAmount;

    private Map<Integer, Faction> factionBySlot = new HashMap<>();
    private Map<HumanEntity, TradeOffer> creatingOffer = new HashMap<>();

    public TradeOfferCommand() {
        setCommand("tradeOffer");
        setMinArgs(0);
        setMaxArgs(7);
        setHelp(FMessage.HELP_CMD_TRADE_OFFER.getMessage());
        setPermission(FPermission.TRADE_OFFER.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        chooseResource = chooseResource();
        chooseExport = chooseExport();

        ItemStack amountInc100 = ItemUtil.setDisplayName(ItemUtil.UP, AMOUNT + PLUS + 100);
        ItemStack amountInc10 = ItemUtil.setDisplayName(ItemUtil.UP, AMOUNT + PLUS + 10);
        ItemStack amountInc1 = ItemUtil.setDisplayName(ItemUtil.UP, AMOUNT + PLUS + 1);
        ItemStack amountDec100 = ItemUtil.setDisplayName(ItemUtil.DOWN, AMOUNT + MINUS + 100);
        ItemStack amountDec10 = ItemUtil.setDisplayName(ItemUtil.DOWN, AMOUNT + MINUS + 10);
        ItemStack amountDec1 = ItemUtil.setDisplayName(ItemUtil.DOWN, AMOUNT + MINUS + 1);
        ItemStack priceInc1000 = ItemUtil.setDisplayName(ItemUtil.UP, PRICE + PLUS + 1000);
        ItemStack priceInc100 = ItemUtil.setDisplayName(ItemUtil.UP, PRICE + PLUS + 100);
        ItemStack priceInc10 = ItemUtil.setDisplayName(ItemUtil.UP, PRICE + PLUS + 10);
        ItemStack priceInc1 = ItemUtil.setDisplayName(ItemUtil.UP, PRICE + PLUS + 1);
        ItemStack priceInc01 = ItemUtil.setDisplayName(ItemUtil.UP, PRICE + PLUS + 0.10);
        ItemStack priceInc001 = ItemUtil.setDisplayName(ItemUtil.UP, PRICE + PLUS + 0.01);
        ItemStack priceDec1000 = ItemUtil.setDisplayName(ItemUtil.DOWN, PRICE + MINUS + 1000);
        ItemStack priceDec100 = ItemUtil.setDisplayName(ItemUtil.DOWN, PRICE + MINUS + 100);
        ItemStack priceDec10 = ItemUtil.setDisplayName(ItemUtil.DOWN, PRICE + MINUS + 10);
        ItemStack priceDec1 = ItemUtil.setDisplayName(ItemUtil.DOWN, PRICE + MINUS + 1);
        ItemStack priceDec01 = ItemUtil.setDisplayName(ItemUtil.DOWN, PRICE + MINUS + 0.10);
        ItemStack priceDec001 = ItemUtil.setDisplayName(ItemUtil.DOWN, PRICE + MINUS + 0.01);
        choosePriceAndAmount = new ItemStack[]{
            amountInc100, amountInc10, amountInc1,
            priceInc1000, priceInc100, priceInc10, priceInc1, priceInc01, priceInc001,
            amountDec100, amountDec10, amountDec1,
            priceDec1000, priceDec100, priceDec10, priceDec1, priceDec01, priceDec001
        };
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction creator = null;
        if (args.length >= 2) {
            creator = factions.getByName(args[1]);
        } else {
            creator = factions.getByMember((Player) sender);
        }
        if (creator == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args.length >= 2 ? args[1] : "none");
            return;
        }
        if (!creator.isPrivileged(sender)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }

        if (args.length < 3) {
            player.openInventory(choosePartner(creator));
            return;
        }

        Faction partner = factions.getByName(args[2]);
        if (partner == null) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_SUCH_FACTION.getMessage(), args[2]);
            return;
        } else if (partner == creator) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_CANNOT_TRADE_WITH_ITSELF.getMessage());
            return;
        }

        if (args.length < 4) {
            player.openInventory(chooseExport);
            return;
        }

        boolean export = args[3].startsWith("e") || args[3].startsWith("E");
        Faction importer = export ? partner : creator;
        Faction exporter = export ? creator : partner;

        if (args.length < 5) {
            player.openInventory(chooseResource);
            return;
        }

        Resource good = Resource.getByName(args[4]);

        if (args.length < 6) {
            player.openInventory(choosePriceAndAmount(0, 0));
            return;
        }

        int amount = NumberUtil.parseInt(args[5]);
        double price = NumberUtil.parseDouble(args[6]);

        if (args.length < 8 || args[7].equals("-confirm")) {
            player.openInventory(choosePriceAndAmount(amount, price));
            return;
        }

        TradeOffer offer = creatingOffer.get(player);
        if (offer == null) {
            offer = new TradeOffer(good, amount, importer, exporter, price);
        }

    }

    private Inventory choosePartner(Faction creator) {
        int size = (int) (9 * Math.ceil(((double) factions.getActive().size() / 9)));
        Inventory gui = Bukkit.createInventory(null, size, FMessage.TRADE_OFFER_CHOOSE_PARTNER.getMessage());
        int i = 0;
        for (Faction faction : factions.getActive()) {
            ItemStack icon = faction.getBannerStack();
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(creator.getRelation(faction).getColor() + faction.getName());
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            icon.setItemMeta(meta);
            gui.setItem(i, icon);
            factionBySlot.put(i, faction);
            i++;
        }
        return gui;
    }

    private Inventory chooseResource() {
        Inventory gui = Bukkit.createInventory(null, 54, FMessage.TRADE_OFFER_CHOOSE_RESOURCE.getMessage());
        for (Resource resource : Resource.values()) {
            ItemStack icon = null;
            if (resource.isPhysical()) {
                icon = resource.getIcon();
            } else {
                icon = new ItemStack(Material.BARRIER);
            }
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + resource.getName());
            icon.setItemMeta(meta);
            gui.addItem(icon);
        }
        return gui;
    }

    private Inventory chooseExport() {
        Inventory gui = Bukkit.createInventory(null, 9, FMessage.TRADE_OFFER_CHOOSE_EXPORT.getMessage());
        ItemStack exportIcon = ItemUtil.E.clone();
        ItemMeta exMeta = exportIcon.getItemMeta();
        exMeta.setDisplayName(ChatColor.GREEN + FMessage.TRADE_EXPORT.getMessage());
        exportIcon.setItemMeta(exMeta);
        ItemStack importIcon = ItemUtil.I.clone();
        ItemMeta imMeta = importIcon.getItemMeta();
        imMeta.setDisplayName(ChatColor.GREEN + FMessage.TRADE_IMPORT.getMessage());
        importIcon.setItemMeta(imMeta);
        gui.setItem(3, exportIcon);
        gui.setItem(5, importIcon);
        return gui;
    }

    private Inventory choosePriceAndAmount(int amount, double price) {
        Inventory gui = Bukkit.createInventory(null, 18, TRADE_OFFER_CHOOSE_PRICE_AND_AMOUNT.replaceAll("&v1", String.valueOf(amount)).replaceAll("&v2", String.valueOf(price)));
        gui.setContents(choosePriceAndAmount);
        return gui;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = event.getInventory().getTitle();
        if (title.equals(FMessage.TRADE_OFFER_CHOOSE_PARTNER.getMessage())) {
            onClickChoosePartner(event);
        } else if (title.equals(FMessage.TRADE_OFFER_CHOOSE_RESOURCE.getMessage())) {
            onClickChooseResource(event);
        } else if (title.equals(FMessage.TRADE_OFFER_CHOOSE_EXPORT.getMessage())) {
            onClickChooseExport(event);
        } else if (title.startsWith(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + FMessage.TRADE_OFFER_AMOUNT.getMessage())) {
            onClickChoosePriceAndAmount(event);
        } else {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
    }

    private void onClickChoosePartner(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Faction creator = factions.getByMember(player);
        Faction partner = factionBySlot.get(event.getSlot());
        if (creator == null || partner == null) {
            return;
        }
        creatingOffer.put(player, new TradeOffer(creator, partner));
        performCommand(event.getWhoClicked(), creator.getName() + " " + partner.getName());
    }

    private void onClickChooseExport(InventoryClickEvent event) {
        TradeOffer offer = creatingOffer.get(event.getWhoClicked());
        if (offer == null) {
            return;
        }
        Faction creator = offer.getImporter();
        Faction partner = offer.getExporter();
        String exImport = null;
        if (event.getSlot() == 3) {
            exImport = "export";
            offer.setExporter(creator);
            offer.setImporter(partner);
            offer.setExporterAccepted(true);
            offer.setImporterAccepted(false);
        } else if (event.getSlot() == 5) {
            exImport = "import";
            offer.setExporter(partner);
            offer.setImporter(creator);
            offer.setExporterAccepted(false);
            offer.setImporterAccepted(true);
        }
        performCommand(event.getWhoClicked(), creator.getName() + " " + partner.getName() + " " + exImport);
    }

    private void onClickChooseResource(InventoryClickEvent event) {
        TradeOffer offer = creatingOffer.get(event.getWhoClicked());
        if (offer == null) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }
        ItemStack button = event.getCurrentItem();
        if (button == null) {
            return;
        }
        Resource resource = Resource.getByIcon(button);
        if (resource == null) {
            return;
        }
        offer.setGood(resource);

        Faction creator = offer.hasExporterAccepted() ? offer.getExporter() : offer.getImporter();
        Faction partner = offer.hasExporterAccepted() ? offer.getImporter() : offer.getExporter();
        String exImport = offer.hasExporterAccepted() ? "export" : "import";
        performCommand(event.getWhoClicked(), creator.getName() + " " + partner.getName() + " " + exImport + " " + resource.getName());
    }

    private void onClickChoosePriceAndAmount(InventoryClickEvent event) {
        TradeOffer offer = creatingOffer.get(event.getWhoClicked());
        if (offer == null) {
            return;
        }
        Faction creator = offer.hasExporterAccepted() ? offer.getExporter() : offer.getImporter();
        Faction partner = offer.hasExporterAccepted() ? offer.getImporter() : offer.getExporter();
        String exImport = offer.hasExporterAccepted() ? "export" : "import";
        int amount = readAmountFromTitle(event.getInventory().getTitle());
        double price = readPriceFromTitle(event.getInventory().getTitle());
        ItemStack button = event.getCurrentItem();
        if (button == null) {
            return;
        } else {
            String name = button.getItemMeta().getDisplayName();
            if (name.startsWith(AMOUNT)) {
                if (name.startsWith(AMOUNT + PLUS)) {
                    amount += NumberUtil.parseInt(name.replaceAll(AMOUNT + PLUS, new String()));
                } else if (name.startsWith(AMOUNT + MINUS)) {
                    amount -= NumberUtil.parseInt(name.replaceAll(AMOUNT + MINUS, new String()));
                }
            } else if (name.startsWith(PRICE)) {
                if (name.startsWith(PRICE + PLUS)) {
                    price += NumberUtil.parseDouble(name.replaceAll(PRICE + PLUS, new String()));
                } else if (name.startsWith(PRICE + MINUS)) {
                    price -= NumberUtil.parseDouble(name.replaceAll(PRICE + MINUS, new String()));
                }
            }
        }
        performCommand(event.getWhoClicked(), creator.getName() + " " + partner.getName() + " " + exImport + " " + offer.getGood().getName() + " " + amount + " " + price);
    }

    private int readAmountFromTitle(String title) {
        String amount = title.replaceAll(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
                + FMessage.TRADE_OFFER_AMOUNT.getMessage() + ": " + ChatColor.DARK_AQUA.toString(), new String());
        amount = amount.split(" ")[0];
        return NumberUtil.parseInt(amount);
    }

    private double readPriceFromTitle(String title) {
        String price = title.split(ChatColor.DARK_AQUA.toString())[2];
        return NumberUtil.parseDouble(price);
    }

    private final String COMMAND = FCommandCache.LABEL + " tradeOffer ";

    private void performCommand(HumanEntity human, String args) {
        if (!(human instanceof Player)) {
            return;
        }
        Player player = (Player) human;
        player.performCommand(COMMAND + args);
    }

    /*@EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().getTitle().startsWith("&2&l" + FMessage.TRADE_OFFER_AMOUNT.getMessage())) {
            return;
        }
        TradeOffer offer = creatingOffer.get(event.getPlayer());
        if (offer == null) {
            return;
        }
        Faction creator = offer.hasExporterAccepted() ? offer.getExporter() : offer.getImporter();
        Faction partner = offer.hasExporterAccepted() ? offer.getImporter() : offer.getExporter();
        String exImport = offer.hasExporterAccepted() ? "export" : "import";
        int amount = readAmountFromTitle(event.getInventory().getTitle());
        double price = readPriceFromTitle(event.getInventory().getTitle());

        // CHECK
        performCommand(event.getPlayer(), creator.getName() + " " + partner.getName() + " " + exImport + " " + offer.getGood().getName() + " " + amount + " " + price + " -confirm");
        creatingOffer.remove(event.getPlayer());
    }*/
}
