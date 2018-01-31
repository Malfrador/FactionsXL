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
package io.github.dre2n.factionsxl.faction;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.player.PlayerCollection;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.util.LazyChunk;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Facton instance manager.
 *
 * @author Daniel Saukel
 */
public class FactionCache {

    FactionsXL plugin;

    private Set<LegalEntity> entities = new HashSet<>();
    private Set<Faction> factions = new HashSet<>();
    private Set<Faction> inactiveFactions = new HashSet<>();
    private Set<Federation> federations = new HashSet<>();
    private Set<TradeLeague> leagues = new HashSet<>();

    public FactionCache(FactionsXL plugin, File factionsDir, File federationsDir, File leaguesDir) {
        this.plugin = plugin;
        for (File file : factionsDir.listFiles()) {
            Faction faction = new Faction(plugin, file);
            entities.add(faction);
            if (faction.isActive()) {
                factions.add(faction);
            } else {
                inactiveFactions.add(faction);
            }
        }

        for (File file : federationsDir.listFiles()) {
            Federation federation = new Federation(plugin, file);
            entities.add(federation);
            federations.add(federation);
        }

        for (File file : leaguesDir.listFiles()) {
            TradeLeague league = new TradeLeague(plugin, file);
            entities.add(league);
            leagues.add(league);
        }
    }

    /**
     * @param player
     * the creator of the faction
     * @param name
     * the name of the faction
     * @param checkPersonalUnions
     */
    public Faction create(OfflinePlayer player, Location home, String name, boolean checkPersonalUnions) {
        int id = generateId();
        File file = new File(FactionsXL.FACTIONS, id + ".yml");

        try {
            file.createNewFile();
            MessageUtil.log(plugin, FMessage.LOG_NEW_FACTION_DATA.getMessage(file.getName()));
        } catch (IOException exception) {
        }

        Faction faction = new Faction(plugin, id);
        faction.creationDate = System.currentTimeMillis();
        faction.active = true;
        faction.name = name;
        faction.setAdmin(player);
        faction.type = GovernmentType.MONARCHY;
        faction.stability = 10;
        faction.setHome(home);
        faction.capital = plugin.getBoard().getByLocation(faction.home);
        faction.capital.setOwner(faction);
        faction.capital.getCoreFactions().put(faction, Calendar.getInstance().getTime());
        faction.members.add(player);
        faction.manpowerModifier = plugin.getFConfig().getDefaultManpowerModifier();
        faction.save();
        faction.load();
        entities.add(faction);
        factions.add(faction);
        if (checkPersonalUnions) {
            faction.checkForPersonalUnions();
        }
        return faction;
    }

    /**
     * @param player
     * the creator of the faction
     * @param name
     * the name of the faction
     */
    public Faction create(OfflinePlayer player, Location home, String name) {
        return create(player, home, name, true);
    }

    /**
     * @param player
     * the creator of the faction
     * @param name
     * the name of the faction
     */
    public Faction create(Player player, String name) {
        return create(player, player.getLocation(), name);
    }

