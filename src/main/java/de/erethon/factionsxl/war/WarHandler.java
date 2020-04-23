package de.erethon.factionsxl.war;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;
import de.erethon.factionsxl.util.ParsingUtil;

/**
 * @author Malfrador
 */

public class WarHandler {
    FactionsXL plugin = FactionsXL.getInstance();
    FConfig config = plugin.getFConfig();
    public void calculateWar() {
        long now = System.currentTimeMillis();
        for (War w : plugin.getWarCache().getWars()) {
            if ((w.getTruce()) && (now > w.getStartDate().getTime() + config.getTruceTime()) ) {
                w.setTruce(false);
                Faction attacker = (Faction) w.getAttacker().getLeader();
                Faction defender = (Faction) w.getDefender().getLeader();
                ParsingUtil.broadcastMessage(FMessage.WAR_TRUCE_ENDED.getMessage(), attacker, defender);
            }
        }
    }
    public void calculateWarStatus() {
        for (War w : plugin.getWarCache().getWars()) {
            for (Faction f : w.getAttacker().getFactions()) {
                if (w.getAttacker().getPoints() < 0) {
                    f.setExhaustion(f.getExhaustion() + config.getExhaustionLoosing());
                    return;
                }
                f.setExhaustion(f.getExhaustion() + config.getExhaustion());
            }
            for (Faction f : w.getDefender().getFactions()) {
                if (w.getDefender().getPoints() < 0) {
                    f.setExhaustion(f.getExhaustion() + config.getExhaustionLoosing());
                    return;
                }
                f.setExhaustion(f.getExhaustion() + config.getExhaustion());
            }
        }
        for (Faction f : plugin.getFactionCache().getActive()) {
            if (!f.isInWar() && f.getExhaustion() >= 0) {
                f.setExhaustion(f.getExhaustion() - config.getExhaustion());
            }
        }
    }
}
