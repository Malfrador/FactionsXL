/*
 *
 *  * Copyright (C) 2017-2020 Daniel Saukel, Malfrador
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.erethon.factionsxl.war.peaceoffer;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.gui.GUIButton;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.faction.LegalEntity;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.WarPartyRole;
import de.erethon.factionsxl.war.demand.WarDemand;

import java.util.*;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class SeparatePeaceOffer extends PeaceOffer {

    FactionsXL plugin = FactionsXL.getInstance();
    Double cost = 0.00;

    public SeparatePeaceOffer(War war, LegalEntity demanding, LegalEntity target, WarDemand... demands) {
        this.war = war;
        subject = demanding;
        object = target;
        expiration = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        this.demands = new ArrayList(Arrays.asList(demands));
    }

    public SeparatePeaceOffer(Map<String, Object> args) {
        //TODO: WarCache noch nicht geladen. WarCache kann nicht vor Fraktionen geladen werden. Fraktionen brauchen aber WarCache, um das hier zu laden.
        new BukkitRunnable() {

            @Override
            public void run() {
                war =  FactionsXL.getInstance().getWarCache().getByDate((long) args.get("war"));
                subject = plugin.getFactionCache().getById((int) args.get("subject"));
                object = plugin.getFactionCache().getById((int) args.get("object"));
                demands = (List<WarDemand>) args.get("demands");
                expiration = (long) args.get("expiration");
            }

        }.runTaskLater(this.plugin, 20);
    }

    @Override
    public Faction getSubject() {
        return (Faction) subject;
    }

    @Override
    public Faction getObject() {
        return (Faction) object;
    }

    @Override
    public boolean canPay() {
        boolean canPay = true;
        for (WarDemand demand : demands) {
            if (!demand.canPay(getObject())) {
                canPay = false;
            }
        }
        return canPay;
    }

    public double getCost() {
        cost = 0.00;
        for (WarDemand d : demands) {
            cost = cost + d.getWarscoreCost();
        }
        return Math.round(cost * 100.00) / 100.0;
    }

    @Override
    public void confirm() {
        demands.forEach(d -> d.pay(getSubject(), getObject()));
        if (war.getAttacker().getFactions().contains(getObject())) {
            war.getAttacker().removeParticipant(getObject());
        }
        if (war.getDefender().getFactions().contains(getObject())) {
            war.getDefender().removeParticipant(getObject());
        }
        MessageUtil.broadcastMessage(" ");
        MessageUtil.broadcastMessage("&aThe faction &e" + getObject().getName() + " &adecided to leave the war against &6" + getSubject().getName() + "&a!");
        MessageUtil.broadcastMessage(" ");
        // TODO: Might break after government update
        // TODO: Add time modifier
        purge();
    }

    @Override
    public void send() {
        ClickEvent onClickConfirm = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getAcceptCommand());
        TextComponent confirm = new TextComponent(ChatColor.GREEN + FMessage.MISC_ACCEPT.getMessage());
        confirm.setClickEvent(onClickConfirm);

        ClickEvent onClickDeny = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getDenyCommand());
        TextComponent deny = new TextComponent(ChatColor.DARK_RED + FMessage.MISC_DENY.getMessage());
        deny.setClickEvent(onClickDeny);

        boolean add = true;
        for (PeaceOffer check : getObject().getRequests(PeaceOffer.class)) {
            if (check.getSubject() == subject && check.getObject() == object) {
                add = false;
                break;
            }
        }
        if (!(add)) {
            for (Player player : object.getRequestAuthorizedPlayers(getClass()).getOnlinePlayers()) {
                MessageUtil.sendMessage(player, "&cRequest already sent. They first need to accept or deny.");
            }
        }
        if (add) {
            getObject().getRequests().add(this);
        }

        sendSubjectMessage();
        sendObjectMessage();
        for (Player player : object.getRequestAuthorizedPlayers(getClass()).getOnlinePlayers()) {
            MessageUtil.sendMessage(player, confirm, new TextComponent(" "), deny);
        }
    }

    @Override
    public ItemStack getButton(Player player) {
        String title = ParsingUtil.parseMessage(player, "&f&lPeace request &8- &6" + getSubject().getName());
        return GUIButton.setDisplay(new ItemStack(Material.WHITE_BANNER), title);
    }

    @Override
    public String getAcceptCommand() {
        return "/f confirmPeace " + war.getStartDate().getTime() + " -accept " + object.getName() + " " + subject.getName();
    }

    @Override
    public String getDenyCommand() {
        return "/f confirmPeace " + war.getStartDate().getTime() + " -deny " + object.getName() + " " + subject.getName();
    }

    @Override
    public void sendSubjectMessage() {
        Faction f = getSubject();
        f.sendMessage("&8&m----------&r &f&lPeace request&r &8&m----------");
        f.sendMessage(" ");
        for (Object d : demands) {
            f.sendMessage(d.toString());
        }
        f.sendMessage(" ");
        f.sendMessage("&aPeace request sent!");

    }

    @Override
    public void sendObjectMessage() {
        Faction f = getObject();
        f.sendMessage("&8&m----------&r &f&lPeace request&r &8&m----------");
        f.sendMessage(" ");
        f.sendMessage("&7&oAccepting this request will end the war immediately.");
        f.sendMessage("&7&oBut you will need to pay the following to the demanding faction: ");
        f.sendMessage("&6&lDemands&8&l:");
        for (Object d : demands) {
            f.sendMessage(d.toString());
        }
        f.sendMessage(" ");

    }

    public void purge() {
        // should remove the request. Does not persist restarts anyway.
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> args = new HashMap<>();
        args.put("war", war.getStartDate().getTime());
        args.put("subject", getSubject().getId());
        args.put("object", getObject().getId());
        args.put("demands", demands);
        args.put("expiration", expiration);
        return args;
    }

}
