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
package io.github.dre2n.factionsxl.economy;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.util.GUIButton;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TradeOffer {

    Economy econ = FactionsXL.getInstance().getEconomyProvider();

    private Resource good;
    private int amount;
    private Faction importer;
    private Faction exporter;
    private double price;
    private boolean importerAccepted;
    private boolean exporterAccepted;

    public TradeOffer(Resource good, int amount, Faction importer, Faction exporter, double price) {
        this.good = good;
        this.amount = amount;
        this.importer = importer;
        this.exporter = exporter;
        this.price = price;
    }

    public TradeOffer(Faction creator, Faction partner) {
        importer = creator;
        exporter = partner;
        importerAccepted = true;
    }

    public TradeOffer(Resource good, Faction importer, Faction exporter) {
        this.good = good;
        this.importer = importer;
        this.exporter = exporter;
    }

    public Resource getGood() {
        return good;
    }

    public void setGood(Resource good) {
        this.good = good;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Faction getImporter() {
        return importer;
    }

    public void setImporter(Faction importer) {
        this.importer = importer;
    }

    public Faction getExporter() {
        return exporter;
    }

    public void setExporter(Faction exporter) {
        this.exporter = exporter;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean hasImporterAccepted() {
        return importerAccepted;
    }

    public void setImporterAccepted(boolean accepted) {
        importerAccepted = accepted;
    }

    public boolean hasExporterAccepted() {
        return exporterAccepted;
    }

    public void setExporterAccepted(boolean accepted) {
        exporterAccepted = accepted;
    }

    public boolean isValid() {
        return importerAccepted && exporterAccepted;
    }

    public boolean check(CommandSender sender) {
        if (importer.getAccount().getBalance() < price + getFee()) {
            String formatted = econ.format(price + getFee());
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NOT_ENOUGH_MONEY_FACTION.getMessage(), importer, formatted);
            return false;
        } else if (!exporter.canAfford(good, amount)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NOT_ENOUGH_MONEY_FACTION.getMessage(), exporter, amount + " " + good.getName());
            return false;
        } else {
            return true;
        }
    }

    public void send() {
        Faction receiver = null;
        if (!importerAccepted) {
            receiver = importer;
            importer.sendMessage(FMessage.TRADE_OFFER_SEND_TRADE.getMessage(), exporter);
            importer.sendMessage(FMessage.TRADE_OFFER_SEND_EXPORT.getMessage(), amount + " " + good.getName(), econ.format(price));
        } else if (!exporterAccepted) {
            receiver = exporter;
            exporter.sendMessage(FMessage.TRADE_OFFER_SEND_TRADE.getMessage(), importer);
            exporter.sendMessage(FMessage.TRADE_OFFER_SEND_IMPORT.getMessage(), amount + " " + good.getName(), econ.format(price));
        }

        String command = "/factionsxl tradeOffer " + (FactionsXL.getInstance().getCommandCache().tradeOffer.finishedOffers.size() - 1);
        ClickEvent onClickConfirm = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        BaseComponent confirm = GUIButton.CONFIRM.duplicate();
        confirm.setClickEvent(onClickConfirm);

        ClickEvent onClickDeny = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " -deny");
        BaseComponent deny = GUIButton.DENY.duplicate();
        deny.setClickEvent(onClickDeny);

        for (Player player : receiver.getOnlineMods()) {
            player.spigot().sendMessage(confirm, new TextComponent(" "), deny);
        }
        if (receiver.getAdmin().isOnline()) {
            receiver.getAdmin().getPlayer().spigot().sendMessage(confirm, new TextComponent(" "), deny);
        }
    }

    public double getFee() {
        return 0;//TODO
    }

    public void accept(CommandSender sender) {
        if (!importerAccepted) {
            if (importer.isPrivileged(sender)) {
                importerAccepted = true;
            }
        } else if (!exporterAccepted) {
            if (exporter.isPrivileged(sender)) {
                exporterAccepted = true;
            }
        }

        importer.chargeResource(good, -1 * amount);
        importer.getAccount().withdraw(price + getFee());
        exporter.chargeResource(good, amount);
        exporter.getAccount().deposit(price - getFee());
        importer.sendMessage(FMessage.TRADE_SUCCESS_IMPORT.getMessage(), amount + " " + good.getName(), econ.format(price), exporter);
        exporter.sendMessage(FMessage.TRADE_SUCCESS_EXPORT.getMessage(), amount + " " + good.getName(), econ.format(price), importer);
    }

}
