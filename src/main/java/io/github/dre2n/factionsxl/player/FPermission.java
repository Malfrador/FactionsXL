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
package io.github.dre2n.factionsxl.player;

import io.github.dre2n.commons.util.EnumUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import static org.bukkit.permissions.PermissionDefault.*;

/**
 * @author Daniel Saukel
 */
public enum FPermission {

    // Main nodes
    ADMIN("admin", TRUE),
    BUILD("build", OP),
    CHAT("chat", TRUE),
    CLAIM("claim", TRUE),
    CREATE("create", OP),
    CREATE_VASSAL("createvassal", OP),
    DESC("desc", OP),
    DISBAND("disband", TRUE),
    HELP("help", TRUE),
    HOME("home", OP),
    HOME_OTHERS("home.others", OP),
    HOME_SET("home.set", TRUE),
    IDEA("idea", TRUE),
    INVITE("invite", TRUE),
    JOIN("join", TRUE),
    KICK("kick", TRUE),
    LEAVE("leave", TRUE),
    LIST("list", TRUE),
    MAIN("main", TRUE),
    MOB("mob", TRUE),
    MOD("mod", TRUE),
    MONEY("money", TRUE),
    OPEN("open", TRUE),
    PAYDAY("payday", OP),
    POWER("power", TRUE),
    REGION("region", TRUE),
    REGIONS("regions", OP),
    RELATION("relation", TRUE),
    RELOAD("reload", OP),
    SCOREBOARD("scoreboard", OP),
    SET_ANTHEM("set.anthem", TRUE),
    SET_BANNER("set.banner", TRUE),
    SET_CAPITAL("set.capital", TRUE),
    SET_COLOR("set.color", TRUE),
    SET_GOVERNMENT("set.government", TRUE),
    STORAGE("storage", TRUE),
    TAG("tag", OP),
    TITLE("title", TRUE),
    TRADE_OFFER("tradeoffer", TRUE),
    // Kits
    ADMINISTRATOR("*", OP),
    HALF_PLAYER("halfplayer", TRUE, ADMIN, CHAT, CLAIM, DISBAND, HELP, HOME_SET, IDEA, INVITE, JOIN, LEAVE, KICK, LIST, MAIN, MOB, MOD, OPEN,
            POWER, REGION, RELATION, SCOREBOARD, SET_ANTHEM, SET_BANNER, SET_CAPITAL, SET_COLOR, SET_GOVERNMENT, STORAGE, TITLE, TRADE_OFFER),
    FULL_PLAYER("fullplayer", OP, HALF_PLAYER, HOME, CREATE, CREATE_VASSAL, TAG);

    public static final String PREFIX = "fxl.";

    private String node;
    private PermissionDefault isDefault;
    private List<FPermission> children = new ArrayList<>();

    FPermission(String node, PermissionDefault isDefault) {
        this.node = node;
        this.isDefault = isDefault;
    }

    FPermission(String node, PermissionDefault isDefault, FPermission... children) {
        this(node, isDefault);
        this.children = Arrays.asList(children);
    }

    /**
     * @return the permission node String
     */
    public String getNode() {
        return PREFIX + node;
    }

    /**
     * @return if a player has the node by default
     */
    public PermissionDefault isDefault() {
        return isDefault;
    }

    /**
     * @return if the node has children
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * @return the child permissions
     */
    public List<FPermission> getChildren() {
        return children;
    }

    /**
     * @param node
     * the node String, with or without "dxl."
     * @return
     * the FPermission value
     */
    public static FPermission getByNode(String node) {
        for (FPermission permission : values()) {
            if (permission.getNode().equals(node) || permission.node.equals(node)) {
                return permission;
            }
        }

        return null;
    }

    /**
     * @param permission
     * the permission to check
     * @return if the player has the permission
     */
    public static boolean hasPermission(CommandSender sender, FPermission permission) {
        if (sender.hasPermission(permission.getNode())) {
            return true;
        }

        for (FPermission parent : FPermission.values()) {
            if (parent.getChildren().contains(permission) && sender.hasPermission(parent.getNode())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param permission
     * the permission to check
     * @return if the player has the permission
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        }

        FPermission dPermission = null;
        if (EnumUtil.isValidEnum(FPermission.class, permission)) {
            dPermission = FPermission.valueOf(permission);

        } else if (FPermission.getByNode(permission) != null) {
            dPermission = FPermission.getByNode(permission);
        }

        if (dPermission == null) {
            return false;
        }

        for (FPermission parent : FPermission.values()) {
            if (parent.getChildren().contains(dPermission) && sender.hasPermission(parent.getNode())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Registers the permissions.
     */
    public static void register() {
        for (FPermission permission : values()) {
            Bukkit.getPluginManager().addPermission(new Permission(permission.getNode(), permission.isDefault()));
        }
    }

}
