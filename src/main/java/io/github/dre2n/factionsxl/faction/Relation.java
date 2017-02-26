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

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import static org.bukkit.ChatColor.*;
import org.bukkit.entity.Player;

/**
 * An enumeration of relations between factions and players.
 *
 * @author Daniel Saukel
 */
public enum Relation {

    REAL_UNION(DARK_GREEN, true, FMessage.RELATION_REAL_UNION),
    ALLIANCE(LIGHT_PURPLE, true, FMessage.RELATION_ALLIANCE),
    PERSONAL_UNION(YELLOW, true, FMessage.RELATION_PERSONAL_UNION, ALLIANCE),
    /**
     * vassal faction ===> Relation.LORD ===> lord faction
     */
    LORD(DARK_AQUA, false, FMessage.RELATION_LORD, ALLIANCE),
    OWN(GREEN, true, FMessage.RELATION_OWN, ALLIANCE, PERSONAL_UNION),
    /**
     * lord faction ===> Relation.VASSAL ===> vassal faction
     */
    VASSAL(AQUA, true, FMessage.RELATION_VASSAL, ALLIANCE),
    COALITION(DARK_PURPLE, true, FMessage.RELATION_COALITION),
    PEACE(WHITE, false, FMessage.RELATION_PEACE),
    ENEMY(RED, false, FMessage.RELATION_ENEMY);

    private ChatColor color;
    private boolean build;
    private FMessage name;
    private Set<Relation> included = new HashSet<>();

    Relation(ChatColor color, boolean build, FMessage name, Relation... included) {
        this.color = color;
        this.build = build;
        this.name = name;
        this.included.addAll(Arrays.asList(included));
    }

    /**
     * @return
     * the chat color
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     * @return
     * if you can build
     */
    public boolean canBuild() {
        return build;
    }

    /**
     * @return
     * the name of the type
     */
    public String getName() {
        return color + name.getMessage();
    }

    /**
     * @return
     * the description
     */
    public String getDescription() {
        return color + ChatColor.BOLD.toString() + name.getMessage() + ChatColor.RESET + "\n" + FMessage.valueOf(name + "_DESC").getMessage();
    }

    /**
     * @return
     * the relations that are included in this relation
     */
    public Set<Relation> getIncludedRelations() {
        return included;
    }

    /**
     * @return
     * the relation formatted with hover description
     */
    public BaseComponent[] getFormatted() {
        BaseComponent[] components = TextComponent.fromLegacyText(getName());
        HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(getDescription()));
        for (BaseComponent component : components) {
            component.setHoverEvent(hover);
        }
        return components;
    }

    /**
     * @return
     * the faction name formatted with hover description
     */
    public BaseComponent[] getFormatted(Faction faction) {
        BaseComponent[] components = TextComponent.fromLegacyText(color + faction.getName());
        HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(getDescription()));
        for (BaseComponent component : components) {
            component.setHoverEvent(hover);
        }
        return components;
    }

    /**
     * @param string
     * the String to check
     * @return
     * the relation
     */
    public static Relation fromString(String string) {
        switch (string.toLowerCase()) {
            case "real_union":
            case "realunion":
            case "ru":
                return REAL_UNION;
            case "personal_union":
            case "personalunion":
            case "pu":
                return PERSONAL_UNION;
            case "vassal":
            case "v":
                return VASSAL;
            case "lord":
            case "l":
                return LORD;
            case "alliance":
            case "ally":
            case "a":
                return ALLIANCE;
            case "coalition":
            case "c":
                return COALITION;
            case "own":
            case "o":
                return OWN;
            case "peace":
            case "p":
            case "neutral":
            case "n":
                return PEACE;
            case "enemy":
            case "e":
                return ENEMY;
            default:
                return null;
        }
    }

    public static class Request {

        private Faction subject;
        private Faction object;
        private Relation relation;

        public Request(Faction subject, Faction object, Relation relation) {
            this.subject = subject;
            this.object = object;
            this.relation = relation;
        }

        public Faction getSubject() {
            return subject;
        }

        public Faction getObject() {
            return object;
        }

        public Relation getRelation() {
            return relation;
        }

        /**
         * Requires Spigot API!
         *
         * Sends the relation request to the object faction
         */
        public void send() {
            String command = "/factionsxl relation " + object.getName() + " " + subject.getName() + " " + relation.toString();
            ClickEvent onClickConfirm = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            TextComponent confirm = new TextComponent(ChatColor.GREEN + FMessage.MISC_ACCEPT.getMessage());
            confirm.setClickEvent(onClickConfirm);

            ClickEvent onClickDeny = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " -deny");
            TextComponent deny = new TextComponent(ChatColor.DARK_RED + FMessage.MISC_DENY.getMessage());
            deny.setClickEvent(onClickDeny);

            object.sendMessage(FMessage.RELATION_WISH.getMessage(), subject, relation.getName());// TODO getFormatted()
            for (Player player : object.getOnlineMods()) {
                player.spigot().sendMessage(confirm, new TextComponent(" "), deny);
            }
            if (object.getAdmin().isOnline()) {
                object.getAdmin().getPlayer().spigot().sendMessage(confirm, new TextComponent(" "), deny);
            }
        }

        /**
         * Applies the requested relation to the factions
         */
        public void confirm() {
            if (relation == REAL_UNION) {
                FactionsXL.getInstance().getFactionCache().formRealUnion(subject, object);
                ParsingUtil.broadcastMessage(FMessage.RELATION_UNITED.getMessage(), subject, object);
                return;
            }
            subject.getRelations().put(object, relation);
            if (relation != LORD && relation != VASSAL) {
                object.getRelations().put(subject, relation);
                ParsingUtil.broadcastMessage(FMessage.RELATION_CONFIRMED.getMessage(), subject, object, relation.getName());
            } else if (relation == VASSAL) {
                object.getRelations().put(subject, LORD);
                ParsingUtil.broadcastMessage(FMessage.RELATION_VASSALIZED.getMessage(), subject, object);
            } else if (relation == LORD) {
                object.getRelations().put(subject, VASSAL);
                ParsingUtil.broadcastMessage(FMessage.RELATION_VASSALIZED.getMessage(), object, subject);
            }
        }

    }

}
