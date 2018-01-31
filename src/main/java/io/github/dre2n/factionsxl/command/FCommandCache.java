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
package io.github.dre2n.factionsxl.command;

import io.github.dre2n.commons.command.DRECommandCache;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.command.relation.*;
import io.github.dre2n.factionsxl.command.war.*;
import io.github.dre2n.factionsxl.config.FConfig;

/**
 * An enumeration of all command instances.
 *
 * @author Daniel Saukel
 */
public class FCommandCache extends DRECommandCache {

    public static final String LABEL = "factionsxl";

    FactionsXL plugin = FactionsXL.getInstance();

    public AddCasusBelliCommand addCasusBelli = new AddCasusBelliCommand(plugin);
    public AdminCommand admin = new AdminCommand(plugin);
    public CasusBelliCommand casusBelli = new CasusBelliCommand(plugin);
    public ChatCommand chat = new ChatCommand(plugin);
    public ClaimCommand claim = new ClaimCommand(plugin);
    public ConfirmWarCommand confirmWar = new ConfirmWarCommand(plugin);
    public CreateCommand create = new CreateCommand(plugin);
    public CreateBullCommand createBull = new CreateBullCommand(plugin);
    public CreateVassalCommand createVassal = new CreateVassalCommand(plugin);
    public DescCommand desc = new DescCommand(plugin);
    public DisbandCommand disband = new DisbandCommand(plugin);
    public HelpCommand help = new HelpCommand(plugin);
    public HomeCommand home = new HomeCommand(plugin);
    public IdeaCommand idea = new IdeaCommand(plugin);
    public IntegrateCommand integrate = new IntegrateCommand(plugin);
    public InviteCommand invite = new InviteCommand(plugin);
    public JoinCommand join = new JoinCommand(plugin);
    public KickCommand kick = new KickCommand(plugin);
    public LeaveCommand leave = new LeaveCommand(plugin);
    public ListCommand list = new ListCommand(plugin);
    public LongTagCommand longTag = new LongTagCommand(plugin);
    public MainCommand main = new MainCommand(plugin);
    public MobCommand mob = new MobCommand(plugin);
    public ModCommand mod = new ModCommand(plugin);
    public MoneyCommand money = new MoneyCommand(plugin);
    public OpenCommand open = new OpenCommand(plugin);
    public PaydayCommand payday = new PaydayCommand(plugin);
    public PlayerHomeCommand playerHome = new PlayerHomeCommand(plugin);
    public PowerCommand power = new PowerCommand(plugin);
    public RegionCommand region = new RegionCommand(plugin);
    public RegionsCommand regions = new RegionsCommand(plugin);
    public RelationCommand relation = new RelationCommand(plugin);
    public RelationAllyCommand relationAlly = new RelationAllyCommand(plugin);
    public RelationOathCommand relationOath = new RelationOathCommand(plugin);
    public RelationNeutralCommand relationNeutral = new RelationNeutralCommand(plugin);
    public RelationUniteCommand relationUnite = new RelationUniteCommand(plugin);
    public RelationVassalizeCommand relationVassalize = new RelationVassalizeCommand(plugin);
    public ReloadCommand reload = new ReloadCommand(plugin);
    public ScoreboardCommand scoreboard = new ScoreboardCommand(plugin);
    public SetAnthemCommand setAnthem = new SetAnthemCommand(plugin);
    public SetBannerCommand setBanner = new SetBannerCommand(plugin);
    public SetCapitalCommand setCapital = new SetCapitalCommand(plugin);
    public SetColorCommand setColor = new SetColorCommand(plugin);
    public SetGovernmentCommand setGovernment = new SetGovernmentCommand(plugin);
    public SetHomeCommand setHome = new SetHomeCommand(plugin);
    public SetPlayerHomeCommand setPlayerHome = new SetPlayerHomeCommand(plugin);
    public SetPowerCommand setPower = new SetPowerCommand(plugin);
    public ShortTagCommand shortTag = new ShortTagCommand(plugin);
    public ShowCommand show = new ShowCommand(plugin);
    public StorageCommand storage = new StorageCommand(plugin);
    public TagCommand tag = new TagCommand(plugin);
    public TitleCommand title = new TitleCommand(plugin);
    public TradeOfferCommand tradeOffer = new TradeOfferCommand(plugin);
    public UnclaimCommand unclaim = new UnclaimCommand(plugin);
    public UninviteCommand uninvite = new UninviteCommand(plugin);
    public WarCommand war = new WarCommand(plugin);
    public WarStatusCommand warStatus = new WarStatusCommand(plugin);
    public WorldCommand world = new WorldCommand(plugin);

    public FCommandCache(FactionsXL plugin) {
        super(LABEL, plugin);
        addCommand(addCasusBelli);
        addCommand(admin);
        addCommand(casusBelli);
        addCommand(chat);
        addCommand(claim);
        addCommand(confirmWar);
        addCommand(create);
        addCommand(createBull);
        addCommand(createVassal);
        addCommand(desc);
        addCommand(disband);
        addCommand(help);
        addCommand(home);
        addCommand(integrate);
        addCommand(invite);
        addCommand(join);
        addCommand(kick);
        addCommand(leave);
        addCommand(list);
        addCommand(longTag);
        addCommand(main);
        addCommand(mob);
        addCommand(mod);
        addCommand(open);
        addCommand(power);
        addCommand(region);
        addCommand(regions);
        addCommand(relation);
        addCommand(relationAlly);
        addCommand(relationOath);
        addCommand(relationNeutral);
        addCommand(relationUnite);
        addCommand(relationVassalize);
        addCommand(reload);
        addCommand(scoreboard);
        addCommand(setAnthem);
        addCommand(setBanner);
        addCommand(setCapital);
        addCommand(setColor);
        addCommand(setGovernment);
        addCommand(setHome);
        addCommand(setPower);
        addCommand(shortTag);
        addCommand(show);
        addCommand(tag);
        addCommand(title);
        addCommand(unclaim);
        addCommand(uninvite);
        addCommand(war);
        addCommand(warStatus);
        addCommand(world);
        FConfig config = plugin.getFConfig();
        if (config.isEconomyEnabled()) {
            addCommand(idea);
            addCommand(money);
            addCommand(payday);
            addCommand(storage);
            addCommand(tradeOffer);
        }
        if (config.arePlayerHomesEnabled()) {
            addCommand(playerHome);
            addCommand(setPlayerHome);
        }
    }

    public void registerAliases() {
        FCommandAlias alias = new FCommandAlias(this);
        plugin.getCommand("home").setExecutor(alias);
        plugin.getCommand("setHome").setExecutor(alias);
    }

}
