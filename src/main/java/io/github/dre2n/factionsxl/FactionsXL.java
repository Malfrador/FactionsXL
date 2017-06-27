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
package io.github.dre2n.factionsxl;

import com.griefcraft.lwc.LWC;
import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.compatibility.Internals;
import io.github.dre2n.commons.compatibility.Version;
import io.github.dre2n.commons.config.MessageConfig;
import io.github.dre2n.commons.javaplugin.DREPlugin;
import io.github.dre2n.commons.javaplugin.DREPluginSettings;
import io.github.dre2n.commons.misc.FileUtil;
import io.github.dre2n.factionsxl.board.Board;
import io.github.dre2n.factionsxl.board.dynmap.Atlas;
import io.github.dre2n.factionsxl.chat.ChatListener;
import io.github.dre2n.factionsxl.command.FCommandCache;
import io.github.dre2n.factionsxl.config.FConfig;
import io.github.dre2n.factionsxl.config.FData;
import io.github.dre2n.factionsxl.config.FMessage;
import io.github.dre2n.factionsxl.economy.IncomeTask;
import io.github.dre2n.factionsxl.faction.FBull;
import io.github.dre2n.factionsxl.faction.FMob;
import io.github.dre2n.factionsxl.faction.FactionCache;
import io.github.dre2n.factionsxl.player.AsyncPowerTask;
import io.github.dre2n.factionsxl.player.FPermission;
import io.github.dre2n.factionsxl.player.FPlayerCache;
import io.github.dre2n.factionsxl.player.PlayerListener;
import io.github.dre2n.factionsxl.protection.EntityProtectionListener;
import io.github.dre2n.factionsxl.protection.LandProtectionListener;
import io.github.dre2n.factionsxl.util.PageGUICache;
import java.io.File;
import java.io.IOException;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;

/**
 * The main class of FactionsXL.
 * It contains several important instances and the actions when the plugin is enabled / disabled.
 *
 * @author Daniel Saukel
 */
public class FactionsXL extends DREPlugin {

    private static FactionsXL instance;
    private LWC lwc;

    public static File BACKUPS;
    public static File LANGUAGES;
    public static File PLAYERS;
    public static File DYNASTIES;
    public static File FACTIONS;
    public static File FEDERATIONS;
    public static File TRADE_LEAGUES;

    private FConfig fConfig;
    private FData fData;
    private MessageConfig messageConfig;
    private FCommandCache fCommands;
    private FPlayerCache fPlayers;
    private FactionCache factions;
    private PageGUICache pageGUIs;
    private Board board;
    private ChatListener chatListener;
    private PlayerListener playerListener;
    private EntityProtectionListener entityProtectionListener;
    private LandProtectionListener landProtectionListener;
    private BukkitTask incomeTask;
    private BukkitTask powerTask;

    public FactionsXL() {
        /*
         * ##########################
         * ####~BRPluginSettings~####
         * ##########################
         * #~Internals~##INDEPENDENT#
         * #~SpigotAPI~##~~~~true~~~#
         * #~~~~UUID~~~##~~~~true~~~#
         * #~~Economy~~##~~~~true~~~#
         * #Permissions##~~~false~~~#
         * #~~Metrics~~##~~~~true~~~#
         * #Resource ID##~~~~????~~~#
         * ##########################
         */

        settings = new DREPluginSettings(true, true, true, false, true, Internals.INDEPENDENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (!compat.isSpigot() || !Version.andHigher(Version.MC1_9).contains(compat.getVersion())) {
            MessageUtil.log(this, "&4This plugin requires Spigot 1.9 or higher to work. It is not compatible with CraftBukkit and older versions.");
            manager.disablePlugin(this);
            return;
        }
        instance = this;

        FPermission.register();
        loadCore();
    }

    @Override
    public void onDisable() {
        if (instance != null) {
            saveData();
        }
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
    }

    // Initialize
    public void initFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        BACKUPS = new File(getDataFolder(), "backups");
        if (!BACKUPS.exists()) {
            BACKUPS.mkdir();
        }

        LANGUAGES = new File(getDataFolder(), "languages");
        if (!LANGUAGES.exists()) {
            LANGUAGES.mkdir();
        }

        PLAYERS = new File(getDataFolder(), "players");
        if (!PLAYERS.exists()) {
            PLAYERS.mkdir();
        }

        DYNASTIES = new File(getDataFolder(), "dynasties");
        if (!DYNASTIES.exists()) {
            DYNASTIES.mkdir();
        }

        FACTIONS = new File(getDataFolder(), "factions");
        if (!FACTIONS.exists()) {
            FACTIONS.mkdir();
        }

        FEDERATIONS = new File(getDataFolder(), "federations");
        if (!FEDERATIONS.exists()) {
            FEDERATIONS.mkdir();
        }

        TRADE_LEAGUES = new File(getDataFolder(), "tradeleagues");
        if (!TRADE_LEAGUES.exists()) {
            TRADE_LEAGUES.mkdir();
        }
    }

