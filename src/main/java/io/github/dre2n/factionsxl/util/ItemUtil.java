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

import io.github.dre2n.factionsxl.config.FMessage;
import net.minecraft.server.v1_11_R1.Item;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * @author Daniel Saukel
 */
public class ItemUtil {

    public static final ItemStack GUI_SWORD = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack GUI_WATER_BOTTLE = CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_11_R1.ItemStack(Item.getById(373)));
    public static final ItemStack DISABLED = setDisplayName(new ItemStack(Material.BARRIER), FMessage.ERROR_ECON_DISABLED.getMessage());
    public static final ItemStack SKULL = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    public static final ItemStack UP = /*SKULL.clone();*/ setSkullOwner(SKULL, "ff1654b0-10f2-48b6-9c05-483b75f6549e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ4Yjc2OGM2MjM0MzJkZmIyNTlmYjNjMzk3OGU5OGRlYzExMWY3OWRiZDZjZDg4ZjIxMTU1Mzc0YjcwYjNjIn19fQ==");
    public static final ItemStack DOWN = /*SKULL.clone();*/ setSkullOwner(SKULL, "9afa272b-ca4a-4502-8073-c4be1b741ebc", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhZGQ3NTVkMDg1MzczNTJiZjdhOTNlM2JiN2RkNGQ3MzMxMjFkMzlmMmZiNjcwNzNjZDQ3MWY1NjExOTRkZCJ9fX0=");
    public static final ItemStack LEFT = /*SKULL.clone();*/ setSkullOwner(SKULL, "69b9a08d-4e89-4878-8be8-551caeacbf2a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViZjkwNzQ5NGE5MzVlOTU1YmZjYWRhYjgxYmVhZmI5MGZiOWJlNDljNzAyNmJhOTdkNzk4ZDVmMWEyMyJ9fX0=");
    public static final ItemStack RIGHT = /*SKULL.clone();*/ setSkullOwner(SKULL, "15f49744-9b61-46af-b1c3-71c6261a0d0e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==");
    public static final ItemStack EXCLAMATION = SKULL.clone();
    public static final ItemStack GUI_BACK = LEFT.clone();
    public static final ItemStack PLACEHOLDER = setDisplayName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), ChatColor.RESET.toString());
    public static final ItemStack QUESTION = SKULL.clone();
    public static final ItemStack E = setSkullOwner(SKULL, "96eaca3a-0f22-484f-bad7-46153597e191", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJiMjczN2VjYmY5MTBlZmUzYjI2N2RiN2Q0YjMyN2YzNjBhYmM3MzJjNzdiZDBlNGVmZjFkNTEwY2RlZiJ9fX0=");
    public static final ItemStack I = setSkullOwner(SKULL, "2ab25358-deaa-46b7-b351-d9732a448ff1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxNzhhZDUxZmQ1MmIxOWQwYTM4ODg3MTBiZDkyMDY4ZTkzMzI1MmFhYzZiMTNjNzZlN2U2ZWE1ZDMyMjYifX19");

    static {
        /*SkullMeta upMeta = (SkullMeta) UP.getItemMeta();
        upMeta.setOwner("MHF_ArrowUp");
        UP.setItemMeta(upMeta);
        SkullMeta downMeta = (SkullMeta) DOWN.getItemMeta();
        downMeta.setOwner("MHF_ArrowDown");
        DOWN.setItemMeta(downMeta);
        SkullMeta leftMeta = (SkullMeta) LEFT.getItemMeta();
        leftMeta.setOwner("MHF_ArrowLeft");
        LEFT.setItemMeta(leftMeta);
        SkullMeta rightMeta = (SkullMeta) RIGHT.getItemMeta();
        rightMeta.setOwner("MHF_ArrowRight");
        RIGHT.setItemMeta(rightMeta);*/
        SkullMeta exMeta = (SkullMeta) EXCLAMATION.getItemMeta();
        exMeta.setOwner("MHF_Exclamation");
        EXCLAMATION.setItemMeta(exMeta);
        SkullMeta quMeta = (SkullMeta) QUESTION.getItemMeta();
        quMeta.setOwner("MHF_Question");
        QUESTION.setItemMeta(quMeta);
        ItemMeta backMeta = ItemUtil.GUI_BACK.getItemMeta();
        backMeta.setDisplayName(FMessage.MISC_BACK.getMessage());
        GUI_BACK.setItemMeta(backMeta);

        ItemMeta swordMeta = GUI_SWORD.getItemMeta();
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        GUI_SWORD.setItemMeta(swordMeta);

        ItemMeta watMeta = GUI_WATER_BOTTLE.getItemMeta();
        watMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        GUI_WATER_BOTTLE.setItemMeta(watMeta);
    }

    public static ItemStack setDisplayName(ItemStack itemStack, String name) {
        itemStack = itemStack.clone();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setSkullOwner(ItemStack itemStack, String id, String textureValue) {
        net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.set("Id", new NBTTagString(id));
        NBTTagCompound properties = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.set("Value", new NBTTagString(textureValue));
        textures.add(value);
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);

        compound.set("SkullOwner", skullOwner);
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

}
