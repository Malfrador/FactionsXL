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
package io.github.dre2n.factionsxl.war;

import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.util.PageGUI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class CallToArmsMenu implements Listener {

    enum Status {
        ATTACKER,
        ATTACKER_CANDIDATE,
        DEFENDER
    }

    private WarParty attacker;
    private Faction attackerLeader;
    private Set<Faction> attackerCandidates = new HashSet<>();
    private WarParty defender;
    private CasusBelli cb;
    private PageGUI gui;

    public CallToArmsMenu(WarParty attacker, Faction defender, CasusBelli cb) {
        this.attacker = attacker;
        attackerLeader = (Faction) attacker.getLeader(); // TODO: Might break after government update
        attackerLeader.getRelatedFactions(Relation.ALLIANCE).forEach(f -> attackerCandidates.add(f));
        attackerLeader.getRelatedFactions(Relation.COALITION).forEach(f -> attackerCandidates.add(f));
        Faction mainTarget = defender.isVassal() ? defender.getLord() : defender;
        this.defender = new WarParty(mainTarget);
        for (Entry<Faction, Relation> entry : mainTarget.getRelations().entrySet()) {
            Relation relation = entry.getValue();
            if (relation == Relation.ALLIANCE || relation == Relation.PERSONAL_UNION || relation == Relation.VASSAL) {
                this.defender.addParticipant(entry.getKey());
            }
        }
        gui = new PageGUI(FMessage.WAR_CALL_TO_ARMS_TITLE.getMessage(), 2);
        this.attacker.getFactions().forEach(f -> gui.addButton1(generateFactionButton(f, Status.ATTACKER))); // Call to arms
        for (Faction faction : attackerCandidates) {// Allies and coalition partners
            if (!this.attacker.getFactions().contains(faction)) {
                gui.addButton2(generateFactionButton(faction, Status.ATTACKER_CANDIDATE));
            }
        }
        this.defender.getFactions().forEach(f -> gui.addButton3(generateFactionButton(f, Status.DEFENDER))); // Enemies
    }

    public void open(HumanEntity player) {
        gui.open(player, 0, 0, 0);
    }

    private ItemStack generateFactionButton(Faction faction, Status status) {
        ItemStack button = faction.getBannerStack();
        ItemMeta meta = button.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.setDisplayName(attackerLeader.getRelation(faction).getColor() + faction.getName());
        List<String> lore = new ArrayList<>();
        if (status != Status.DEFENDER) {
            switch (attackerLeader.getRelation(faction)) {
                case ALLIANCE:
                    lore.add(FMessage.WAR_CALL_TO_ARMS_ALLY_1.getMessage(faction.getName()));
                    if (defender.getParticipants().contains(faction)) {
                        lore.add(FMessage.WAR_CALL_TO_ARMS_ALLY_2_ALLIED.getMessage());
                        lore.add(FMessage.WAR_CALL_TO_ARMS_ALLY_3_ALLIED.getMessage());
                    } else {
                        lore.add(FMessage.WAR_CALL_TO_ARMS_ALLY_2_NOT_ALLIED.getMessage());
                        lore.add(FMessage.WAR_CALL_TO_ARMS_ALLY_3_NOT_ALLIED.getMessage());
                        lore.add((status == Status.ATTACKER_CANDIDATE ? FMessage.WAR_CALL_TO_ARMS_ADD : FMessage.WAR_CALL_TO_ARMS_REMOVE).getMessage());
                    }
                    break;
                case PERSONAL_UNION:
                case OWN:
                    lore.add(FMessage.WAR_CALL_TO_ARMS_OWN.getMessage(faction.getName()));
                    break;
                case VASSAL:
                    lore.add(FMessage.WAR_CALL_TO_ARMS_VASSAL_1.getMessage(faction.getName()));
                    // TODO if (vassal is stronger than lord) lore.add(FMessage.WAR_CALL_TO_ARMS_VASSAL_2_WEAKER.getMessage()
                    lore.add(FMessage.WAR_CALL_TO_ARMS_VASSAL_2_STRONGER.getMessage());
                    lore.add((status == Status.ATTACKER_CANDIDATE ? FMessage.WAR_CALL_TO_ARMS_ADD : FMessage.WAR_CALL_TO_ARMS_REMOVE).getMessage());
                    break;
                case COALITION:
                    lore.add(FMessage.WAR_CALL_TO_ARMS_COALITION_1.getMessage(faction.getName()));
                    lore.add(FMessage.WAR_CALL_TO_ARMS_COALITION_2.getMessage());
                    lore.add((status == Status.ATTACKER_CANDIDATE ? FMessage.WAR_CALL_TO_ARMS_ADD : FMessage.WAR_CALL_TO_ARMS_REMOVE).getMessage());
                    break;
            }
        } else {
            lore.add(FMessage.WAR_CALL_TO_ARMS_DEFENDER.getMessage(faction.getName()));
        }
        meta.setLore(lore);
        button.setItemMeta(meta);
        return button;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        int slot = event.getSlot();
        if (inventory == null || !gui.getTitle().equals(inventory.getTitle()) || (slot > 8 & slot < 18 || slot > 26 & slot < 36 || slot >= 45)) {
            return;
        }
        ItemStack button = event.getCurrentItem();
        if (button == null) {
            return;
        }
        assert button.getItemMeta().hasLore();
        if (button.getItemMeta().getLore().contains(FMessage.WAR_CALL_TO_ARMS_ADD.getMessage())) {
            gui.removeButton2(button);
            gui.addButton1(button);
            gui.open(event.getWhoClicked(), 0, 0, 0);
        } else if (button.getItemMeta().getLore().contains(FMessage.WAR_CALL_TO_ARMS_REMOVE.getMessage())) {
            gui.removeButton1(button);
            gui.addButton2(button);
            gui.open(event.getWhoClicked(), 0, 0, 0);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!gui.equals(event.getInventory())) {
            return;
        }
        War war = new War(attacker, defender, cb);
    }

}
