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
package io.github.dre2n.factionsxl.player;

import io.github.dre2n.factionsxl.FactionsXL;
import io.github.dre2n.factionsxl.config.FData;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class AsyncPowerTask extends BukkitRunnable {

    FData data;

    private double increaseRate;
    private double decreaseRate;
    private int maxPower;

    public AsyncPowerTask(FactionsXL plugin, double increaseRate, double decreaseRate, int maxPower) {
        data = plugin.getFData();
        this.increaseRate = increaseRate;
        this.decreaseRate = decreaseRate;
        this.maxPower = maxPower;
    }

    @Override
    public void run() {
        Map<UUID, Double> updatedPower = new HashMap<>();

        for (Entry<UUID, Double> entry : data.power.entrySet()) {
            UUID uuid = entry.getKey();
            double power = entry.getValue();
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) {
                continue;
            }
            if (power > 0) {
                power -= decreaseRate;
            }
            if (power < 0 || power > 1) {
                updatedPower.put(uuid, power);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            double power = data.power.get(uuid) != null ? data.power.get(uuid) : 0;
            power += increaseRate;
            if (power > maxPower) {
                power = maxPower;
            }
            updatedPower.put(uuid, power);
        }

        data.power = updatedPower;
        data.lastPowerUpdate = System.currentTimeMillis();
        FactionsXL.debug("Updated power values");
    }

}