    /**
     * Merges two factions into a new one.
     *
     * @param faction1
     * the first faction
     * @param faction2
     * the second faction
     * @return
     * the real union
     */
    public Faction formRealUnion(Faction faction1, Faction faction2) {
        Faction union = create(Bukkit.getOfflinePlayer(faction1.admin), faction1.home, faction1.name + "-" + faction2.name, false);
        faction1.setActive(false);
        faction2.setActive(false);

        union.desc = faction1.desc;
        union.anthem = faction1.anthem;
        union.banner = faction1.banner;
        union.bannerColor = faction2.bannerColor;
        if (plugin.getFConfig().isEconomyEnabled()) {
            union.account.setBalance(faction1.account.getBalance() + faction2.account.getBalance());
            faction1.account.setBalance(0);
            faction2.account.setBalance(0);
        }
        union.mapFillColor = faction1.mapFillColor;
        union.mapLineColor = faction2.mapLineColor;
        union.type = faction1.type;
        union.open = faction1.open;
        union.prestige = faction1.prestige + faction2.prestige;
        union.stability = (byte) ((faction1.stability + faction2.stability) / 2);
        union.exhaustion = faction1.exhaustion + faction2.exhaustion;
        union.manpowerModifier = faction1.manpowerModifier + faction2.manpowerModifier; // Double ideas?
        union.capital = faction1.capital;
        union.chunks = new HashSet<>(faction1.chunks);
        union.chunks.addAll(faction2.chunks);
        union.regions = new HashSet<>(faction1.regions);
        union.regions.addAll(faction2.regions);
        for (Region region : union.regions) {
            region.setOwner(union);
            Date coreDate = null;
            for (Entry<Faction, Date> entry : region.getCoreFactions().entrySet()) {
                if (entry.getKey() == faction1 || entry.getKey() == faction2) {
                    coreDate = entry.getValue();
                }
            }
            if (coreDate != null) {
                region.getCoreFactions().put(union, coreDate);
            }
        }
        union.mods = new PlayerCollection(faction1.mods.getUniqueIds());
        union.mods.addAll(faction2.mods);
        union.members = new PlayerCollection(faction1.members.getUniqueIds());
        union.members.addAll(faction2.members);
        union.invited = new PlayerCollection(faction1.invited.getUniqueIds());
        union.invited.addAll(faction2.invited);
        for (Entry<Faction, Relation> entry : faction1.relations.entrySet()) {
            if (entry.getValue() == Relation.VASSAL) {
                union.relations.put(entry.getKey(), entry.getValue());
                entry.getKey().relations.remove(faction1);
                entry.getKey().relations.put(union, Relation.LORD);
            }
        }
        for (Entry<Faction, Relation> entry : faction2.relations.entrySet()) {
            if (entry.getValue() == Relation.VASSAL) {
                union.relations.put(entry.getKey(), entry.getValue());
                entry.getKey().relations.remove(faction2);
                entry.getKey().relations.put(union, Relation.LORD);
            }
        }
        // Ideas? Trade?

        faction1.disband(false);
        faction2.disband(false);

        union.checkForPersonalUnions();
        return union;
    }

    /**
     * Integrate one faction into the other.
     *
     * @param integrating
     * the first faction
     * @param integrated
     * the faction that gets integrated
     */
    public void integrate(Faction integrating, Faction integrated) {
        for (Region region : integrated.getRegions()) {
            Date coreDate = null;
            for (Entry<Faction, Date> entry : region.getCoreFactions().entrySet()) {
                if (entry.getKey() == integrating || entry.getKey() == integrated) {
                    coreDate = entry.getValue();
                }
            }
            if (coreDate != null) {
                region.getCoreFactions().put(integrating, coreDate);
            }
            region.setOwner(integrating);
        }
        for (UUID uuid : integrated.members.getUniqueIds()) {
            integrating.members.add(uuid);
        }
        if (plugin.getFConfig().isEconomyEnabled()) {
            integrating.account.deposit(integrated.account.getBalance());
            integrated.account.setBalance(0);
        }
        integrated.relations.clear();
        integrated.disband();
        ParsingUtil.broadcastMessage(FMessage.FACTION_INTEGRATED_VASSAL.getMessage(), integrating, integrated);
    }

    /* Getters and setters */
    /**
     * @param id
     * the ID to check
     * @return
     * the faction that has this ID
     */
    public Faction getById(int id) {
        for (Faction faction : factions) {
            if (faction.getId() == id) {
                return faction;
            }
        }
        for (Faction faction : inactiveFactions) {
            if (faction.getId() == id) {
                return faction;
            }
        }
        return null;
    }

    /**
     * @param name
     * the name to check
     * @return
     * the active faction that has this name
     */
    public Faction getByName(String name) {
        for (Faction faction : factions) {
            if (faction.getName().equalsIgnoreCase(name) || faction.getShortName().equalsIgnoreCase(name) || faction.getLongName().equalsIgnoreCase(name)) {
                return faction;
            }
        }
        return null;
    }

    /**
     * @param name
     * the name to check
     * @return
     * the inactive faction that has this name
     */
    public Faction getInactiveByName(String name) {
        for (Faction faction : inactiveFactions) {
            if (faction.getName().equalsIgnoreCase(name) || faction.getShortName().equalsIgnoreCase(name) || faction.getLongName().equalsIgnoreCase(name)) {
                return faction;
            }
        }
        return null;
    }

