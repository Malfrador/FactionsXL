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
package io.github.dre2n.factionsxl.faction;

import io.github.dre2n.factionsxl.economy.FAccount;
import io.github.dre2n.factionsxl.player.FPermission;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/**
 * @author Daniel Saukel
 */
public abstract class LegalEntity {

    int id;
    String name;
    String longName;
    String shortName;
    String desc = "Default description :(";
    String anthem;
    BannerMeta banner;
    short bannerColor;
    UUID admin;
    long creationDate;
    FAccount account;

    /**
     * @return
     * the ID number
     */
    public int getId() {
        return id;
    }

    /**
     * @return
     * the entity's name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     * the faction's long name
     */
    public String getLongName() {
        return longName != null ? longName : name;
    }

    /**
     * @param name
     * the name to set
     */
    public void setLongName(String name) {
        longName = name;
    }

    /**
     * @return
     * the faction's long name
     */
    public String getShortName() {
        return shortName != null ? shortName : name;
    }

    /**
     * @param name
     * the name to set
     */
    public void setShortName(String name) {
        shortName = name;
    }

    /**
     * @return
     * the description of the faction
     */
    public String getDescription() {
        return desc;
    }

    /**
     * @param description
     * the description to set
     */
    public void setDescription(String description) {
        desc = description;
    }

    /**
     * @return
     * the description of the anthem
     */
    public String getAnthem() {
        return anthem;
    }

    /**
     * @param anthem
     * an identifier to play the anthem
     */
    public void setAnthem(String anthem) {
        this.anthem = anthem;
    }

    /**
     * @return
     * the faction's banner
     */
    public BannerMeta getBanner() {
        return banner;
    }

    /**
     * @return
     * the faction's banner as an ItemStack
     */
    public ItemStack getBannerStack() {
        ItemStack stack = new ItemStack(Material.BANNER, 1, bannerColor);
        stack.setItemMeta(banner);
        return stack;
    }

    /**
     * @param banner
     * the banner to set
     */
    public void setBanner(ItemStack banner) {
        if (banner.getType() != Material.BANNER && banner.getType() != Material.SHIELD) {
            return;
        }

        this.banner = (BannerMeta) banner.getItemMeta();
        bannerColor = banner.getDurability();
    }

    /**
     * @return
     * the background color damage value of the banner
     */
    public short getBannerColor() {
        return bannerColor;
    }

    /**
     * @return
     * the admin of the faction
     */
    public OfflinePlayer getAdmin() {
        return Bukkit.getOfflinePlayer(admin);
    }

    /**
     * @param admin
     * the new admin to set
     */
    public void setAdmin(OfflinePlayer admin) {
        if (admin != null) {
            this.admin = admin.getUniqueId();
        } else {
            admin = null;
        }
    }

    /**
     * @param sender
     * a CommandSender
     * @return
     * if the sender has admin rights in this faction
     */
    public boolean isAdmin(CommandSender sender) {
        if (admin == null) {
            return false;
        }
        return getAdmin().getName().equals(sender.getName()) || FPermission.hasPermission(sender, FPermission.BYPASS);
    }

    /**
     * @param uuid
     * a unique ID of a Player
     * @return
     * if the sender has admin rights in this faction
     */
    public boolean isAdmin(UUID uuid) {
        if (admin == null) {
            return false;
        }
        return admin.equals(uuid);
    }

    /**
     * @param playerName
     * the name of a Player
     * @return
     * if the sender has admin rights in this faction
     */
    public boolean isAdmin(String playerName) {
        if (admin == null) {
            return false;
        }
        return getAdmin().getName().equals(playerName);
    }

    /**
     * @return
     * the creation date
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * @return
     * the entity's bank account
     */
    public FAccount getAccount() {
        return account;
    }

}
