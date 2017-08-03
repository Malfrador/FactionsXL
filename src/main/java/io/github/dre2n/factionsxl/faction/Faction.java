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

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.github.dre2n.commons.config.ConfigUtil;
import io.github.dre2n.commons.misc.EnumUtil;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.commons.player.PlayerCollection;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.board.Region;
import io.github.dre2n.factionsxl.board.dynmap.DynmapStyle;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.economy.EconomyMenu;
import io.github.dre2n.factionsxl.economy.FAccount;
import io.github.dre2n.factionsxl.economy.FStorage;
import io.github.dre2n.factionsxl.economy.Resource;
import io.github.dre2n.factionsxl.economy.ResourceSubcategory;
import io.github.dre2n.factionsxl.economy.TradeMenu;
import io.github.dre2n.factionsxl.idea.Idea;
import io.github.dre2n.factionsxl.idea.IdeaGroup;
import io.github.dre2n.factionsxl.idea.IdeaMenu;
import io.github.dre2n.factionsxl.player.Dynasty;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayer;
import io.github.dre2n.factionsxl.population.PopulationMenu;
import io.github.dre2n.factionsxl.population.SaturationLevel;
import io.github.dre2n.factionsxl.relation.Relation;
import io.github.dre2n.factionsxl.relation.RelationParticipator;
import io.github.dre2n.factionsxl.scoreboard.FTeamWrapper;
import io.github.dre2n.factionsxl.util.LazyChunk;
import io.github.dre2n.factionsxl.util.ParsingUtil;
import io.github.dre2n.factionsxl.war.CasusBelli;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Represents a faction.
 *
 * @author Daniel Saukel
 */
public class Faction extends LegalEntity implements RelationParticipator {

    FactionsXL plugin = FactionsXL.getInstance();
    FConfig fConfig = plugin.getFConfig();

    File file;
    FileConfiguration config;
    boolean active;
    String mapFillColor = "#E0E0E0";
    String mapLineColor = "#FFFFFF";
    DynmapStyle dynmapStyle;
    boolean mapVisibility = true;
    GovernmentType type;
    boolean open;
    double prestige;
    int stability;
    double exhaustion;
    double manpowerModifier;
    Location home;
    Hologram homeHolo;
    Region capital;
    Set<LazyChunk> chunks = new HashSet<>();
    Set<Region> regions = new HashSet<>();
    PlayerCollection mods = new PlayerCollection();
    PlayerCollection members = new PlayerCollection();
    PlayerCollection invited = new PlayerCollection();
    Map<Faction, Relation> relations = new HashMap<>();
    EconomyMenu economyMenu;
    TradeMenu tradeMenu;
    FStorage storage;
    Map<Resource, Integer> groceryList = new HashMap<>();
    Map<Resource, Integer> consumableResources = new HashMap<>();
    Map<Resource, Integer> saturatedResources = new HashMap<>();
    Map<ResourceSubcategory, Integer> saturatedSubcategories = new HashMap<>();
    PopulationMenu populationMenu;
    IdeaMenu ideaMenu;
    Set<IdeaGroup> ideaGroups = new HashSet<>();
    Set<Idea> ideas = new HashSet<>();
    Set<CasusBelli> casusBelli = new HashSet<>();

    public Faction(File file) {
        id = NumberUtil.parseInt(file.getName().replace(".yml", ""));
        this.file = file;
        config = YamlConfiguration.loadConfiguration(file);
    }

