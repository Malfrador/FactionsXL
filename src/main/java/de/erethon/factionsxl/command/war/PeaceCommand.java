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
package de.erethon.factionsxl.command.war;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.gui.GUIButton;
import de.erethon.commons.gui.PageGUI;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.command.FCommand;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.faction.FactionCache;
import de.erethon.factionsxl.gui.StandardizedGUI;
import de.erethon.factionsxl.player.FPermission;
import de.erethon.factionsxl.util.ParsingUtil;
import de.erethon.factionsxl.war.WarCache;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.demand.WarDemandWarPartyGUI;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class PeaceCommand extends FCommand implements Listener, InventoryHolder {

    FactionsXL plugin = FactionsXL.getInstance();
    FactionCache factions = plugin.getFactionCache();
    WarCache wars = plugin.getWarCache();

    private Inventory gui = Bukkit.createInventory(this, 9, FMessage.CMD_PEACE_TITLE.getMessage());
    private ItemStack create = GUIButton.setDisplay(StandardizedGUI.MAILBOX, FMessage.CMD_PEACE_CREATE.getMessage());
    private ItemStack listReceived = GUIButton.setDisplay(StandardizedGUI.MAILBOX, FMessage.CMD_PEACE_LIST_RECEIVED.getMessage());
    private ItemStack listSent = GUIButton.setDisplay(StandardizedGUI.MAILBOX, FMessage.CMD_PEACE_LIST_SENT.getMessage());

    public PeaceCommand() {
        setCommand("peace");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(FMessage.HELP_PEACE.getMessage());
        setPermission(FPermission.WAR.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        gui.setContents(new ItemStack[]{
                GUIButton.PLACEHOLDER,
                GUIButton.PLACEHOLDER,
                GUIButton.PLACEHOLDER,
                create,
                listReceived,
                listSent,
                GUIButton.PLACEHOLDER,
                GUIButton.PLACEHOLDER,
                GUIButton.PLACEHOLDER
        });
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Faction f = factions.getByMember(player);
        boolean inWar = false;
        for (Faction faction : factions.getByLeader(player)) {
            if (faction.isInWar()) {
                inWar = true;
                break;
            }
        }
        if (!factions.getByMember(player).isInWar() && !inWar) {
            MessageUtil.sendMessage(sender, FMessage.ERROR_NOT_IN_WAR.getMessage());
            return;
        }
        if (!f.isAdmin(player)) {
            ParsingUtil.sendMessage(sender, FMessage.ERROR_NO_PERMISSION.getMessage());
            return;
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.getInventory().getHolder() != this) {
            return;
        }
        event.setCancelled(true);
        PageGUI.playSound(event);
        ItemStack button = event.getCurrentItem();
        if (create.equals(button)) {
            Set<Faction> ownFactions = factions.getByLeader(player);
            Set<WarParty> parties = new HashSet<>();
            ownFactions.forEach(f -> f.getWarParties().forEach(p -> parties.add(p.getEnemy())));
            new WarDemandWarPartyGUI(plugin, ownFactions, parties.toArray(new WarParty[]{})).open(player);
        } else if (listReceived.equals(button)) {
            PageGUI received = new PageGUI(FMessage.CMD_PEACE_LIST_RECEIVED.getMessage());
            received.open(player);
        } else if (listSent.equals(button)) {
            PageGUI sent = new PageGUI(FMessage.CMD_PEACE_LIST_SENT.getMessage());
            sent.open(player);
        }
    }

    @Override
    public Inventory getInventory() {
        return gui;
    }
}