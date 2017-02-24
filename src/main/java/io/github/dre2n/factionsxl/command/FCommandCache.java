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
package io.github.dre2n.factionsxl.command;

import io.github.dre2n.commons.command.BRCommands;
import io.github.dre2n.commons.javaplugin.BRPlugin;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.command.relation.*;

/**
 * An enumeration of all command instances.
 *
 * @author Daniel Saukel
 */
public class FCommandCache extends BRCommands {

    public static final String LABEL = "factionsxl";

    public static AdminCommand ADMIN = new AdminCommand();
    public static ChatCommand CHAT = new ChatCommand();
    public static ClaimCommand CLAIM = new ClaimCommand();
    public static CreateCommand CREATE = new CreateCommand();
    public static CreateVassalCommand CREATE_VASSAL = new CreateVassalCommand();
    public static DescCommand DESC = new DescCommand();
    public static DisbandCommand DISBAND = new DisbandCommand();
    public static HelpCommand HELP = new HelpCommand();
    public static HomeCommand HOME = new HomeCommand();
    public static IdeaCommand IDEA = new IdeaCommand();
    public static InviteCommand INVITE = new InviteCommand();
    public static JoinCommand JOIN = new JoinCommand();
    public static KickCommand KICK = new KickCommand();
    public static LeaveCommand LEAVE = new LeaveCommand();
    public static ListCommand LIST = new ListCommand();
    public static LongTagCommand LONG_TAG = new LongTagCommand();
    public static MainCommand MAIN = new MainCommand();
    public static MobCommand MOB = new MobCommand();
    public static ModCommand MOD = new ModCommand();
    public static MoneyCommand MONEY = new MoneyCommand();
    public static OpenCommand OPEN = new OpenCommand();
    public static PaydayCommand PAYDAY = new PaydayCommand();
    public static PowerCommand POWER = new PowerCommand();
    public static RegionCommand REGION = new RegionCommand();
    public static RegionsCommand REGIONS = new RegionsCommand();
    public static RelationCommand RELATION = new RelationCommand();
    public static RelationAllyCommand RELATION_ALLY = new RelationAllyCommand();
    public static RelationOathCommand RELATION_OATH = new RelationOathCommand();
    public static RelationNeutralCommand RELATION_NEUTRAL = new RelationNeutralCommand();
    public static RelationVassalizeCommand RELATION_VASSALIZE = new RelationVassalizeCommand();
    public static ReloadCommand RELOAD = new ReloadCommand();
    public static ScoreboardCommand SCOREBOARD = new ScoreboardCommand();
    public static SetAnthemCommand SET_ANTHEM = new SetAnthemCommand();
    public static SetBannerCommand SET_BANNER = new SetBannerCommand();
    public static SetCapitalCommand SET_CAPITAL = new SetCapitalCommand();
    public static SetColorCommand SET_COLOR = new SetColorCommand();
    public static SetGovernmentCommand SET_GOVERNMENT = new SetGovernmentCommand();
    public static SetHomeCommand SET_HOME = new SetHomeCommand();
    public static ShowCommand SHOW = new ShowCommand();
    public static StorageCommand STORAGE = new StorageCommand();
    public static TagCommand TAG = new TagCommand();
    public static TitleCommand TITLE = new TitleCommand();
    public static TradeOfferCommand TRADE_OFFER = new TradeOfferCommand();
    public static UnclaimCommand UNCLAIM = new UnclaimCommand();
    public static UninviteCommand UNINVITE = new UninviteCommand();

    public FCommandCache(BRPlugin plugin) {
        super("factionsxl", plugin,
                ADMIN,
                CHAT,
                CLAIM,
                CREATE,
                CREATE_VASSAL,
                DESC,
                DISBAND,
                HELP,
                HOME,
                INVITE,
                JOIN,
                KICK,
                LEAVE,
                LIST,
                LONG_TAG,
                MAIN,
                MOB,
                MOD,
                OPEN,
                POWER,
                REGION,
                REGIONS,
                RELATION,
                RELATION_ALLY,
                RELATION_OATH,
                RELATION_NEUTRAL,
                RELATION_VASSALIZE,
                RELOAD,
                SCOREBOARD,
                SET_ANTHEM,
                SET_BANNER,
                SET_CAPITAL,
                SET_COLOR,
                SET_GOVERNMENT,
                SET_HOME,
                SHOW,
                TAG,
                TITLE,
                UNCLAIM,
                UNINVITE
        );
        if (FactionsXL.getInstance().getFConfig().isEconomyEnabled()) {
            addCommand(IDEA);
            addCommand(MONEY);
            addCommand(PAYDAY);
            addCommand(STORAGE);
            addCommand(TRADE_OFFER);
        }
    }

}