    public Faction(int id) {
        this.id = id;
        file = new File(FactionsXL.FACTIONS, id + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    /* Getters and setters */
    @Override
    public void setName(String name) {
        super.setName(name);
        FTeamWrapper.updatePrefixes(this);
    }

    @Override
    public void setBanner(ItemStack banner) {
        super.setBanner(banner);
        updateHomeHologram();
    }

    /**
     * @return
     * if the faction is active or disbanded
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     * set if the faction is active or disbanded
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return
     * the Dynmap fill color
     */
    public String getMapFillColor() {
        return mapFillColor;
    }

    /**
     * @return
     * the Dynmap line color
     */
    public String getMapLineColor() {
        return mapLineColor;
    }

    /**
     * @param fill
     * the Dynmap fill color to set
     * @param line
     * the Dynmap line color to set
     */
    public void setMapColor(String fill, String line) {
        if (fill.matches("#[0-9A-F]{6}") && line.matches("#[0-9A-F]{6}")) {
            mapFillColor = fill;
            mapLineColor = line;
            dynmapStyle = new DynmapStyle(DynmapStyle.DEFAULT_STYLE).setStrokeColor(mapLineColor).setFillColor(mapFillColor);
        }
    }

    /**
     * @return
     * the dynmap style of the faction
     */
    public DynmapStyle getDynmapStyle() {
        if (dynmapStyle == null) {
            dynmapStyle = new DynmapStyle(DynmapStyle.DEFAULT_STYLE).setStrokeColor(mapLineColor).setFillColor(mapFillColor);
        }
        return dynmapStyle;
    }

    /**
     * @return
     * the map visibility
     */
    public boolean isMapVisible() {
        return mapVisibility;
    }

    /**
     * @param visibility
     * set the faction to visible or hidden
     */
    public void setMapVisibility(boolean visibility) {
        mapVisibility = visibility;
    }

    /**
     * @return
     * the type of the government
     */
    public GovernmentType getGovernmentType() {
        return type;
    }

    /**
     * @param type
     * the type of the government to set
     */
    public void setGovernmentType(GovernmentType type) {
        this.type = type;
    }

    /**
     * @return
     * if the faction is open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @param open
     * set if the faction is open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * @return
     * the prestige value
     */
    public double getPrestige() {
        return prestige;
    }

    /**
     * @param prestige
     * the prestige value to set
     */
    public void setPrestige(double prestige) {
        this.prestige = prestige;
    }

    /**
     * @return
     * the power of all players
     */
    public int getPower() {
        Double power = 0D;
        for (UUID member : members.getUniqueIds()) {
            Double d = plugin.getFData().power.get(member);
            if (d != null) {
                power += d;
            }
        }
        return power.intValue();
    }

    /**
     * @return
     * the stability value
     */
    public int getStability() {
        int i = (int) Math.round(stability - exhaustion * exhaustion) - (regions.size() - 1 * regions.size() - 1) / 2;
        if (!members.contains(admin)) {
            i = i - 25;
        }
        if (getPower() > chunks.size()) {
            i += 10;
        } else if (getPower() < chunks.size()) {
            i -= 10;
        }
        for (ResourceSubcategory category : ResourceSubcategory.values()) {
            i += isSubcategorySaturated(category).getStabilityBonus();
        }
        return i;
    }

    /**
     * @return
     * the stability value with all modifiers as hover texts
     */
    public BaseComponent[] getStabilityModifiers(ChatColor c) {
        String stability = FMessage.CMD_SHOW_STABILITY.getMessage() + c + getStability();
        String base = FMessage.CMD_SHOW_STABILITY_MOD_BASE.getMessage() + color(this.stability) + "\n";
        String exhaustion = ChatColor.RESET + FMessage.CMD_SHOW_STABILITY_MOD_EXHAUSTION.getMessage() + color((int) (this.exhaustion * this.exhaustion)) + "\n";
        String size = ChatColor.RESET + FMessage.CMD_SHOW_STABILITY_MOD_PROVINCES.getMessage() + color((regions.size() - 1 * regions.size() - 1) / 2) + "\n";
        String adminNotMember = ChatColor.RESET + FMessage.CMD_SHOW_STABILITY_MOD_ABSENT_MONARCH.getMessage() + color(members.contains(admin) ? 0 : -25) + "\n";
        String power = ChatColor.RESET + FMessage.CMD_SHOW_STABILITY_MOD_POWER.getMessage();
        if (getPower() > chunks.size()) {
            power += ChatColor.GREEN + "+10";
        } else if (getPower() < chunks.size()) {
            power += ChatColor.DARK_RED + "-10";
        } else {
            power += ChatColor.YELLOW + "0";
        }
        power += "\n";
        int i = 0;
        for (ResourceSubcategory category : ResourceSubcategory.values()) {
            i += isSubcategorySaturated(category).getStabilityBonus();
        }
        String wealth = ChatColor.RESET + FMessage.CMD_SHOW_STABILITY_MOD_WEALTH.getMessage() + color(i);

        BaseComponent[] msg = TextComponent.fromLegacyText(stability);
        for (BaseComponent component : msg) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(base + exhaustion + size + adminNotMember + power + wealth)));
        }
        return msg;
    }

    private String color(int i) {
        if (i > 0) {
            return ChatColor.GREEN + "+" + String.valueOf(i);
        } else if (i < 0) {
            return ChatColor.DARK_RED + String.valueOf(i);
        } else {
            return ChatColor.YELLOW + String.valueOf(i);
        }
    }

    /**
     * @param stability
     * the stability value to set
     */
    public void setStability(int stability) {
        this.stability = stability;
    }

    /**
     * @return
     * the war exhaustion value
     */
    public double getExhaustion() {
        return exhaustion;
    }

    /**
     * @param exhaustion
     * the war exhaustion value to set
     */
    public void setExhaustion(double exhaustion) {
        this.exhaustion = exhaustion;
    }

    /**
     * @return
     * the amount of people without the manpower modifier
     */
    public int getPopulation() {
        int manpower = 0;
        for (Region region : regions) {
            manpower += region.getPopulation();
        }
        return manpower;
    }

    /**
     * @return
     * the manpower value
     */
    public int getManpower() {
        int manpower = 0;
        for (Region region : regions) {
            manpower += region.getPopulation() * (manpowerModifier / 100);
        }
        return manpower;
    }

    /**
     * @param resource
     * the resource to check
     * @return
     * how much of a resource is needed to saturate a resource at 100%
     */
    public int getDemand(Resource resource) {
        return (int) (SaturationLevel.getRequiredResourceUnits(getPopulation()) * resource.getRequiredAmountModifier());
    }

    /**
     * @return
     * the overall sympathy value
     */
    public int getSympathy() {
        return 0;
    }

    /**
     * @return
     * the home location of the faction
     */
    public Location getHome() {
        return home;
    }

    /**
     * @param home
     * the location to set
     */
    public void setHome(Location home) {
        this.home = home;
        updateHomeHologram();
    }

    /**
     * Updates the home hologram
     */
    public void updateHomeHologram() {
        if (!fConfig.areHologramsEnabled() || !active) {
            return;
        }
        // Run this 1 tick later sothat everything is loaded
        final Faction faction = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (homeHolo != null) {
                    homeHolo.delete();
                }
                homeHolo = HologramsAPI.createHologram(plugin, getHome().clone().add(0, 3, 0));
                homeHolo.appendItemLine(getBannerStack());
                for (String line : fConfig.getHomeHologramText()) {
                    homeHolo.appendTextLine(ParsingUtil.replaceFactionPlaceholders(line, faction));
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    /**
     * @return
     * the capital region
     */
    public Region getCapital() {
        return capital;
    }

    /**
     * @param capital
     * the capital region to set
     */
    public void setCapital(Region capital) {
        this.capital = capital;
    }

    /**
     * @return
     * a Set of all chunks owned by this faction
     */
    public Set<LazyChunk> getChunks() {
        return chunks;
    }

    /**
     * @return
     * a Set of all chunks owned by this faction
     */
    public Set<Region> getRegions() {
        return regions;
    }

    @Override
    public void setAdmin(OfflinePlayer admin) {
        super.setAdmin(admin);
        checkForPersonalUnions();
    }

    /**
     * Updates personal unions.
     */
    public void checkForPersonalUnions() {
        HashSet<Faction> toRemove = new HashSet<>();
        for (Entry<Faction, Relation> entry : relations.entrySet()) {
            if (entry.getValue() == Relation.PERSONAL_UNION && !entry.getKey().admin.equals(admin)) {
                toRemove.add(entry.getKey());
            }
        }
        for (Faction key : toRemove) {
            relations.remove(key);
        }

        for (Faction faction : plugin.getFactionCache().getActive()) {
            if (faction == this) {
                continue;
            }
            if (faction.admin.equals(admin)) {
                relations.put(faction, Relation.PERSONAL_UNION);
                faction.relations.put(this, Relation.PERSONAL_UNION);
                ParsingUtil.broadcastMessage(FMessage.FACTION_PERSONAL_UNION_FORMED.getMessage(), this, faction, admin);
            } else if (faction.getRelation(this) == Relation.PERSONAL_UNION) {
                faction.relations.remove(this);
            }
        }
    }

    /**
     * @return
     * the faction admin as an FPlayer
     */
    public FPlayer getFAdmin() {
        return plugin.getFPlayerCache().getByUniqueId(admin);
    }

    /**
     * @return
     * the dynasty of the faction admin;
     * null if the faction is a republic or a theocracy
     */
    public Dynasty getDynasty() {
        return getFAdmin().getDynasty();
    }

    /**
     * @return
     * a PlayerCollection of all mods as OfflinePlayers
     */
    public PlayerCollection getMods() {
        return mods;
    }

    /**
     * @return
     * a Set of all mods as FPlayerCache
     */
    public Set<FPlayer> getFMods() {
        HashSet<FPlayer> fPlayers = new HashSet<>();
        for (UUID member : mods.getUniqueIds()) {
            fPlayers.add(plugin.getFPlayerCache().getByUniqueId(member));
        }
        return fPlayers;
    }

    /**
     * @return
     * a Set of all mods that are online
     */
    public Collection<Player> getOnlineMods() {
        return mods.getOnlinePlayers();
    }

    /**
     * @return
     * a Set of all members as OfflinePlayers
     */
    public PlayerCollection getMembers() {
        return members;
    }

    /**
     * @return
     * a Set of all members as FPlayerCache
     */
    public Collection<FPlayer> getFPlayers() {
        HashSet<FPlayer> fPlayers = new HashSet<>();
        for (UUID member : members.getUniqueIds()) {
            fPlayers.add(plugin.getFPlayerCache().getByUniqueId(member));
        }
        return fPlayers;
    }

    /**
     * @return
     * a Set of all members that are not mod or admin as OfflinePlayers
     */
    public Collection<OfflinePlayer> getNonPrivilegedMembers() {
        HashSet<OfflinePlayer> players = new HashSet<>();
        for (UUID member : members.getUniqueIds()) {
            if (!mods.contains(member) && !member.equals(admin)) {
                players.add(Bukkit.getOfflinePlayer(member));
            }
        }
        return players;
    }

    /**
     * @return
     * a Set of all members that are not mod or admin as FPlayerCache
     */
    public Collection<FPlayer> getNonPrivilegedFPlayers() {
        HashSet<FPlayer> fPlayers = new HashSet<>();
        for (UUID member : members.getUniqueIds()) {
            FPlayer fPlayer = plugin.getFPlayerCache().getByUniqueId(member);
            if (!isPrivileged(fPlayer)) {
                fPlayers.add(fPlayer);
            }
        }
        return fPlayers;
    }

    /**
     * @return
     * a Set of all members that are online
     */
    public Collection<Player> getOnlineMembers() {
        return members.getOnlinePlayers();
    }

    /**
     * @param relation
     * the relation type
     * @return
     * a Set of all related players that are online
     */
    public Collection<Player> getOnlineByRelation(Relation relation) {
        HashSet<Player> online = new HashSet<>();
        for (Faction faction : getRelatedFactions(relation)) {
            online.addAll(faction.getOnlineMembers());
        }
        return online;
    }

    /**
     * @return
     * a Set of all invited players
     */
    public PlayerCollection getInvitedPlayers() {
        return invited;
    }

    /**
     * @return
     * a Map of all relations except peace (default)
     */
    public Map<Faction, Relation> getRelations() {
        return relations;
    }

    @Override
    public Relation getRelation(RelationParticipator object) {
        Faction faction = null;
        if (object instanceof FPlayer) {
            if (((FPlayer) object).hasFaction()) {
                faction = ((FPlayer) object).getFaction();
            }
        } else if (object instanceof Faction) {
            faction = (Faction) object;
        }

        Faction lord = getLord();
        if (lord != null) {
            Relation relation = lord.getRelations().get(faction);
            return relation.doVassalsInherit() ? relation : relations.get(faction);
        } else if (relations.containsKey(faction)) {
            return relations.get(faction);
        } else if (faction == this) {
            return Relation.OWN;
        } else {
            return Relation.PEACE;
        }
    }

    /**
     * @param relation
     * the relation type
     * @return
     * all factions that have the specified relation to this faction
     */
    public Collection<Faction> getRelatedFactions(Relation relation) {
        HashSet<Faction> factions = new HashSet<>();
        for (Faction faction : plugin.getFactionCache().getActive()) {
            if (getRelation(faction) == relation || getRelation(faction).getIncludedRelations().contains(relation)) {
                factions.add(faction);
            }
        }
        return factions;
    }

    /**
     * @return
     * if the faction is a vassal
     */
    public boolean isVassal() {
        return !getRelatedFactions(Relation.LORD).isEmpty();
    }

    /**
     * @return
     * the lord faciton
     */
    public Faction getLord() {
        Collection<Faction> lord = getRelatedFactions(Relation.LORD);
        assert lord.size() <= 1;
        for (Faction faction : lord) {
            return faction;
        }
        return null;
    }

    /**
     * @return
     * the economy menu of the faction
     */
    public EconomyMenu getEconomyMenu() {
        return economyMenu;
    }

    /**
     * @return
     * the trade menu of the faction
     */
    public TradeMenu getTradeMenu() {
        return tradeMenu;
    }

    /**
     * @return
     * the storage of the faction
     */
    public FStorage getStorage() {
        return storage;
    }

    /**
     * @param resource
     * the resource
     * @return
     * how much of the resource is imported (export = negative values)
     */
    public int getImportValue(Resource resource) {
        Integer eximport = groceryList.get(resource);
        if (eximport == null) {
            return 0;
        } else {
            return eximport;
        }
    }

    /**
     * @return
     * a Map of exports / imports
     */
    public Map<Resource, Integer> getGroceryList() {
        return groceryList;
    }

    /**
     * @return
     * how many resources the people shall consume
     */
    public Map<Resource, Integer> getConsumableResources() {
        return consumableResources;
    }

    /**
     * @return
     * a Map of all saturated resources
     */
    public Map<Resource, Integer> getSaturatedResources() {
        return saturatedResources;
    }

    /**
     * @param resource
     * @param basic
     * if the resource subcategory is a basic need
     * @return if the resource is saturated
     */
    public SaturationLevel isResourceSaturated(Resource resource, boolean basic) {
        int value = saturatedResources.get(resource) != null ? saturatedResources.get(resource) : 0;
        return SaturationLevel.getByPercentage(value, basic);
    }

    /**
     * @param resource
     * @return if the resource is saturated
     */
    public SaturationLevel isResourceSaturated(Resource resource) {
        return isResourceSaturated(resource, false);
    }

    /**
     * @return
     * a Map of all saturated ResourceSubcategories
     */
    public Map<ResourceSubcategory, Integer> getSaturatedSubcategories() {
        return saturatedSubcategories;
    }

    /**
     * Ensures that the Map of saturated subcategories matchs the Map of saturated resources
     */
    public void updateSaturatedSubcategories() {
        for (ResourceSubcategory category : ResourceSubcategory.values()) {
            int percentage = 0;
            for (Resource resource : category.getResources()) {
                percentage += saturatedResources.get(resource);
            }
            percentage = percentage / category.getResources().length;
            saturatedSubcategories.put(category, percentage);
        }
        saturatedSubcategories.clear();
    }

    /**
     * @param subcategory
     * @return if the resource is saturated
     */
    public SaturationLevel isSubcategorySaturated(ResourceSubcategory subcategory) {
        int value = saturatedSubcategories.get(subcategory) != null ? saturatedSubcategories.get(subcategory) : 0;
        return SaturationLevel.getByPercentage(value, subcategory.isBasic());
    }

    /**
     * @return
     * the population menu
     */
    public PopulationMenu getPopulationMenu() {
        return populationMenu;
    }

    /**
     * @return
     * the idea menu
     */
    public IdeaMenu getIdeaMenu() {
        return ideaMenu;
    }

    /**
     * @return
     * the idea groups that this faction has chosen
     */
    public Set<IdeaGroup> getIdeaGroups() {
        return ideaGroups;
    }

    /**
     * @return
     * the ideas that this faction has bought
     */
    public Set<Idea> getIdeas() {
        return ideas;
    }

    /**
     * @return
     * the casus belli of this faction
     */
    public Set<CasusBelli> getCasusBelli() {
        return casusBelli;
    }

    /**
     * @param sender
     * the CommandSender to check
     * @return
     * true if the player is admin or mod
     */
    public boolean isPrivileged(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        return mods.contains(player.getUniqueId()) || admin.equals(player.getUniqueId()) || FPermission.hasPermission(sender, FPermission.BYPASS);
    }

    /**
     * @param fPlayer
     * the FPlayer to check
     * @return
     * true if the player is admin or mod
     */
    public boolean isPrivileged(FPlayer fPlayer) {
        return fPlayer.isMod(this) || admin.equals(fPlayer.getUniqueId()) || FPermission.hasPermission(fPlayer.getPlayer(), FPermission.BYPASS);
    }

    /* Actions */
    /**
     * Sends a message to all players in this faction.
     *
     * @param message
     * the message to send
     */
    public void sendMessage(String message, Object... args) {
        for (Player player : getOnlineMembers()) {
            ParsingUtil.sendMessage(player, "&a[" + name + "] &r" + message, args);
        }
        Player admin = Bukkit.getPlayer(this.admin);
        if (admin != null && !getOnlineMembers().contains(admin)) {
            ParsingUtil.sendMessage(admin, "&a[" + name + "] &r" + message, args);
        }
    }

    /**
     * Requires Spigot API!
     *
     * Sends an invitation to join this faction to the player.
     *
     * @param player
     * the player that will receive the invitation
     */
    public void sendInvitation(Player player) {
        ClickEvent onClickConfirm = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factionsxl join " + name);
        TextComponent confirm = new TextComponent(ChatColor.GREEN + FMessage.MISC_ACCEPT.getMessage());
        confirm.setClickEvent(onClickConfirm);

        ClickEvent onClickDeny = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/factionsxl join " + name + " deny");
        TextComponent deny = new TextComponent(ChatColor.DARK_RED + FMessage.MISC_DENY.getMessage());
        deny.setClickEvent(onClickDeny);

        ParsingUtil.sendMessage(player, FMessage.FACTION_INVITE.getMessage(name));
        player.spigot().sendMessage(confirm, new TextComponent(" "), deny);
    }

    /**
     * @param kicker
     * the CommandSender who kicked this member
     * @param member
     * the member to kick
     */
    public void kick(CommandSender kicker, OfflinePlayer member) {
        sendMessage(FMessage.FACTION_PLAYER_KICKED.getMessage(), member, kicker);
        members.remove(member.getUniqueId());
        mods.remove(member.getUniqueId());
    }

    /**
     * Actions when a day passed.
     */
    public void payday() {
        storage.payday();
    }

    /**
     * Makes the faction pay the resource value for the resource
     *
     * @param goods
     * a Map of resources and their amount
     * @param modifier
     * a modifier for the price, e.g. for taxes
     * @return
     * true if the faction can afford the price, false if not
     */
    public boolean chargeMoneyForResource(Map<Resource, Integer> goods, double modifier) {
        double price = 0;
        for (Entry<Resource, Integer> entry : goods.entrySet()) {
            price += entry.getValue() * entry.getKey().getValue();
        }
        boolean canAfford = account.getBalance() >= price * modifier;
        if (canAfford) {
            account.withdraw(price);
            for (Entry<Resource, Integer> entry : goods.entrySet()) {
                storage.getGoods().put(entry.getKey(), storage.getGoods().get(entry.getKey()) + entry.getValue());
            }
        }
        return canAfford;
    }

    /**
     * Makes the faction pay the resource value for the resource
     *
     * @param type
     * the resource type
     * @param amount
     * the amount of the resource
     * @param modifier
     * a modifier for the price, e.g. for taxes
     * @return
     * true if the faction can afford the price, false if not
     */
    public boolean chargeMoneyForResource(Resource type, int amount, double modifier) {
        double price = amount * type.getValue();
        boolean canAfford = account.getBalance() >= price * modifier;
        if (canAfford) {
            account.withdraw(price * modifier);
            storage.getGoods().put(type, storage.getGoods().get(type) + amount);
        }
        return canAfford;
    }

    /**
     * Makes the faction pay anything with the resource
     *
     * @param price
     * a Map of resources and their amount
     * @return
     * true if the faction can afford the price, false if not
     */
    public boolean chargeResource(Map<Resource, Integer> price) {
        for (Entry<Resource, Integer> entry : price.entrySet()) {
            if (!canAfford(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        for (Entry<Resource, Integer> entry : price.entrySet()) {
            if (entry.getKey() == Resource.TAXES) {
                boolean has = account.getBalance() >= entry.getValue();
                account.withdraw(entry.getValue());
                return has;
            } else if (entry.getKey() == Resource.MANPOWER) {
                // TODO
            } else {
                storage.getGoods().put(entry.getKey(), storage.getGoods().get(entry.getKey()) - entry.getValue());
            }
        }
        return true;
    }

    /**
     * Makes the faction pay anything with the resource
     *
     * @param type
     * the resource type to withdraw
     * @param price
     * the amount of resources to withdraw
     * @return
     * true if the faction can afford the price, false if not
     */
    public boolean chargeResource(Resource type, int price) {
        if (!canAfford(type, price)) {
            return false;
        }
        if (type == Resource.TAXES) {
            boolean has = account.getBalance() >= price;
            account.withdraw(price);
            return has;
        } else if (type == Resource.MANPOWER) {
            // TODO
        } else {
            storage.getGoods().put(type, storage.getGoods().get(type) - price);
        }
        return true;
    }

    /**
     * @param good
     * a good
     * @param amount
     * the amount of the good
     * @return
     * true if the faction can afford it, false if not
     */
    public boolean canAfford(Resource good, int amount) {
        if (good == Resource.TAXES) {
            return account.getBalance() >= amount;
        } else if (good == Resource.MANPOWER) {
            return true; // TODO
        }
        return storage.getGoods().get(good) >= amount;
    }

    /**
     * Deactivates the faction.
     */
    public void disband() {
        disband(true);
    }

    /**
     * Deactivates the faction.
     *
     * @param unclaim
     * if the land owner of the land of this faction shall be set to null.
     */
    public void disband(boolean unclaim) {
        active = false;
        open = false;
        home = null;
        if (fConfig.areHologramsEnabled()) {
            homeHolo.delete();
        }
        capital = null;
        if (unclaim) {
            for (Region region : regions) {
                region.setOwner(null);
            }
        }
        regions.clear();
        admin = null;
        mods.clear();
        members.clear();
        invited.clear();
        chunks.clear();
        relations.clear();
        for (Faction faction : plugin.getFactionCache().getActive()) {
            faction.relations.remove(this);
        }
        ideaGroups.clear();
        ideas.clear();
        FTeamWrapper.applyUpdates(this);
    }

    /* Serialization */
    public void load() {
        active = config.getBoolean("active");
        name = config.getString("name");
        longName = config.getString("longName");
        shortName = config.getString("shortName");
        desc = config.getString("desc");
        anthem = config.getString("anthem");
        banner = (BannerMeta) config.get("banner");
        bannerColor = (short) config.getInt("bannerColor");
        mapFillColor = config.getString("mapFillColor");
        mapLineColor = config.getString("mapLineColor");
        mapVisibility = config.getBoolean("mapVisibility");
        creationDate = config.getLong("creationDate");
        type = GovernmentType.valueOf(config.getString("type"));
        open = config.getBoolean("open");
        stability = config.getInt("stability");
        manpowerModifier = config.getDouble("manpowerModifier", fConfig.getDefaultManpowerModifier());
        setHome((Location) config.get("home"));
        capital = plugin.getBoard().getById(config.getInt("capital"));

        admin = UUID.fromString(config.getString("admin"));
        mods.add(config.getStringList("mods"));
        members.add(config.getStringList("members"));

        for (Entry<String, Object> entry : ConfigUtil.getMap(config, "relations").entrySet()) {
            relations.put(plugin.getFactionCache().getById(NumberUtil.parseInt(entry.getKey())), Relation.valueOf((String) entry.getValue()));
        }

        for (Region region : plugin.getBoard().getRegions()) {
            if (region.getOwner() == this) {
                regions.add(region);
            }
        }
        for (Region region : regions) {
            chunks.addAll(region.getChunks());
        }

        if (fConfig.isEconomyEnabled()) {
            account = new FAccount(this);
        }
        economyMenu = new EconomyMenu(this);
        tradeMenu = new TradeMenu(this);

        storage = new FStorage(this, ConfigUtil.getMap(config, "storage"));
        if (storage == null) {
            storage = new FStorage(this);
        }

        for (Entry<String, Object> entry : ConfigUtil.getMap(config, "groceryList").entrySet()) {
            if (EnumUtil.isValidEnum(Resource.class, entry.getKey())) {
                groceryList.put(Resource.valueOf(entry.getKey()), (int) entry.getValue());
            }
        }
        for (Entry<String, Object> entry : ConfigUtil.getMap(config, "consumableResources").entrySet()) {
            if (EnumUtil.isValidEnum(Resource.class, entry.getKey())) {
                consumableResources.put(Resource.valueOf(entry.getKey()), (int) entry.getValue());
            }
        }
        for (Resource resource : Resource.values()) {
            if (!consumableResources.containsKey(resource)) {
                consumableResources.put(resource, 0);
            }
        }
        for (Entry<String, Object> entry : ConfigUtil.getMap(config, "saturatedResources").entrySet()) {
            if (EnumUtil.isValidEnum(Resource.class, entry.getKey())) {
                saturatedResources.put(Resource.valueOf(entry.getKey()), (int) entry.getValue());
            }
        }
        for (Resource resource : Resource.values()) {
            if (!saturatedResources.containsKey(resource)) {
                saturatedResources.put(resource, 0);
            }
        }

        populationMenu = new PopulationMenu(this);

        List<String> groups = config.getStringList("ideaGroups");
        if (groups != null) {
            for (String group : groups) {
                if (EnumUtil.isValidEnum(IdeaGroup.class, group)) {
                    ideaGroups.add(IdeaGroup.valueOf(group));
                }
            }
        }
        List<String> ideas = config.getStringList("ideas");
        if (ideas != null) {
            for (String idea : ideas) {
                this.ideas.add(IdeaGroup.ideaValueOf(idea));
            }
        }
        ideaMenu = new IdeaMenu(this);
        ConfigurationSection cbs = config.getConfigurationSection("casusBelli");
        if (cbs != null) {
            for (String cb : cbs.getKeys(false)) {
                casusBelli.add(new CasusBelli(config.getConfigurationSection("casusBelli." + cb)));
            }
        }
    }

    public void save() {
        config.set("active", active);
        config.set("name", name);
        config.set("longName", longName);
        config.set("shortName", shortName);
        config.set("desc", desc);
        config.set("anthem", anthem);
        config.set("banner", banner);
        config.set("bannerColor", bannerColor);
        config.set("mapFillColor", mapFillColor);
        config.set("mapLineColor", mapLineColor);
        config.set("mapVisibility", mapVisibility);
        config.set("creationDate", creationDate);
        config.set("type", type.toString());
        config.set("open", open);
        config.set("stability", stability);
        if (!active) {
            try {
                config.save(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return;
        }
        config.set("home", home);
        if (homeHolo != null) {
            homeHolo.delete();
        }
        config.set("capital", capital.getId());
        config.set("admin", admin.toString());

        config.set("mods", mods.serialize());
        config.set("members", members.serialize());
        if (storage == null) {
            storage = new FStorage(this);
        }

        String relPath = "relations";
        if (!config.contains(relPath)) {
            config.createSection(relPath);
        }
        config.set("relations", null);
        for (Entry<Faction, Relation> entry : relations.entrySet()) {
            config.set(relPath + "." + entry.getKey().getId(), entry.getValue().toString());
        }

        config.set("storage", storage.serialize());
        for (Entry<Resource, Integer> entry : groceryList.entrySet()) {
            config.set("groceryList." + entry.getKey(), entry.getValue());
        }
        for (Entry<Resource, Integer> entry : consumableResources.entrySet()) {
            config.set("consumableResources." + entry.getKey(), entry.getValue());
        }
        for (Resource resource : Resource.values()) {
            if (!config.contains("consumableResources." + resource)) {
                config.set("consumableResources." + resource, 0);
            }
        }
        for (Entry<Resource, Integer> entry : saturatedResources.entrySet()) {
            config.set("saturatedResources." + entry.getKey(), entry.getValue());
        }
        for (Resource resource : Resource.values()) {
            if (!config.contains("saturatedResources." + resource)) {
                config.set("saturatedResources." + resource, 0);
            }
        }
        List<String> ideaGroupIds = new ArrayList<>();
        for (IdeaGroup ideaGroup : ideaGroups) {
            ideaGroupIds.add(ideaGroup.toString());
        }
        config.set("ideaGroups", ideaGroupIds);
        List<String> ideaIds = new ArrayList<>();
        for (Idea idea : ideas) {
            ideaIds.add(idea.toString());
        }
        config.set("ideas", ideaIds);
        int i = 0;
        for (CasusBelli cb : casusBelli) {
            config.set("casusBelli." + i, cb.serialize());
            i++;
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
