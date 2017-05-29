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

import static io.github.dre2n.commons.item.ItemUtil.setSkullOwner;
import io.github.dre2n.factionsxl.config.FMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * @author Daniel Saukel
 */
public class GUIButton {

    /* Raw skulls */
    public static final ItemStack SKULL = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    public static final ItemStack UP_ALT = setSkullOwner(SKULL, "e4d7b07b-59fc-4f77-b08b-b0446048dcd4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThmZTI1MWE0MGU0MTY3ZDM1ZDA4MWMyNzg2OWFjMTUxYWY5NmI2YmQxNmRkMjgzNGQ1ZGM3MjM1ZjQ3NzkxZCJ9fX0=");
    public static final ItemStack DOWN_ALT = setSkullOwner(SKULL, "ccd469f7-1df1-42f9-8915-15de387906e4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI3Y2U2ODNkMDg2OGFhNDM3OGFlYjYwY2FhNWVhODA1OTZiY2ZmZGFiNmI1YWYyZDEyNTk1ODM3YTg0ODUzIn19fQ==");
    public static final ItemStack UP = setSkullOwner(SKULL, "ff1654b0-10f2-48b6-9c05-483b75f6549e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ4Yjc2OGM2MjM0MzJkZmIyNTlmYjNjMzk3OGU5OGRlYzExMWY3OWRiZDZjZDg4ZjIxMTU1Mzc0YjcwYjNjIn19fQ==");
    public static final ItemStack DOWN = setSkullOwner(SKULL, "9afa272b-ca4a-4502-8073-c4be1b741ebc", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhZGQ3NTVkMDg1MzczNTJiZjdhOTNlM2JiN2RkNGQ3MzMxMjFkMzlmMmZiNjcwNzNjZDQ3MWY1NjExOTRkZCJ9fX0=");
    public static final ItemStack LEFT = setSkullOwner(SKULL, "69b9a08d-4e89-4878-8be8-551caeacbf2a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViZjkwNzQ5NGE5MzVlOTU1YmZjYWRhYjgxYmVhZmI5MGZiOWJlNDljNzAyNmJhOTdkNzk4ZDVmMWEyMyJ9fX0=");
    public static final ItemStack RIGHT = setSkullOwner(SKULL, "15f49744-9b61-46af-b1c3-71c6261a0d0e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==");
    public static final ItemStack EXCLAMATION = setSkullOwner(SKULL, "165c8c0f-d1ba-4c9e-9836-cbff5b40ba80", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE1M2JkZDE1NDU1MzFjOWViYjljNmY4OTViYzU3NjAxMmY2MTgyMGU2ZjQ4OTg4NTk4OGE3ZTg3MDlhM2Y0OCJ9fX0=");
    public static final ItemStack QUESTION = setSkullOwner(SKULL, "210665a1-0f17-4353-b85a-426e2cb651e2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE2M2RhZmFjMWQ5MWE4YzkxZGI1NzZjYWFjNzg0MzM2NzkxYTZlMThkOGY3ZjYyNzc4ZmM0N2JmMTQ2YjYifX19");
    public static final ItemStack E = setSkullOwner(SKULL, "96eaca3a-0f22-484f-bad7-46153597e191", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJiMjczN2VjYmY5MTBlZmUzYjI2N2RiN2Q0YjMyN2YzNjBhYmM3MzJjNzdiZDBlNGVmZjFkNTEwY2RlZiJ9fX0=");
    public static final ItemStack I = setSkullOwner(SKULL, "2ab25358-deaa-46b7-b351-d9732a448ff1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxNzhhZDUxZmQ1MmIxOWQwYTM4ODg3MTBiZDkyMDY4ZTkzMzI1MmFhYzZiMTNjNzZlN2U2ZWE1ZDMyMjYifX19");
    public static final ItemStack SOLDIER = setSkullOwner(SKULL, "f59b9e38-8c9d-4917-8d9e-6b5aad457ec5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQ1MzAyNzY3NDE0ODUyNzM4MDFlMWIxMWYzYTNmZGNmMGRkMjA2ZjE0NTI1NWRmNGY1YTIwMjAyOGEwNjMifX19");

    /* GUI buttons */
    public static final ItemStack BACK = setDisplayName(LEFT, FMessage.MISC_BACK.getMessage());
    public static final ItemStack CONTINUE = setDisplayName(RIGHT, FMessage.MISC_CONTINUE.getMessage());
    public static final ItemStack NEXT_PAGE = setDisplayName(RIGHT, FMessage.MISC_NEXT_PAGE.getMessage());
    public static final ItemStack PREVIOUS_PAGE = setDisplayName(LEFT, FMessage.MISC_PREVIOUS_PAGE.getMessage());
    public static final ItemStack PLACEHOLDER = setDisplayName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), ChatColor.RESET.toString());
    public static final ItemStack DISABLED = setDisplayName(new ItemStack(Material.BARRIER), FMessage.ERROR_ECON_DISABLED.getMessage());

    /* Blank items that show meta stuff by default */
    public static final ItemStack GUI_SWORD = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack GUI_WATER_BOTTLE = new ItemStack(Material.POTION);

    static {
        ItemMeta swordMeta = GUI_SWORD.getItemMeta();
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        GUI_SWORD.setItemMeta(swordMeta);

        PotionMeta watMeta = (PotionMeta) GUI_WATER_BOTTLE.getItemMeta();
        watMeta.setBasePotionData(new PotionData(PotionType.WATER));
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

}
