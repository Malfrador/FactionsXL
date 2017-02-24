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
package io.github.dre2n.factionsxl.config;

import io.github.dre2n.commons.config.Messages;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.factionsxl.FactionsXL;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * An enumeration of all messages.
 * The values are fetched from the language file.
 *
 * @author Daniel Saukel
 */
public enum FMessage implements Messages {

    CHAT_CHANNEL_SWITCHED("chat.channelSwitched", "&aYour chat channel has been switched to &v1&a."),
    CHAT_PREFIX_ADMIN("chat.prefix.admin", "**"),
    CHAT_PREFIX_MEMBER("chat.prefix.member", new String()),
    CHAT_PREFIX_MOD("chat.prefix.mod", "*"),
    CMD_ADMIN_SUCCESS("cmd.admin.success", "&v1 &agave &v2 &athe leadership of &v3&a."),
    CMD_CLAIM_SUCCESS("cmd.claim.success", "&aSuccessfully annexed the region &v1&a."),
    CMD_CREATE_SUCCESS("cmd.create.success", "&v1 &acreated the new faction &v2&a."),
    CMD_DESC_SUCCESS("cmd.desc.success", "&aThe faction &v1&a changed their description to: &6&v2&a."),
    CMD_DISBAND_SUCCESS("cmd.disband.success", "&v1 &adisbanded the faction &v2&a."),
    CMD_INVITE_FAIL("cmd.invite.fail", "&4Could change the invitation status of &v1&4."),
    CMD_INVITE_SUCCESS("cmd.invite.success", "&v1 &ainvited &v2 &ato join your faction."),
    CMD_KICK_FAIL("cmd.kick.fail", "&4You are not allowed to kick &v1&4."),
    CMD_KICK_SUCCESS("cmd.kick.success", "&v1 &akicked &v2 &aout of the faction."),
    CMD_LIST_TITLE("cmd.list.title", "&6&l= List of Factions ="),
    CMD_MAIN_WELCOME("cmd.main.welcome", "&aWelcome to &4Factions&fXL"),
    CMD_MAIN_HELP("cmd.main.help", "&aType in &o/f help&r&a for further information."),
    CMD_MOD_PROMOTE("cmd.mod.promote", "&v1 &apromoted &v2&a. He will now serve as a moderator."),
    CMD_MOD_DEMOTE("cmd.mod.demote", "&v1 &ademoted &v2&a. He will not serve as a moderator any longer."),
    CMD_MONEY_BALANCE("cmd.money.balance", "&aThe faction &v1 &ahas &6&v2&a."),
    CMD_MONEY_DEPOSIT_FAIL("cmd.money.deposit.fail", "&4You do not have enough money."),
    CMD_MONEY_DEPOSIT_SUCCESS("cmd.money.deposit.success", "&v1 &agave &6&v2 &ato the bank of &v3&a."),
    CMD_MONEY_WITHDRAW_FAIL("cmd.money.withdraw.fail", "&v1 &4cannot afford &6&v2..."),
    CMD_MONEY_WITHDRAW_SUCCESS("cmd.money.withdraw.success", "&v1 &atook &6&v2 &afrom the bank of &v3&a."),
    CMD_OPEN_CLOSED("cmd.open.closed", "&v1 &aclosed the faction for uninvited players."),
    CMD_OPEN_OPENED("cmd.open.opened", "&v1 &aopened the faction for uninvited players."),
    CMD_POWER("cmd.power", "&aThe player &v1 &ahas &6&v2 &apower points."),
    CMD_REGION_CLAIMS("cmd.region.claims", "&6Claims: "),
    CMD_REGION_CORES("cmd.region.cores", "&6Cores: "),
    CMD_REGION_POPULATION("cmd.region.population", "&6Population: "),
    CMD_REGION_OWNER("cmd.region.owner", "&6Owner: "),
    CMD_REGION_TYPE("cmd.region.type", "&6Type: "),
    CMD_REGIONS_CHUNK_ADDED("cmd.regions.chunkAdded", "&aSuccessfully added this chunk to the region &v1&a."),
    CMD_REGIONS_CHUNK_REMOVED("cmd.regions.chunkRemoved", "&aSuccessfully removed this chunk from the region &v1&a."),
    CMD_REGIONS_CREATE("cmd.regions.create", "&aSuccessfully created a new region named &v1&a."),
    CMD_REGIONS_DELETE("cmd.regions.delete", "&aSuccessfully deleted the region &v1&a."),
    CMD_RELOAD_DONE("cmd.reload.done", "&aSuccessfully reloaded FactionsXL."),
    CMD_SET_ANTHEM_SUCCESS("cmd.setAnthem.success", "&v1 &aset the national anthem of &v2 &ato &6&v3&a."),
    CMD_SHOW_BALANCE("cmd.show.balance", "&6Balance: "),
    CMD_SHOW_CAPITAL("cmd.show.capital", "&6Capital: "),
    CMD_SHOW_DESCRIPTION("cmd.show.description", "&6Description: "),
    CMD_SHOW_GOVERNMENT_TYPE("cmd.show.governmentType", "&6Type of government: "),
    CMD_SHOW_INFO("cmd.show.info", "&6Stability / Power / Amount of provinces: &v1&v2 / &v3 / &v4"),
    CMD_SHOW_INVITATION("cmd.show.invitation", "&6Invitation required: "),
    CMD_SHOW_LEADER("cmd.show.leader", "&6Leader: "),
    CMD_SHOW_MEMBERS("cmd.show.members", "&6Members: "),
    CMD_SHOW_RELATIONS("cmd.show.relations", "&6Relations: "),
    CMD_SET_BANNER_SUCCESS("cmd.setBanner.success", "&v1 &adecided to let a new flag decorate the towers of &v2&a."),
    CMD_SET_CAPITAL_SUCCESS("cmd.setCapital.success", "&v1 &aset their capital to &v2&a."),
    CMD_SET_COLOR_SUCCESS("cmd.setColor.success", "&v1 &aset the livemap color of your faction to &v2 / &v3."),
    CMD_SET_GOVERNMENT_SUCCESS("cmd.setGovernment.success", "&v1 &aset their government type to &6&v2&a."),
    CMD_SET_HOME_FAIL("cmd.setHome.fail", "&4The home location must be in the territory of the faction."),
    CMD_SET_HOME_SUCCESS("cmd.setHome.success", "&v1 &ahas set your faction home to a new position."),
    CMD_TAG_SUCCESS("cmd.tag.success", "&aThe faction &v1 &achanged their tag to &6&v2&a."),
    CMD_TITLE_SUCCESS("cmd.title.success", "&v1 &achanged the title of &v2 &ato &6&v3&a."),
    CMD_UNCLAIM_FAIL("cmd.unclaim.fail", "&4You cannot unclaim your capital province."),
    CMD_UNCLAIM_SUCCESS("cmd.unclaim.success", "&aSuccessfully unclaimed the region &v1&a."),
    CMD_UNINVITE_SUCCESS("cmd.uninvite.success", "&v1 &aremoved &v2&a's invitation to join your faction."),
    ERROR_CANNOT_PASS_CAPITAL("cmd.cannotPass.capital", "&4You cannot give your capital to a vassal!"),
    ERROR_CANNOT_PASS_LAND("cmd.cannotPass.land", "&4You cannot give this land away!"),
    ERROR_CANNOT_TRADE_WITH_ITSELF("error.cannotTradeWithItself", "&4A faction cannot trade with itself."),
    ERROR_CMD_NOT_EXIST_1("error.cmdNotExist.1", "&4Command &6&v1&4 does not exist."),
    ERROR_CMD_NOT_EXIST_2("error.cmdNotExist.2", "&4Please enter &6/f help&4 for help."),
    ERROR_DO_NOT_MOVE("error.doNotMove", "&4You must not move."),
    ERROR_ECON_DISABLED("error.econDisabled", "&4Economy features are disabled."),
    ERROR_JOIN_FACTION("error.joinFaction", "&4You have to join a faction in order to do this."),
    ERROR_LAND_NOT_FOR_SALE("error.land.notForSale", "&4This land is not for sale."),
    ERROR_LAND_WILDERNESS("error.land.wilderness", "&4You cannot do this in the wilderness."),
    ERROR_LEAVE_FACTION("error.leaveFaction", "&4You have to leave your faction in order to do this."),
    ERROR_MAX_IDEA_GROUPS_REACHED("error.maxIdeaGroupsReached", "&7You reached the maximum amount of idea groups."),
    ERROR_NAME_IN_USE("error.nameInUse", "&4The name &6&v1 &4is already in use."),
    ERROR_NO_PERMISSION("error.noPermission", "&4You do not have permission to do this."),
    ERROR_NO_SUCH_FACTION("error.noSuch.faction", "&4The faction &6&v1 &4does not exist."),
    ERROR_NO_SUCH_GOVERNMENT_TYPE("error.noSuch.governmentType", "&4The government type &6&v1 &4does not exist."),
    ERROR_NO_SUCH_PLAYER("error.noSuch.player", "&4The player &6&v1 &4does not exist."),
    ERROR_NO_SUCH_REGION("error.noSuch.region", "&4This region does not exist."),
    ERROR_NO_SUCH_RELATION("error.noSuch.relation", "&4The relation &6&v1 &4does not exist."),
    ERROR_NOT_NUMERIC("error.notNumeric", "&6&v1 &4is not a numeric value."),
    ERROR_PERSONAL_UNION_WITH_FACTION("error.personalUnionWithFaction", "&4The faction &v1 &4has a personal union with &v2&4."),
    ERROR_PLAYER_NOT_IN_FACTION("error.playerNotInFaction", "&4The player &6&v1 &4is not member of the faction &6&v2&4."),
    ERROR_PLAYER_NOT_ONLINE("error.playerNotOnline", "&4The player &6&v1 &4is not online."),
    ERROR_SELECT_IDEA_GROUP("error.selectIdeaGroup", "&7You have to select the idea group first."),
    ERROR_SPECIFY_PLAYER("error.specifyFaction", "&4You have to specify a player."),
    ERROR_SPECIFY_FACTION("error.specifyFaction", "&4You have to specify a faction."),
    ERROR_VASSAL_IS_MOTHER_ADMIN("error.vassalIsMotherAdmin", "&4The player &v1 &4is already leader of the mother faction!"),
    FACTION_INVITE("faction.invite", "&aThe faction &v1&a would like to have you as a comrade. Join them?"),
    FACTION_JOIN_ACCEPT("faction.join.accept", "&v1 &ajoined the faction."),
    FACTION_JOIN_DENY("faction.join.deny", "&v1 &adecided not to join the faction."),
    FACTION_LEFT("faction.left", "&v1 &aleft the faction."),
    FACTION_LOST_CLAIM("faction.lostClaim", "&aThe faction &v1&a lost their claim for &v2&a."),
    FACTION_LOST_CORE("faction.lostCore", "&aThe faction &v1&a lost their core at &v2&a."),
    FACTION_NEW_CLAIM("faction.newClaim", "&aThe faction &v1&a got a new claim for &v2&a."),
    FACTION_NEW_CORE("faction.newCore", "&aThe faction &v1&a got a new core at &v2&a."),
    FACTION_PERSONAL_UNION_FORMED("faction.personalUnionFormed",
            "&aThe factions &v1 &aand &v2 &adecided to unite their dynasties under &v3 &aand formed a personal union!"),
    GOVERNMENT_TYPE_MONARCHY("governmentType.monarchy", "Monarchy"),
    GOVERNMENT_TYPE_REPUBLIC("governmentType.republic", "Republic"),
    GOVERNMENT_TYPE_THEOCRACY("governmentType.theocracy", "Theocracy"),
    HELP_CMD_ADMIN("help.cmd.admin", "/f admin ([faction]) [player] - Passes the ownership of a faction to another player."),
    HELP_CMD_ALLY("help.cmd.ally", "/f ally ([faction]) [target] - Sends an alliance request to the target faction."),
    HELP_CMD_CHAT("help.cmd.chat", "/f chat [f(action)|p(ublic)] [faction] - Changes the chat mode."),
    HELP_CMD_CLAIM("help.cmd.claim", "/f claim [faction] - Claims a territory."),
    HELP_CMD_CREATE("help.cmd.create", "/f create [tag] - Creates a new faction."),
    HELP_CMD_CREATE_VASSAL("help.cmd.createVassal", "/f createVassal [tag] [leader] - Creates a new faction as your vassal."),
    HELP_CMD_DESC("help.cmd.desc", "/f desc [faction] [description] - Changes the description of the faction."),
    HELP_CMD_DISBAND("help.cmd.disband", "/f disband [faction] - Disbands a faction."),
    HELP_CMD_HELP("help.cmd.help", "/f help [page] - Shows the help page."),
    HELP_CMD_HOME("help.cmd.home", "/f home ([faction]) - Teleports to the home point of a faction."),
    HELP_CMD_IDEA("help.cmd.idea", "/f idea ([faction]) - Opens the idea menu."),
    HELP_CMD_INVITE("help.cmd.invite", "/f invite [faction] [player] - Invites a player to join the faction."),
    HELP_CMD_JOIN("help.cmd.join", "/f join [faction] - Accepts an invitation."),
    HELP_CMD_KICK("help.cmd.kick", "/f kick [faction] [player] - Kicks the specified player out of the faction."),
    HELP_CMD_LEAVE("help.cmd.leave", "/f leave - Makes the player leave the faction."),
    HELP_CMD_LIST("help.cmd.list", "/f list - Shows a list of all factions."),
    HELP_CMD_LONG_TAG("help.cmd.longTag", "/f longtag [faction] [tag] - Changes the full name of a faction."),
    HELP_CMD_MAIN("help.cmd.main", "/f - General status information."),
    HELP_CMD_MOB("help.cmd.mob", "/f mob [type] - Spawns a faction mob of this type."),
    HELP_CMD_MOD("help.cmd.mod", "/f mod [faction] [player] - Grants another player faction moderator permissions."),
    HELP_CMD_MONEY("help.cmd.money", "/f money [faction] [b(alance)|d(eposit)|w(ithdraw)] ([amount])"),
    HELP_CMD_OPEN("help.cmd.open", "/f open [faction] - Allows / Forbids all players to join the faction without an invitation."),
    HELP_CMD_PAYDAY("help.cmd.payday", "/f payday ([amount]) - Enforces paydays."),
    HELP_CMD_POWER("help.cmd.power", "/f power ([player]) - Displays a player's power value."),
    HELP_CMD_REGION("help.cmd.region", "/f region ([region name/ID]) - Shows information about a region."),
    HELP_CMD_REGIONS("help.cmd.regions", "/f regions - Shows a list of all regions."),
    HELP_CMD_RELATION("help.cmd.relation", "/f relation [faction] [target] [relation] - Sends a relation request to the target faction."),
    HELP_CMD_RELOAD("help.cmd.reload", "/f reload - Reloads the plugin."),
    HELP_CMD_SCOREBOARD("help.cmd.scoreboard", "/f scoreboard - Toggles the scoreboard."),
    HELP_CMD_SET_ANTHEM("help.cmd.setAnthem", "/f setAnthem [faction] [anthem name] - Sets the faction anthem to a custom sound."),
    HELP_CMD_SET_BANNER("help.cmd.setBanner", "/f setBanner [faction] - Sets the faction banner to the one in the player's main hand."),
    HELP_CMD_SET_CAPITAL("help.cmd.setCapital", "/f setCapital - Sets the faction's capital to the player's current region."),
    HELP_CMD_SET_COLOR("help.cmd.setColor", "/f setColor [faction] [fill color] [line color] - Sets the livemap color to a specific RRGGBB color."),
    HELP_CMD_SET_HOME("help.cmd.setHome", "/f setHome [faction] - Sets the home point of the faction to the current position."),
    HELP_CMD_SET_GOVERNMENT("help.cmd.setGovernment", "/f setGovernment [faction] [type] - Sets the government type of the faction."),
    HELP_CMD_SHOW("help.cmd.show", "/f show [faction] - Shows a faction's status information."),
    HELP_CMD_STORAGE("help.cmd.storage", "/f storage [faction] - Shows a faction's storage."),
    HELP_CMD_TAG("help.cmd.tag", "/f tag ([faction]) [tag] - Changes the name of the faction."),
    HELP_CMD_TITLE("help.cmd.title", "/f title [player] [title] - Changes the player's title."),
    HELP_CMD_TRADE_OFFER("help.cmd.tradeOffer", "/f tradeOffer ([faction]) - Creates a trade offer."),
    HELP_CMD_UNCLAIM("help.cmd.unclaim", "/f unclaim - Unclaims a territory."),
    HELP_CMD_UNINVITE("help.cmd.uninvite", "/f uninvite ([faction]) [player] - Remove a player's invitation."),
    IDEA_GROUP_CENTRALIZATION("idea.group.centralization", "Centralization"),
    IDEA_GROUP_DIPLOMACY("idea.group.diplomacy", "Diplomacy"),
    IDEA_GROUP_ECONOMY("idea.group.economy", "Economy"),
    IDEA_GROUP_MERCENARY("idea.group.mercenary", "Mercenary"),
    IDEA_GROUP_RELIGION("idea.group.religion", "Religion"),
    IDEA_GROUP_SETTLER("idea.group.settler", "Settler"),
    IDEA_GROUP_TRADE("idea.group.trade", "Trade"),
    IDEA_DESC_SETTLER_COLONIZATION("idea.desc.settler.colonization", "&3+10% Manpower"),
    IDEA_MENU_GROUPS_TITLE("idea.menu.groups.title", "&6&lIdea Groups"),
    IDEA_MENU_GROUPS_DESELECTED("idea.menu.groups.deselected", "&aYou deselected the idea group &6&v1&a."),
    IDEA_MENU_GROUPS_SELECTED("idea.menu.groups.selected", "&aYou selected the idea group &6&v1&a."),
    IDEA_MENU_IDEAS_TITLE("idea.menu.ideas.title", "&6&l&v1 Ideas"),
    IDEA_NAME_SETTLER_COLONIZATION("idea.name.settler.colonization", "Colonization"),
    LOG_DYNMAP_NOT_ENABLED("log.dynmapNotEnabled", "&4Could not find Dynmap."),
    LOG_NEW_FACTION_DATA("log.newFactionData", "&6A new faction data file has been created and saved as &v1."),
    LOG_NEW_PLAYER_DATA("log.newPlayerData", "&6A new player data file has been created and saved as &v1."),
    MISC_ACCEPT("misc.accept", "[ ACCEPT ]"),
    MISC_BACK("misc.back", "&6&lBACK"),
    MISC_DENY("misc.deny", "[ DENY ]"),
    MISC_NEXT_PAGE("misc.nextPage", "&6&lNEXT PAGE"),
    MISC_PREVIOUS_PAGE("misc.previousPage", "&6&lPREVIOUS PAGE"),
    MISC_PURCHASE_FAIL("misc.purchase.success", "&4You cannot afford &6&v1&4."),
    MISC_PURCHASE_SUCCESS("misc.purchase.success", "&aYou successfully purchased &6&v1&a."),
    MISC_SHIFT_CLICK_PURCHASE("misc.shiftClick.purchase", "&7Hold [SHIFT] and click to purchase."),
    MISC_SHIFT_CLICK_SELECT("misc.shiftClick.select", "&7Hold [SHIFT] and click to select."),
    MISC_WILDERNESS("misc.wilderness", "Wilderness"),
    MOB_TRADER("mob.trader", "&6Trader"),
    MOB_VILLAGER("mob.villager", "&6Villager"),
    POPULATION_DEMANDS("population.demands", "&aDemands"),
    POPULATION_MILITARY("population.military", "&aMilitary Status"),
    POPULATION_TITLE("population.title", "&6&lPopluation - &v1"),
    PROTECTION_CANNOT_ATTACK_FACTION("protection.cannotAttackFaction", "&4You may not attack in the territory of &v1&4."),
    PROTECTION_CANNOT_BUILD_FACTION("protection.cannotBuildFaction", "&4You may not build in the territory of &v1&4."),
    PROTECTION_CANNOT_BUILD_WILDERNESS("protection.cannotBuildWilderness", "&4You may not build in the wilderness."),
    PROTECTION_CANNOT_DESTROY_FACTION("protection.cannotDestroyFaction", "&4You may not destroy the territory of &v1&4."),
    PROTECTION_CANNOT_DESTROY_WILDERNESS("protection.cannotDestroyFaction", "&4You may not destroy the wilderness."),
    REGION_BARREN("region.barren", "Barren"),
    REGION_CITY("region.city", "City"),
    REGION_DESERT("region.desert", "Desert"),
    REGION_FARMLAND("region.farmland", "Farmland"),
    REGION_FOREST("region.forest", "Forest"),
    REGION_MAGIC("region.magic", "Magic"),
    REGION_MOUNTAINOUS("region.mountainous", "Mountainous"),
    REGION_SEA("region.sea", "Sea"),
    RELATION_ALLIANCE("relation.alliance", "Alliance"),
    RELATION_ALLIANCE_DESC("relation.allianceDesc", "Two friendly factions form an alliance to defend each other."),
    RELATION_COALITION("relation.coalition", "Coalition"),
    RELATION_COALITION_DESC("relation.coalitionDesc", "Two neutral factions form a transient alliance to defeat a common enemy."),
    RELATION_CONFIRMED("relation.confirmed", "&aThe factions &v1 &aand &v2 &aset their relation to &v3&a."),
    RELATION_DENIED("relation.denied", "&aThe faction &v1 &adenied your request to set your relation to &v2&a."),
    RELATION_ENEMY("relation.enemy", "Enemy"),
    RELATION_ENEMY_DESC("relation.enemyDesc", "It seems these factions don't like each other very much..."),
    RELATION_LORD("relation.lord", "Lord"),
    RELATION_LORD_DESC("relation.lordDesc", "This faction rules over its vassal."),
    RELATION_OWN("relation.own", "Own"),
    RELATION_OWN_DESC("relation.ownDesc", "Your faction."),
    RELATION_PEACE("relation.peace", "Peace"),
    RELATION_PEACE_DESC("relation.peaceDesc", "There are no special contacts to this faction."),
    RELATION_PERSONAL_UNION("relation.personalUnion", "Personal Union"),
    RELATION_PERSONAL_UNION_DESC("relation.personalUnionDesc", "Two factions are ruled by the same monarch."),
    RELATION_REAL_UNION("relation.realUnion", "Real Union"),
    RELATION_REAL_UNION_DESC("relation.realUnionDesc", "An advanced personal union. The factions can be regarded as one."),
    RELATION_VASSAL("relation.vassal", "Vassal"),
    RELATION_VASSAL_DESC("relation.vassalDesc", "This faction vowed to serve its foreign lord."),
    RELATION_VASSALIZED("relation.vassalized", "&aThe faction &v1 &avassalized &v2&a."),
    RELATION_WISH("relation.wish", "&aThe faction &v1 &aasked your faction to change your relation to &v2&a."),
    RELATION_WISH_OWN("relation.wishOwn", "&v1 &aasked the faction &v2 &ato change your relation to &v3&a."),
    RESOURCE_COAL("resource.coal", "Coal"),
    RESOURCE_SULPHUR("resource.sulphur", "Sulphur"),
    RESOURCE_GOLD("resource.gold", "Gold"),
    RESOURCE_IRON("resource.iron", "Iron"),
    RESOURCE_DIAMOND("resource.diamond", "Diamond"),
    RESOURCE_EMERALD("resource.emerald", "Emerald"),
    RESOURCE_LAPIS_LAZULI("resource.lapisLazuli", "Lapis Lazuli"),
    RESOURCE_QUARTZ("resource.quartz", "Quartz"),
    RESOURCE_REDSTONE("resource.redstone", "Redstone"),
    RESOURCE_ANDESITE("resource.andesite", "Andesite"),
    RESOURCE_DIORITE("resource.diorite", "Diorite"),
    RESOURCE_GRANITE("resource.granite", "Granite"),
    RESOURCE_GRAVEL("resource.gravel", "Gravel"),
    RESOURCE_OBSIDIAN("resource.obsidian", "Obsidian"),
    RESOURCE_STONE("resource.stone", "Stone"),
    RESOURCE_CHICKEN("resource.chicken", "Chicken"),
    RESOURCE_COW("resource.cow", "Cow"),
    RESOURCE_HORSE("resource.horse", "Horse"),
    RESOURCE_PIG("resource.pig", "Pig"),
    RESOURCE_RABBIT("resource.rabbit", "Rabbit"),
    RESOURCE_SHEEP("resource.sheep", "Sheep"),
    RESOURCE_APPLE("resource.apple", "Apple"),
    RESOURCE_BEETROOT("resource.beetroot", "Beetroot"),
    RESOURCE_CARROT("resource.carrot", "Carrot"),
    RESOURCE_CHORUS("resource.chorus", "Chorus"),
    RESOURCE_COCOA("resource.cocoa", "Cocoa"),
    RESOURCE_MELON("resource.melon", "Melon"),
    RESOURCE_POTATO("resource.potato", "Potato"),
    RESOURCE_PUMPKIN("resource.pumpkin", "Pumpkin"),
    RESOURCE_SUGAR("resource.sugar", "Sugar"),
    RESOURCE_WHEAT("resource.wheat", "Wheat"),
    RESOURCE_ACACIA("resource.acacia", "Adacia Wood"),
    RESOURCE_BIRCH("resource.birch", "Birch Wood"),
    RESOURCE_DARK_OAK("resource.sulphur", "Dark Oak Wood"),
    RESOURCE_JUNGLE("resource.jungle", "Jungle Wood"),
    RESOURCE_OAK("resource.oak", "Oak Wood"),
    RESOURCE_SPRUCE("resource.spruce", "Spruce Wood"),
    RESOURCE_PAPER("resource.paper", "Paper"),
    RESOURCE_MUSHROOMS("resource.mushrooms", "Mushrooms"),
    RESOURCE_CODFISH("resource.codfish", "Codfish"),
    RESOURCE_CLOWNFISH("resource.clownfish", "Clownfish"),
    RESOURCE_PUFFERFISH("resource.pufferfish", "Pufferfish"),
    RESOURCE_SALMON("resource.salmon", "Salmon"),
    RESOURCE_INK("resource.ink", "Ink"),
    RESOURCE_SALT("resource.salt", "Salt"),
    RESOURCE_WATER("resource.water", "Water"),
    RESOURCE_CLAY("resource.clay", "Clay"),
    RESOURCE_PRISMARINE("resource.prismarine", "Prismarine"),
    RESOURCE_DRAGON_BREATH("resource.dragonBreath", "Dragon Breath"),
    RESOURCE_EXPERIENCE("resource.experience", "Experience"),
    RESOURCE_NETHER_WART("resource.netherWart", "Nether Wart"),
    RESOURCE_PURPUR("resource.purpur", "Purpur"),
    RESOURCE_CACTUS("resource.cactus", "Cactus"),
    RESOURCE_GLASS("resource.glass", "Glass"),
    RESOURCE_RED_SANDSTONE("resource.redSandstone", "Red Sandstone"),
    RESOURCE_YELLOW_SANDSTONE("resource.yellowSandstone", "Yellow Sandstone"),
    RESOURCE_CRAFT("resource.craft", "Craft"),
    RESOURCE_MANPOWER("resource.manpower", "Manpower"),
    RESOURCE_TAXES("resource.taxes", "Taxes"),
    STORAGE_NON_PHYSICAL("storage.nonPhysical", "&c&v1 is a non-physical resource."),
    STORAGE_NON_PHYSICAL_MANPOWER("storage.nonPhysical.manpower", "&cCheck your villagers to see information."),
    STORAGE_NON_PHYSICAL_TAXES("storage.nonPhysical.taxes", "&cTaxes flow into your bank account (/f money)."),
    STORAGE_PAYDAY("storage.payday", "&aA day has passed. Taxes and trade have been collected."),
    STORAGE_STOCK("storage.stock", "&3Stock: &v1"),
    STORAGE_TITLE("storage.title", "&6&lStorage - &v1"),
    TRADE_TITLE("trade.title", "&6&lTrade - &v1"),
    TRADE_EXPORT("trade.export", "&a&lEXPORT"),
    TRADE_EXPORT_DOWN("trade.exportDown", "&7Decrease export"),
    TRADE_EXPORT_UP("trade.exportUp", "&7Increase export"),
    TRADE_IMPORT("trade.import", "&a&lIMPORT"),
    TRADE_IMPORT_DOWN("trade.importDown", "&7Decrease import"),
    TRADE_IMPORT_UP("trade.importUp", "&7Increase import"),
    TRADE_OFFER_CHOOSE_EXPORT("trade.offer.chooseExport", "&2&lChoose your action:"),
    TRADE_OFFER_CHOOSE_PARTNER("trade.offer.choosePartner", "&2&lChoose your trade partner:"),
    TRADE_OFFER_PRICE("trade.offer.price", "Price"),
    TRADE_OFFER_AMOUNT("trade.offer.amount", "Amount"),
    TRADE_OFFER_CHOOSE_RESOURCE("trade.offer.chooseResource", "&2&lChoose a resource:"),
    TRADE_RESOURCE_TITLE("trade.resourceTitle", "&6&l&v1 - &v2");

    private String identifier;
    private String message;

    FMessage(String identifier, String message) {
        this.identifier = identifier;
        this.message = message;
    }

    /* Getters and setters */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public String getMessage(String... args) {
        return FactionsXL.getInstance().getMessageConfig().getMessage(this, args);
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /* Actions */
    /**
     * Sends the message to the console.
     */
    public void debug() {
        MessageUtil.log(FactionsXL.getInstance(), getMessage());
    }

    /* Statics */
    /**
     * @param identifer
     * the identifer to set
     */
    public static Messages getByIdentifier(String identifier) {
        for (Messages message : values()) {
            if (message.getIdentifier().equals(identifier)) {
                return message;
            }
        }

        return null;
    }

    /**
     * @return a FileConfiguration containing all messages
     */
    public static FileConfiguration toConfig() {
        FileConfiguration config = new YamlConfiguration();
        for (FMessage message : values()) {
            config.set(message.getIdentifier(), message.message);
        }
        return config;
    }

}
