/*
 * Copyright (c) 2017-2018 Daniel Saukel
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

import io.github.dre2n.commons.player.PlayerUtil;
import io.github.dre2n.commons.player.PlayerWrapper;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.chat.ChatChannel;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.relation.RelationParticipator;
import io.github.dre2n.factionsxl.scoreboard.FScoreboard;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.io.File;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a player.
 *
 * @author Daniel Saukel
 */
public class FPlayer implements RelationParticipator, PlayerWrapper {

    FactionsXL plugin;

    private Player player;
    private UUID uuid;

    private ChatChannel chatChannel = ChatChannel.PUBLIC;
    private Region autoclaiming;
    private Region lastRegion;

    private FPlayerData data;

    FPlayer(FactionsXL plugin, Player player) {
        this.plugin = plugin;

        this.player = player;
        uuid = player.getUniqueId();
        loadPlayerData(FPlayerCache.getFile(uuid));
        data.setLastName(player.getName());
    }

    FPlayer(FactionsXL plugin, UUID uuid) {
        this.plugin = plugin;

        this.uuid = uuid;
        loadPlayerData(FPlayerCache.getFile(uuid));
    }

    FPlayer(FactionsXL plugin, String name) {
        this(plugin, PlayerUtil.getUniqueIdFromName(name));
    }

    /* Getters and setters */
    @Override
    public String getName() {
        return data.getLastName();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * @return
     * the player
     */
    public boolean isOnline() {
        return player != null;
    }

    /**
     * @return
     * if the player is faction mod in any faction
     */
    public boolean isMod() {
        for (Faction faction : plugin.getFactionCache().getActive()) {
            if (isMod(faction)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param the faction to check
     * @return
     * if the player is faction mod
     */
    public boolean isMod(Faction faction) {
        if (faction.getMods().contains(this)) {
            return true;
        }
        if (faction.isAdmin(uuid)) {
            return true;
        }
        return false;
    }

    /**
     * @return
     * the player's dynasty
     */
    public Dynasty getDynasty() {
        return plugin.getFPlayerCache().getDynastyByUUID(uuid);
    }

    /**
     * @return
     * the chat channel that this player is using
     */
    public ChatChannel getChatChannel() {
        return chatChannel;
    }

    /**
     * @param channel
     * the chat channel to set
     */
    public void setChatChannel(ChatChannel channel) {
        chatChannel = channel;
        ParsingUtil.sendMessage(player, FMessage.CHAT_CHANNEL_SWITCHED.getMessage(), channel.getFormatted());
    }

    /**
     * @return
     * true if the scoreboard is enabled
     */
    public boolean isScoreboardEnabled() {
        return data.isScoreboardEnabled();
    }

    /**
     * @param enabled
     * if the scoreboard is enabled
     */
    public void setScoreboardEnabled(boolean enabled) {
        data.setScoreboardEnabled(enabled);
    }

    /**
     * @return
     * if the scoreboard is enabled
     */
    public boolean areAnthemsEnabled() {
        return data.areAnthemsEnabled();
    }

    /**
     * @param enabled
     * if faction anthems shall be enabled
     */
    public void setAnthemsEnabled(boolean enabled) {
        data.setAnthemsEnabled(enabled);
    }

    /**
     * @return
     * the region to which the player adds chunks automatically
     */
    public Region getAutoclaimingRegion() {
        return autoclaiming;
    }

    /**
     * @return
     * if the player is currently autoclaiming
     */
    public boolean isAutoclaiming() {
        return autoclaiming != null;
    }

    /**
     * @param region
     * the region to claim automatically
     */
    public void setAutoclaiming(Region region) {
        autoclaiming = region;
    }

    /**
     * @return
     * the player's power value
     */
    public int getPower() {
        Double raw = plugin.getFData().power.get(uuid);
        return raw != null ? (int) Math.round(raw) : 0;
    }

    /**
     * @param power
     * the power value to set
     */
    public void setPower(double power) {
        plugin.getFData().power.put(uuid, power);
    }

    /**
     * @return
     * the player's title
     */
    public String getTitle() {
        return data.getTitle();
    }

    /**
     * @param title
     * the title to set
     */
    public void setTitle(String title) {
        data.setTitle(title);
    }

    /**
     * @return
     * the player's prefix as specified in the language file
     */
    public String getPrefix() {
        Faction faction = getFaction();
        if (faction != null && faction.isAdmin(uuid)) {
            return FMessage.CHAT_PREFIX_ADMIN.getMessage();
        } else if (faction != null && isMod(faction)) {
            return FMessage.CHAT_PREFIX_MOD.getMessage();
        } else {
            return FMessage.CHAT_PREFIX_MEMBER.getMessage();
        }
    }

    @Override
    public Relation getRelation(RelationParticipator object) {
        Faction own = getFaction();
        return own != null ? own.getRelation(object) : Relation.PEACE;
    }

    @Override
    public boolean isInWar(RelationParticipator object) {
        Faction own = getFaction();
        return own != null ? own.isInWar(object) : false;
    }

    public Location getHome() {
        return data.getHome();
    }

    public void setHome(Location location) {
        data.setHome(location);
    }

    public boolean checkHome() {
        return checkHome(getHome());
    }

    public boolean checkHome(Location home) {
        if (home == null) {
            return true;
        }
        Region region = plugin.getBoard().getByLocation(home);
        if (region == null || region.getOwner() == null) {
            return true;
        }
        if (!region.getOwner().getRelation(this).canBuild()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return
     * the saved data
     */
    public FPlayerData getData() {
        return data;
    }

    /**
     * Load / reload a new instance of PlayerData
     */
    public void loadPlayerData(File file) {
        data = new FPlayerData(plugin, file);
    }

    /**
     * @return
     * the player's faction
     */
    public Faction getFaction() {
        return plugin.getFactionCache().getByFPlayer(this);
    }

    /**
     * @return
     * if the player is member of a faction.
     */
    public boolean hasFaction() {
        return plugin.getFactionCache().getByFPlayer(this) != null;
    }

    /**
     * @return
     * the last region the player was known to be inside
     */
    public Region getLastRegion() {
        if (player == null) {
            return null;
        }
        if (lastRegion == null) {
            lastRegion = plugin.getBoard().getByLocation(player.getLocation());
        }
        return lastRegion;
    }

    /**
     * @param region
     * the region to cache
     */
    public void setLastRegion(Region region) {
        lastRegion = region;
    }

    @Override
    public String toString() {
        return "FPlayer{name=" + getName() + "}";
    }

}
