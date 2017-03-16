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
package io.github.dre2n.factionsxl.util;

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Daniel Saukel
 */
public class ItemUtil {

    private static String INTERNALS_VERSION = CompatibilityHandler.getInstance().getInternals().toString();
    private static String ORG_BUKKIT_CRAFTBUKKIT = "org.bukkit.craftbukkit." + INTERNALS_VERSION;
    private static String NET_MINECRAFT_SERVER = "net.minecraft.server." + INTERNALS_VERSION;

    private static Class ITEMSTACK;
    private static Method ITEMSTACK_GET_TAG;
    private static Method ITEMSTACK_SET_TAG;

    private static Class CRAFTITEMSTACK;
    private static Method CRAFTITEMSTACK_AS_BUKKIT_COPY;
    private static Method CRAFTITEMSTACK_AS_NMS_COPY;

    private static Class NBT_BASE;

    private static Class NBT_TAG_COMPOUND;
    private static Method NBT_TAG_COMPOUND_SET;

    private static Class NBT_TAG_LIST;
    private static Method NBT_TAG_LIST_ADD;

    private static Class NBT_TAG_STRING;
    private static Constructor NBT_TAG_STRING_CONSTRUCTOR;

    static {
        try {
            NBT_BASE = Class.forName(NET_MINECRAFT_SERVER + ".NBTBase");

            NBT_TAG_COMPOUND = Class.forName(NET_MINECRAFT_SERVER + ".NBTTagCompound");
            NBT_TAG_COMPOUND_SET = NBT_TAG_COMPOUND.getDeclaredMethod("set", String.class, NBT_BASE);

            NBT_TAG_LIST = Class.forName(NET_MINECRAFT_SERVER + ".NBTTagList");
            NBT_TAG_LIST_ADD = NBT_TAG_LIST.getDeclaredMethod("add", NBT_BASE);

            NBT_TAG_STRING = Class.forName(NET_MINECRAFT_SERVER + ".NBTTagString");
            NBT_TAG_STRING_CONSTRUCTOR = NBT_TAG_STRING.getConstructor(String.class);

            ITEMSTACK = Class.forName(NET_MINECRAFT_SERVER + ".ItemStack");
            ITEMSTACK_GET_TAG = ITEMSTACK.getDeclaredMethod("getTag");
            ITEMSTACK_SET_TAG = ITEMSTACK.getDeclaredMethod("setTag", NBT_TAG_COMPOUND);

            CRAFTITEMSTACK = Class.forName(ORG_BUKKIT_CRAFTBUKKIT + ".inventory.CraftItemStack");
            CRAFTITEMSTACK_AS_BUKKIT_COPY = CRAFTITEMSTACK.getDeclaredMethod("asBukkitCopy", ITEMSTACK);
            CRAFTITEMSTACK_AS_NMS_COPY = CRAFTITEMSTACK.getDeclaredMethod("asNMSCopy", ItemStack.class);

        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException exception) {
        }
    }

    public static final ItemStack SKULL = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

    // Raw custom textured skulls
    public static final ItemStack UP = setSkullOwner(SKULL, "ff1654b0-10f2-48b6-9c05-483b75f6549e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ4Yjc2OGM2MjM0MzJkZmIyNTlmYjNjMzk3OGU5OGRlYzExMWY3OWRiZDZjZDg4ZjIxMTU1Mzc0YjcwYjNjIn19fQ==");
    public static final ItemStack DOWN = setSkullOwner(SKULL, "9afa272b-ca4a-4502-8073-c4be1b741ebc", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhZGQ3NTVkMDg1MzczNTJiZjdhOTNlM2JiN2RkNGQ3MzMxMjFkMzlmMmZiNjcwNzNjZDQ3MWY1NjExOTRkZCJ9fX0=");
    public static final ItemStack LEFT = setSkullOwner(SKULL, "69b9a08d-4e89-4878-8be8-551caeacbf2a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViZjkwNzQ5NGE5MzVlOTU1YmZjYWRhYjgxYmVhZmI5MGZiOWJlNDljNzAyNmJhOTdkNzk4ZDVmMWEyMyJ9fX0=");
    public static final ItemStack RIGHT = setSkullOwner(SKULL, "15f49744-9b61-46af-b1c3-71c6261a0d0e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==");
    public static final ItemStack EXCLAMATION = setSkullOwner(SKULL, "165c8c0f-d1ba-4c9e-9836-cbff5b40ba80", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE1M2JkZDE1NDU1MzFjOWViYjljNmY4OTViYzU3NjAxMmY2MTgyMGU2ZjQ4OTg4NTk4OGE3ZTg3MDlhM2Y0OCJ9fX0=");
    public static final ItemStack QUESTION = setSkullOwner(SKULL, "210665a1-0f17-4353-b85a-426e2cb651e2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE2M2RhZmFjMWQ5MWE4YzkxZGI1NzZjYWFjNzg0MzM2NzkxYTZlMThkOGY3ZjYyNzc4ZmM0N2JmMTQ2YjYifX19");
    public static final ItemStack E = setSkullOwner(SKULL, "96eaca3a-0f22-484f-bad7-46153597e191", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJiMjczN2VjYmY5MTBlZmUzYjI2N2RiN2Q0YjMyN2YzNjBhYmM3MzJjNzdiZDBlNGVmZjFkNTEwY2RlZiJ9fX0=");
    public static final ItemStack I = setSkullOwner(SKULL, "2ab25358-deaa-46b7-b351-d9732a448ff1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxNzhhZDUxZmQ1MmIxOWQwYTM4ODg3MTBiZDkyMDY4ZTkzMzI1MmFhYzZiMTNjNzZlN2U2ZWE1ZDMyMjYifX19");

    public static ItemStack setDisplayName(ItemStack itemStack, String name) {
        itemStack = itemStack.clone();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setSkullOwner(ItemStack itemStack, String id, String textureValue) {
        try {
            Object nmsStack = CRAFTITEMSTACK_AS_NMS_COPY.invoke(null, itemStack);

            Object compound = ITEMSTACK_GET_TAG.invoke(nmsStack);
            if (compound == null) {
                compound = NBT_TAG_COMPOUND.newInstance();
            }

            Object skullOwner = NBT_TAG_COMPOUND.newInstance();
            NBT_TAG_COMPOUND_SET.invoke(skullOwner, "Id", NBT_TAG_STRING_CONSTRUCTOR.newInstance(id));
            Object properties = NBT_TAG_COMPOUND.newInstance();
            Object textures = NBT_TAG_LIST.newInstance();
            Object value = NBT_TAG_COMPOUND.newInstance();
            NBT_TAG_COMPOUND_SET.invoke(value, "Value", NBT_TAG_STRING_CONSTRUCTOR.newInstance(textureValue));
            NBT_TAG_LIST_ADD.invoke(textures, value);
            NBT_TAG_COMPOUND_SET.invoke(properties, "textures", textures);
            NBT_TAG_COMPOUND_SET.invoke(skullOwner, "Properties", properties);

            NBT_TAG_COMPOUND_SET.invoke(compound, "SkullOwner", skullOwner);
            ITEMSTACK_SET_TAG.invoke(nmsStack, compound);
            return (ItemStack) CRAFTITEMSTACK_AS_BUKKIT_COPY.invoke(null, nmsStack);

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            return itemStack;
        }
    }

}
