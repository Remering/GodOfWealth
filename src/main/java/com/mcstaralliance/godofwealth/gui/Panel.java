package com.mcstaralliance.godofwealth.gui;

import com.mcstaralliance.godofwealth.GodOfWealth;
import com.mcstaralliance.godofwealth.util.ConfigUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Dye;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Panel implements InventoryHolder {

    private final String title = ConfigUtil.color(GodOfWealth.getInstance().getConfig().getString("lang.menu-title"));
    private final Inventory inv = Bukkit.createInventory(this, 3 * 9, title);

    public Panel() {
        setBorder();
        setInfoButton();
    }

    public void setBorder() {
        String borderGlassPlainName = GodOfWealth.getInstance().getConfig().getString("lang.border-glass-name");
        String borderGlassName = ConfigUtil.color(borderGlassPlainName);
        ItemStack borderGlass = createOrangeStainedPane(Material.STAINED_GLASS_PANE, borderGlassName);
        arrangeBorder(borderGlass);
    }

    public void arrangeBorder(ItemStack border) {
        for (int i = 0; i < 27; i++) {
            ItemStack itemStack;
            if (i <= 8 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                itemStack = border;
            } else {
                itemStack = null;
            }
            inv.setItem(i, itemStack);
        }
    }

    public void setInfoButton() {
        String headName = ConfigUtil.color(GodOfWealth.getInstance().getConfig().getString("lang.head-name"));
        UUID uuid = UUID.fromString(GodOfWealth.getInstance().getConfig().getString("lucky-player"));
        ItemStack head = createHead(Material.SKULL_ITEM, headName, uuid);
        inv.setItem(13, head);
    }

    public String getLuckyPlayer() {
        if (!GodOfWealth.getInstance().getConfig().getString("lucky-player-real-name").equals("")) {
            return GodOfWealth.getInstance().getConfig().getString("lucky-player-real-name");
        } else {
            return "无";
        }
    }

    public ItemStack createHead(Material material, String name, UUID uuid) {
        ItemStack skull = new ItemStack(material, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        Player player = Bukkit.getPlayer(uuid);
        List<String> loreBeforeNextSelection = GodOfWealth.getInstance().getConfig().getStringList("lang.info-lore-before-next-selection").stream()
                .map(ConfigUtil::color)
                .map(s -> s.replaceAll("%gow_player%", getLuckyPlayer()))
                .map(s -> PlaceholderAPI.setPlaceholders(player, s))
                .collect(Collectors.toList());
        List<String> loreAfterLastSelection = GodOfWealth.getInstance().getConfig().getStringList("lang.info-lore-after-last-selection").stream()
                .map(ConfigUtil::color)
                .map(s -> s.replaceAll("%gow_player%", getLuckyPlayer()))
                .map(s -> PlaceholderAPI.setPlaceholders(player, s))
                .collect(Collectors.toList());
        int selectionHour = GodOfWealth.getInstance().getConfig().getInt("selection.time");
        LocalTime now = LocalTime.now();
        if (now.getHour() < selectionHour) {
            skullMeta.setLore(loreAfterLastSelection);
        } else {
            skullMeta.setLore(loreBeforeNextSelection);
        }
        skullMeta.setDisplayName(name);
        skull.setItemMeta(skullMeta);
        return skull;
    }


    public ItemStack createOrangeStainedPane(Material material, String name) {
        Dye dye = new Dye();
        dye.setColor(DyeColor.ORANGE);
        ItemStack item = dye.toItemStack(1);
        item.setType(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }


    public Inventory getInventory() {
        return inv;
    }

}
