package de.erethon.factionsxl.war;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.config.FConfig;
import de.erethon.factionsxl.faction.Faction;
import org.bukkit.ChatColor;


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
                MessageUtil.broadcastMessage(ChatColor.GREEN + "Der Waffenstillstand zwischen " + ChatColor.YELLOW + attacker + ChatColor.GREEN + " und " + ChatColor.YELLOW + defender + ChatColor.GREEN + "ist nun vorbei.");
            }
        }
    }
    public void calculateWarStatus() {

    }
}
