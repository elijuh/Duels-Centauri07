package me.elijuh.duels.objects;

import lombok.Getter;
import me.elijuh.duels.Core;
import me.elijuh.duels.utils.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Kit {
    @Getter
    private final String name;
    private final Map<Integer, ItemStack> contents = new HashMap<>();

    public Kit(String name, ConfigurationSection section) {
        this.name = name;
        for (String key : section.getKeys(false)) {
            try {
                ConfigurationSection itemSection = section.getConfigurationSection(key);
                if (itemSection == null) {
                    throw new IllegalStateException("must be a configuration section: " + key);
                }
                int slot = Integer.parseInt(key);
                Material material = Material.getMaterial(itemSection.getString("material", ""));
                if (material == null) {
                    throw new IllegalArgumentException("Material not found: " + itemSection.getString("material"));
                }
                ItemStack item = new ItemStack(material);
                if (itemSection.contains("amount")) {
                    item.setAmount(itemSection.getInt("amount"));
                }
                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    throw new IllegalStateException("item meta cannot be null");
                }
                if (itemSection.contains("name")) {
                    meta.setDisplayName(Text.color(itemSection.getString("name")));
                }
                if (itemSection.contains("lore")) {
                    List<String> lore = itemSection.getStringList("lore")
                            .stream()
                            .map(Text::color)
                            .collect(Collectors.toList());
                    meta.setLore(lore);
                }
                ConfigurationSection enchants = itemSection.getConfigurationSection("enchants");
                if (enchants != null) {
                    for (String enchant : enchants.getKeys(false)) {
                        Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft(enchant));
                        if (e == null) {
                            Core.i().getLogger().warning("invalid enchant id in kit "  + itemSection.getName() + ": " + enchant);
                            continue;
                        }
                        meta.addEnchant(e, enchants.getInt(enchant), true);
                    }
                }
                if (itemSection.contains("itemFlags")) {
                    for (String flag : itemSection.getStringList("itemFlags")) {
                        meta.addItemFlags(ItemFlag.valueOf(flag));
                    }
                }
                if (itemSection.contains("unbreakable")) {
                    meta.setUnbreakable(itemSection.getBoolean("unbreakable"));
                }
                contents.put(slot, item);
            } catch (Exception e) {
                Core.i().getLogger().warning("Error while parsing item for kit " + section.getName() + ": " + e.getMessage());
            }
        }
    }

    public void apply(Player p) {
        contents.forEach((slot, item) -> p.getInventory().setItem(slot, item.clone()));
    }
}
