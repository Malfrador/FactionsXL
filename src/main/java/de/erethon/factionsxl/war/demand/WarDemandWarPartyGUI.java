/*
 * Copyright (C) 2017-2018 Daniel Saukel
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
package de.erethon.factionsxl.war.demand;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.gui.WarPartyGUI;
import de.erethon.factionsxl.player.FPlayerCache;
import de.erethon.factionsxl.war.War;
import de.erethon.factionsxl.war.WarCache;
import de.erethon.factionsxl.war.WarParty;
import de.erethon.factionsxl.war.peaceoffer.FinalPeaceOffer;
import de.erethon.factionsxl.war.peaceoffer.SeparatePeaceOffer;
import java.util.Collection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class WarDemandWarPartyGUI extends WarPartyGUI {

    private FPlayerCache fPlayers;
    private WarCache wars;

    private Collection<Faction> ownFactions;

    public WarDemandWarPartyGUI(FactionsXL plugin, Collection<Faction> ownFactions, WarParty... parties) {
        super(plugin, FMessage.FACTION_SELECT.getMessage(), parties);
        fPlayers = plugin.getFPlayerCache();
        wars = plugin.getWarCache();
        this.ownFactions = ownFactions;
    }

    @Override
    public void onButtonClick(Player whoClicked, Faction buttonFaction) {
        WarParty offerTarget = null;
        for (WarParty party : buttonFaction.getWarParties()) {
            if (party.getLeader() == buttonFaction) {
                offerTarget = party;
            }
        }

        if (offerTarget != null) {
            War war = offerTarget.getWar();
            WarParty demanding = war.getEnemy(offerTarget);
            fPlayers.getByPlayer(whoClicked).setPeaceOffer(new FinalPeaceOffer(war, demanding, offerTarget));
        } else {
            War war = wars.getUnsafe(buttonFaction);
            fPlayers.getByPlayer(whoClicked).setPeaceOffer(new SeparatePeaceOffer(war, ownFactions.toArray(new Faction[]{})[0], buttonFaction));
        }
        wars.getWarDemandCreationMenu().open(whoClicked);
    }

}