    public void loadCore() {
        initFolders();
        // Load Language
        loadMessageConfig(new File(LANGUAGES, "english.yml"));
        // Load Config
        loadFConfig(new File(getDataFolder(), "config.yml"));
        // Load Language 2
        loadMessageConfig(new File(LANGUAGES, fConfig.getLanguage() + ".yml"));
        loadFData();
        loadPageGUIs();
        loadFactions();
        loadBoard();
        loadFPlayers();
        fPlayers.loadAll();
        factions.loadAll();
        loadAtlas();
        loadFCommands();
        loadChatListener();
        loadPlayerListener();
        loadEntityProtectionListener();
        loadLandProtectionListener();
        if (fConfig.isLWCEnabled()) {
            loadLWC();
        }
        startPowerTask();
        if (fConfig.isEconomyEnabled()) {
            startIncomeTask();
        }
        manager.registerEvents(new FBull(), this);
        manager.registerEvents(new FMob(), this);
    }

    public void saveData() {
        fData.save();
        board.save(Board.FILE);
        factions.saveAll();
        fPlayers.saveAll();
        messageConfig.save();
        File backupDir = new File(BACKUPS, String.valueOf(System.currentTimeMillis()));
        backupDir.mkdir();
        FileUtil.copyDirectory(PLAYERS, new File(backupDir, "players"), new String[]{});
        FileUtil.copyDirectory(DYNASTIES, new File(backupDir, "dynasties"), new String[]{});
        FileUtil.copyDirectory(FACTIONS, new File(backupDir, "factions"), new String[]{});
        FileUtil.copyDirectory(FEDERATIONS, new File(backupDir, "federations"), new String[]{});
        FileUtil.copyDirectory(TRADE_LEAGUES, new File(backupDir, "tradeleagues"), new String[]{});
        try {
            FileUtil.copyFile(Board.FILE, new File(backupDir, "board.yml"));
            FileUtil.copyFile(new File(getDataFolder(), "config.yml"), new File(backupDir, "config.yml"));
            FileUtil.copyFile(FData.FILE, new File(backupDir, "data.yml"));
        } catch (IOException exception) {
        }
    }

    /* Getters and loaders */
    /**
     * @return
     * the plugin instance
     */
    public static FactionsXL getInstance() {
        return instance;
    }

    /**
     * @return
     * the loaded instance of FConfig
     */
    public FConfig getFConfig() {
        return fConfig;
    }

    /**
     * load / reload a new instance of FConfig
     */
    public void loadFConfig(File file) {
        fConfig = new FConfig(file);
    }

    /**
     * @return
     * the loaded instance of FData
     */
    public FData getFData() {
        return fData;
    }

    /**
     * load / reload a new instance of FData
     */
    public void loadFData() {
        fData = new FData();
    }

    /**
     * @return
     * the loaded instance of MessageConfig
     */
    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    /**
     * load / reload a new instance of MessageConfig
     */
    public void loadMessageConfig(File file) {
        messageConfig = new MessageConfig(FMessage.class, file);
    }

    /**
     * @return
     * the loaded instance of FCommandCache
     */
    @Override
    public FCommandCache getCommandCache() {
        return fCommands;
    }

    /**
     * load / reload a new instance of FCommandCache
     */
    public void loadFCommands() {
        fCommands = new FCommandCache(this);
        fCommands.register(this);
    }

    /**
     * @return
     * the loaded instance of FPlayerCache
     */
    public FPlayerCache getFPlayerCache() {
        return fPlayers;
    }

    /**
     * load / reload a new instance of FPlayerCache
     */
    public void loadFPlayers() {
        fPlayers = new FPlayerCache();
    }

    /**
     * @return
     * the loaded instance of FactionCache
     */
    public FactionCache getFactionCache() {
        return factions;
    }

    /**
     * load / reload a new instance of FactionCache
     */
    public void loadFactions() {
        factions = new FactionCache();
    }

