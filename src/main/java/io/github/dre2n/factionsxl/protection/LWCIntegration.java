/*
 * Copyright (C) 2016-2017 Daniel Saukel
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
package io.github.dre2n.factionsxl.protection;

import com.griefcraft.lwc.LWC;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCProtectionDestroyEvent;
import com.griefcraft.scripting.event.LWCProtectionInteractEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Adapted from FactionsLWCBridge by Sataniel.
 *
 * @author Daniel Saukel
 */
public class LWCIntegration extends JavaModule {

    FactionsXL plugin;
    LWC lwc;

    public LWCIntegration(FactionsXL plugin) {
        this.plugin = plugin;
        lwc = plugin.getLWC();
        lwc.getModuleLoader().registerModule(plugin, this);
    }

    @Override
    public void onProtectionInteract(LWCProtectionInteractEvent event) {
        if (canBypass(event.getPlayer(), event.getProtection().getBlock())) {
            event.setResult(ALLOW);
        }
    }

    @Override
    public void onDestroyProtection(LWCProtectionDestroyEvent event) {
        if (canBypass(event.getPlayer(), event.getProtection().getBlock())) {
            event.getProtection().remove();
        }
    }

    @Override
    public void onRegisterProtection(LWCProtectionRegisterEvent event) {
        FPlayer fPlayer = plugin.getFPlayerCache().getByPlayer(event.getPlayer());
        Region region = plugin.getBoard().getByLocation(event.getBlock().getLocation());
        if (region == null || region.isNeutral()) {
            return;
        }
        Faction faction = region.getOwner();
        if (!faction.getRelation(fPlayer).canBuild()) {
            event.setCancelled(true);
            ParsingUtil.sendMessage(event.getPlayer(), FMessage.PROTECTION_CANNOT_REGISTER_FACTION.getMessage(), faction);
        }
    }

    public boolean canBypass(Player player, Block block) {
        Region region = plugin.getBoard().getByLocation(block.getLocation());
        if (region == null || region.isNeutral()) {
            return false;
        }
        Faction faction = region.getOwner();
        return faction.isAdmin(player);
    }

}
