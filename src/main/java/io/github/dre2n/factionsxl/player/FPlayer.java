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
package io.github.dre2n.factionsxl.player;

import io.github.dre2n.commons.misc.PlayerUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.chat.ChatChannel;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.faction.Faction;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.relation.RelationParticipator;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.io.File;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Represents a player.
 *
 * @author Daniel Saukel
 */
public class FPlayer implements RelationParticipator {

    FactionsXL plugin = FactionsXL.getInstance();

    private Player player;
    private UUID uuid;

    private ChatChannel chatChannel = ChatChannel.PUBLIC;
    private boolean scoreboardEnabled = plugin.getFConfig().isScoreboardEnabledByDefault();
    private Region autoclaiming;

    private FPlayerData data;

    public FPlayer(Player player) {
        this.player = player;
        uuid = player.getUniqueId();
        loadPlayerData(FPlayerCache.getFile(uuid));
        data.setLastName(player.getName());
    }

    public FPlayer(UUID uuid) {
        this.uuid = uuid;
        loadPlayerData(FPlayerCache.getFile(uuid));
    }

    public FPlayer(String name) {
        this(PlayerUtil.getUniqueIdFromName(name));
    }

    /* Getters and setters */
    /**
     * @return
     * the player's name
     */
    public String getName() {
        return data.getLastName();
    }

    /**
     * @return uuid
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * @return
     * the Bukkit player
     */
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
        for (OfflinePlayer player : faction.getMods()) {
            if (player.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        if (faction.getAdmin().getUniqueId().equals(uuid)) {
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
        return scoreboardEnabled;
    }

    /**
     * @param enabled
     * if the scoreboard is enabled
     */
    public void setScoreboardEnabled(boolean enabled) {
        scoreboardEnabled = enabled;
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
        if (faction != null && faction.getAdmin().getUniqueId().equals(uuid)) {
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
        data = new FPlayerData(file);
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

}
