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

import io.github.dre2n.commons.command.DRECommandCache;
import io.github.dre2n.commons.javaplugin.DREPlugin;
import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.command.relation.*;

/**
 * An enumeration of all command instances.
 *
 * @author Daniel Saukel
 */
public class FCommandCache extends DRECommandCache {

    public static final String LABEL = "factionsxl";

    public AddCasusBelliCommand addCasusBelli = new AddCasusBelliCommand();
    public AdminCommand admin = new AdminCommand();
    public AddCasusBelliCommand casusBelli = new AddCasusBelliCommand();
    public ChatCommand chat = new ChatCommand();
    public ClaimCommand claim = new ClaimCommand();
    public CreateCommand create = new CreateCommand();
    public CreateBullCommand createBull = new CreateBullCommand();
    public CreateVassalCommand createVassal = new CreateVassalCommand();
    public DescCommand desc = new DescCommand();
    public DisbandCommand disband = new DisbandCommand();
    public HelpCommand help = new HelpCommand();
    public HomeCommand home = new HomeCommand();
    public IdeaCommand idea = new IdeaCommand();
    public InviteCommand invite = new InviteCommand();
    public JoinCommand join = new JoinCommand();
    public KickCommand kick = new KickCommand();
    public LeaveCommand leave = new LeaveCommand();
    public ListCommand list = new ListCommand();
    public LongTagCommand longTag = new LongTagCommand();
    public MainCommand main = new MainCommand();
    public MobCommand mob = new MobCommand();
    public ModCommand mod = new ModCommand();
    public MoneyCommand money = new MoneyCommand();
    public OpenCommand open = new OpenCommand();
    public PaydayCommand payday = new PaydayCommand();
    public PowerCommand power = new PowerCommand();
    public RegionCommand region = new RegionCommand();
    public RegionsCommand regions = new RegionsCommand();
    public RelationCommand relation = new RelationCommand();
    public RelationAllyCommand relationAlly = new RelationAllyCommand();
    public RelationOathCommand relationOath = new RelationOathCommand();
    public RelationNeutralCommand relationNeutral = new RelationNeutralCommand();
    public RelationUniteCommand relationUnite = new RelationUniteCommand();
    public RelationVassalizeCommand relationVassalize = new RelationVassalizeCommand();
    public ReloadCommand reload = new ReloadCommand();
    public ScoreboardCommand scoreboard = new ScoreboardCommand();
    public SetAnthemCommand setAnthem = new SetAnthemCommand();
    public SetBannerCommand setBanner = new SetBannerCommand();
    public SetCapitalCommand setCapital = new SetCapitalCommand();
    public SetColorCommand setColor = new SetColorCommand();
    public SetGovernmentCommand setGovernment = new SetGovernmentCommand();
    public SetHomeCommand setHome = new SetHomeCommand();
    public SetPowerCommand setPower = new SetPowerCommand();
    public ShortTagCommand shortTag = new ShortTagCommand();
    public ShowCommand show = new ShowCommand();
    public StorageCommand storage = new StorageCommand();
    public TagCommand tag = new TagCommand();
    public TitleCommand title = new TitleCommand();
    public TradeOfferCommand tradeOffer = new TradeOfferCommand();
    public UnclaimCommand unclaim = new UnclaimCommand();
    public UninviteCommand uninvite = new UninviteCommand();
    public WorldCommand world = new WorldCommand();

    public FCommandCache(DREPlugin plugin) {
        super("factionsxl", plugin);
        addCommand(addCasusBelli);
        addCommand(admin);
        addCommand(casusBelli);
        addCommand(chat);
        addCommand(claim);
        addCommand(create);
        addCommand(createBull);
        addCommand(createVassal);
        addCommand(desc);
        addCommand(disband);
        addCommand(help);
        addCommand(home);
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
        addCommand(world);
        if (FactionsXL.getInstance().getFConfig().isEconomyEnabled()) {
            addCommand(idea);
            addCommand(money);
            addCommand(payday);
            addCommand(storage);
            addCommand(tradeOffer);
        }
    }

}