    /**
     * @param member
     * the member to check
     * @return
     * the faction that has this member
     */
    public Faction getByMember(OfflinePlayer member) {
        for (Faction faction : getActive()) {
            if (faction.getMembers().contains(member)) {
                return faction;
            }
        }
        return null;
    }

    /**
     * @param member
     * the UUID of the member to check
     * @return
     * the faction that has this member
     */
    public Faction getByMember(UUID member) {
        for (Faction faction : getActive()) {
            if (faction.getMembers().contains(member)) {
                return faction;
            }
        }
        return null;
    }

    /**
     * @param fPlayer
     * the FPlayer to check
     * @return
     * the faction that has this member
     */
    public Faction getByFPlayer(FPlayer fPlayer) {
        for (Faction faction : getActive()) {
            for (UUID uuid : faction.getMembers().getUniqueIds()) {
                if (uuid.equals(fPlayer.getUniqueId())) {
                    return faction;
                }
            }
        }
        return null;
    }

    /**
     * @param leader
     * the leader to check
     * @return
     * a Set of all factions led by this player
     */
    public Set<Faction> getByLeader(Player leader) {
        UUID uuid = leader.getUniqueId();
        Set<Faction> factions = new HashSet<>();
        for (Faction faction : getActive()) {
            if (uuid.equals(faction.admin)) {
                factions.add(faction);
            }
        }
        return factions;
    }

    /**
     * @param chunk
     * the chunk to check
     * @return
     * the faction that owns this chunk
     */
    public Faction getByChunk(Chunk chunk) {
        for (Faction faction : factions) {
            for (LazyChunk fChunk : faction.getChunks()) {
                if (chunk.getX() == fChunk.getX() && chunk.getZ() == fChunk.getZ()) {
                    return faction;
                }
            }
        }
        return null;
    }

    /**
     * @param location
     * the location to check
     * @return
     * the faction that owns this location
     */
    public Faction getByLocation(Location location) {
        return getByChunk(location.getChunk());
    }

    /**
     * @return
     * all active factions
     */
    public Set<Faction> getActive() {
        return factions;
    }

    /**
     * @return
     * all inactive factions
     */
    public Set<Faction> getInactive() {
        return inactiveFactions;
    }

    /**
     * @return
     * all factions
     */
    public Set<Faction> getAll() {
        HashSet<Faction> toReturn = new HashSet<>();
        toReturn.addAll(factions);
        toReturn.addAll(inactiveFactions);
        return toReturn;
    }

    /**
     * @param entity
     * an instance of LegelEntity to add
     */
    public void addEntity(LegalEntity entity) {
        entities.add(entity);
        if (entity instanceof Faction) {
            if (((Faction) entity).isActive()) {
                factions.add((Faction) entity);
            } else {
                inactiveFactions.add((Faction) entity);
            }
        } else if (entity instanceof Federation) {
            federations.add((Federation) entity);
        } else if (entity instanceof TradeLeague) {
            leagues.add((TradeLeague) entity);
        }
    }

    /**
     * @param entity
     * an instance of LegalEntity to remove
     */
    public void removeEntity(LegalEntity entity) {
        entities.remove(entity);
        if (entity instanceof Faction) {
            factions.remove((Faction) entity);
            inactiveFactions.remove((Faction) entity);
        } else if (entity instanceof Federation) {
            federations.remove((Federation) entity);
        } else if (entity instanceof TradeLeague) {
            leagues.remove((TradeLeague) entity);
        }
    }

    /**
     * @return
     * a new, unused entity ID.
     */
    public int generateId() {
        int highest = 0;
        for (LegalEntity entity : entities) {
            if (entity.getId() > highest) {
                highest = entity.getId();
            }
        }
        return highest + 1;
    }

    /* Persistence */
    /**
     * Saves all factions
     */
    public void saveAll() {
        for (Faction faction : factions) {
            faction.save();
        }
    }

    /**
     * Loads the persistent data of all factions
     */
    public void loadAll() {
        factions.forEach(f -> f.load());
        inactiveFactions.forEach(f -> f.load());
    }

}
