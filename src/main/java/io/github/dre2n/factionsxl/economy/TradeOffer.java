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

import io.github.dre2n.factionsxl.faction.Faction;

/**
 * @author Daniel Saukel
 */
public class TradeOffer {

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

}
