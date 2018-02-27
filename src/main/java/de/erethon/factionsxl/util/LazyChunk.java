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
package de.erethon.factionsxl.util;

import java.lang.ref.WeakReference;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * @author Daniel Saukel
 */
public class LazyChunk {

    private int x;
    private int z;

    private WeakReference<Chunk> bukkit;

    public LazyChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public LazyChunk(Chunk chunk) {
        x = chunk.getX();
        z = chunk.getZ();
    }

    public LazyChunk(String string) {
        try {
            String[] args = string.split(",");
            int i = args.length > 2 ? 1 : 0;
            x = Integer.parseInt(args[i]);
            z = Integer.parseInt(args[i + 1]);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /* Getters and setters */
    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Chunk asBukkitChunk(World world) {
        Chunk chunk = null;
        if (bukkit == null) {
            chunk = world.getChunkAt(x, z);
            bukkit = new WeakReference<>(chunk);
        } else {
            chunk = bukkit.get();
        }
        return chunk;
    }

    @Override
    public String toString() {
        return x + "," + z;
    }

}
