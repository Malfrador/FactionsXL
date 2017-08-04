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

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.economy.FAccount;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel Saukel
 */
public class Federation extends LegalEntity {

    FactionsXL plugin = FactionsXL.getInstance();

    public Federation(File file) {
        if (plugin.getFConfig().isEconomyEnabled()) {
            account = new FAccount(this);
        }
    }

    public Set<Faction> getFactions() {
        return new HashSet<>();
    }
    
    @Override
    public String toString() {
        return "Federation{ID=" + id + "name=" + name + "}";
    }

}