    /**
     * @return
     * the loaded instance of Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * load / reload a new instance of Board
     */
    public void loadBoard() {
        board = new Board();
    }

    /**
     * load / reload a new instance of Atlas
     */
    public void loadAtlas() {
        Plugin plugin = manager.getPlugin("dynmap");
        if (plugin != null) {
            Atlas.init();
        } else {
            MessageUtil.log(this, FMessage.LOG_DYNMAP_NOT_ENABLED.getMessage());
        }
    }

    /**
     * @return
     * the loaded instance of ChatListener
     */
    public ChatListener getChatListener() {
        return chatListener;
    }

    /**
     * load / reload a new instance of ChatListener and register it
     */
    public void loadChatListener() {
        if (chatListener != null) {
            HandlerList.unregisterAll(chatListener);
        }
        chatListener = new ChatListener();
        manager.registerEvents(chatListener, this);
    }

    /**
     * @return
     * the loaded instance of PageGUICache
     */
    public PageGUICache getPageGUIs() {
        return pageGUIs;
    }

    /**
     * load / reload a new instance of PageGUICache
     */
    public void loadPageGUIs() {
        if (pageGUIs != null) {
            HandlerList.unregisterAll(pageGUIs);
        }
        pageGUIs = new PageGUICache();
        manager.registerEvents(pageGUIs, this);
    }

    /**
     * @return
     * the loaded instance of PlayerListener
     */
    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    /**
     * load / reload a new instance of PlayerListener and register it
     */
    public void loadPlayerListener() {
        if (playerListener != null) {
            HandlerList.unregisterAll(playerListener);
        }
        playerListener = new PlayerListener(this);
        manager.registerEvents(playerListener, this);
    }

    /**
     * @return
     * the loaded instance of EntityProtectionListener
     */
    public EntityProtectionListener getEntityProtectionListener() {
        return entityProtectionListener;
    }

    /**
     * load / reload a new instance of EntityProtectionListener and register it
     */
    public void loadEntityProtectionListener() {
        if (entityProtectionListener != null) {
            HandlerList.unregisterAll(entityProtectionListener);
        }
        entityProtectionListener = new EntityProtectionListener();
        manager.registerEvents(entityProtectionListener, this);
    }

    /**
     * @return
     * the loaded instance of LandProtectionListener
     */
    public LandProtectionListener getLandProtectionListener() {
        return landProtectionListener;
    }

    /**
     * load / reload a new instance of LandProtectionListener and register it
     */
    public void loadLandProtectionListener() {
        if (landProtectionListener != null) {
            HandlerList.unregisterAll(landProtectionListener);
        }
        landProtectionListener = new LandProtectionListener();
        manager.registerEvents(landProtectionListener, this);
    }

    /**
     * @return
     * the loaded instance of LWC
     */
    public LWC getLWC() {
        return lwc;
    }

    /**
     * load / reload the instance of LWC
     */
    public void loadLWC() {
        if (manager.isPluginEnabled("LWC")) {
            lwc = LWC.getInstance();
            PluginDescriptionFile desc = lwc.getPlugin().getDescription();
            MessageUtil.log(this, "Successfully hooked into " + desc.getName() + " v" + desc.getVersion() + ".");
        } else {
            MessageUtil.log(this, "&4Could not find LWC.");
        }
    }

    /**
     * @return
     * the AsyncPowerTask
     */
    public BukkitTask getPowerTask() {
        return powerTask;
    }

    /**
     * start a new AsyncPowerTask
     */
    public void startPowerTask() {
        long interval = fConfig.getPowerUpdateInterval();
        long passed = System.currentTimeMillis() - fData.lastPowerUpdate;
        double increase = fConfig.getPowerIncreaseRate();
        double decrease = fConfig.getPowerDecreaseRate();
        int maxPower = fConfig.getMaxPower();
        powerTask = new AsyncPowerTask(increase, decrease, maxPower).runTaskTimerAsynchronously(this, interval - passed, interval);
    }

    /**
     * @return
     * the IncomeTask
     */
    public BukkitTask getIncomeTask() {
        return incomeTask;
    }

    /**
     * start a new IncomeTask
     */
    public void startIncomeTask() {
        long dayLength = fConfig.getDayLength();
        long passed = System.currentTimeMillis() - fData.lastNewDay;
        incomeTask = new IncomeTask().runTaskTimer(this, dayLength - passed, dayLength);
    }

}
