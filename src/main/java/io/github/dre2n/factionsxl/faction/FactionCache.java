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
package io.github.dre2n.factionsxl.faction;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.relation.Relation;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
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

    FactionsXL plugin = FactionsXL.getInstance();

    private Set<LegalEntity> entities = new HashSet<>();
    private Set<Faction> factions = new HashSet<>();
    private Set<Federation> federations = new HashSet<>();
    private Set<TradeLeague> leagues = new HashSet<>();

    public FactionCache(File factionsDir, File federationsDir, File leaguesDir) {
        for (File file : factionsDir.listFiles()) {
            Faction faction = new Faction(file);
            entities.add(faction);
            factions.add(faction);
        }

        for (File file : federationsDir.listFiles()) {
            Federation federation = new Federation(file);
            entities.add(federation);
            federations.add(federation);
        }

        for (File file : leaguesDir.listFiles()) {
            TradeLeague league = new TradeLeague(file);
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

        Board board = plugin.getBoard();

        Faction faction = new Faction(id);
        faction.creationDate = System.currentTimeMillis();
        faction.active = true;
        faction.name = name;
        faction.admin = player;
        faction.type = GovernmentType.MONARCHY;
        faction.stability = 10;
        faction.setHome(home);
        faction.capital = board.getByLocation(faction.home);
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
        Faction union = create(faction1.admin, faction1.home, faction1.name + "-" + faction2.name, false);
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
        union.mods = new HashSet<>(faction1.mods);
        union.mods.addAll(faction2.mods);
        union.members = new HashSet<>(faction1.members);
        union.members.addAll(faction2.members);
        union.invited = new HashSet<>(faction1.invited);
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
        return null;
    }

    /**
     * @param name
     * the name to check
     * @return
     * the faction that has this name
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
     * @param member
     * the member to check
     * @return
     * the faction that has this member
     */
    public Faction getByMember(OfflinePlayer member) {
        for (Faction faction : getActive()) {
            for (OfflinePlayer player : faction.getMembers()) {
                if (player.getUniqueId().equals(member.getUniqueId())) {
                    return faction;
                }
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
            for (OfflinePlayer player : faction.getMembers()) {
                if (player.getUniqueId().equals(fPlayer.getUniqueId())) {
                    return faction;
                }
            }
        }
        return null;
    }

    /**
     * @param chunk
     * the chunk to check
     * @return
     * the faction that owns this chunk
     */
    public Faction getByChunk(Chunk chunk) {
        for (Faction faction : factions) {
            if (faction.getChunks().contains(chunk)) {
                return faction;
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
        HashSet<Faction> toReturn = new HashSet<>();
        for (Faction faction : factions) {
            if (faction.isActive()) {
                toReturn.add(faction);
            }
        }
        return toReturn;
    }

    /**
     * @return
     * all factions
     */
    public Set<Faction> getAll() {
        return factions;
    }

    /**
     * @param entity
     * an instance of LegelEntity to add
     */
    public void addEntity(LegalEntity entity) {
        entities.add(entity);
        if (entity instanceof Faction) {
            factions.add((Faction) entity);
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
        return entities.size();
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
        for (Faction faction : factions) {
            faction.load();
        }
    }

}
