package de.erethon.factionsxl.util;

import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.board.Region;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.config.FMessage;
import de.erethon.factionsxl.faction.Faction;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class CoringHandler {

    FactionsXL plugin = FactionsXL.getInstance();
    FConfig config = plugin.getFConfig();

    List<Region> regions = new CopyOnWriteArrayList<>();

    public void calculateCoringProgress() {
        regions = plugin.getBoard().getRegions();
        for (Region r : regions) {
            for (Map.Entry<Faction, Integer> entry : r.getCoringProgress().entrySet() ) {
                Faction f = entry.getKey();
                if (entry.getValue() <= 99 && !(f.isInWar()) ) {
                    int progress = entry.getValue();
                    int newProgress = (int) (progress + config.getCoringPerDay());
                    entry.setValue(newProgress);
                }
                if (entry.getValue() >= 100 && !(f.isInWar()) ) {
                    r.getCoreFactions().put(f, Calendar.getInstance().getTime());
                    r.setInfluence(100);
                    ParsingUtil.broadcastMessage(FMessage.FACTION_NEW_CORE.getMessage(), f, r);
                    r.getCoringProgress().remove(f);
                }
            }
        }
    }
    public void calculateClaimTime() {
        regions = plugin.getBoard().getRegions();
        for (Region r : regions) {
            for (Map.Entry<Faction, Date> entry : r.getClaimFactions().entrySet() ) {
                Faction f = entry.getKey();
                if(System.currentTimeMillis() > entry.getValue().getTime() + config.getClaimTimeout()) {
                    r.getClaimFactions().remove(f);
                    f.sendMessage(FMessage.FACTION_LOST_CLAIM.getMessage(), f, r);
                }
            }
        }
    }
}
