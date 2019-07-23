package org.savage.skyblock.island.rules;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;

import java.util.ArrayList;
import java.util.List;

import static org.savage.skyblock.island.upgrades.UpgradesUI.createFadedItem;

public class RuleUI implements Listener {

    @EventHandler
    public void click(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();

        Inventory top = p.getOpenInventory().getTopInventory();

        if (top == null) return;

        String name = e.getView().getTitle();
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig();

        ItemStack clicked = e.getCurrentItem();

        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());

        if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("island-rules.rules-menu.name")))){
            e.setCancelled(true);
            if (island == null) return;
            if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

            for (Rule rule : Rule.values()){
                ItemStack ruleItem = createRuleItem(island, rule);
                if (clicked.equals(ruleItem)){
                    island.modifyRules(rule, !island.hasRule(rule));
                    int slot = e.getSlot();
                    top.setItem(slot, createRuleItem(island, rule));
                    p.updateInventory();
                }
            }
        }
    }

    public static void openRulesUI(Player p){
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig();
        //open the main permission ui to player
        Inventory i = Bukkit.createInventory(null, f.getInt("island-rules.rules-menu.rows")*9, SkyBlock.getInstance().getUtils().color(f.getString("island-rules.rules-menu.name")));
        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());

        for (String s : SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("island-rules.rules-menu.faded-slots").split(",")){
            int slot = Integer.parseInt(s);
            i.setItem(slot -1, createFadedItem());
        }

        for (Rule rule : Rule.values()){
            i.addItem(createRuleItem(island, rule));
        }
        //do faded

        p.openInventory(i);
    }

    private static ItemStack createRuleItem(Island island, Rule rule){
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getRules().getFileConfig();
        String materialName = f.getString("rule-items."+rule.name().toUpperCase()+".item-id");
        int materialData = f.getInt("rule-items."+rule.name().toUpperCase()+".item-data");

        ItemStack temp = SkyBlock.getInstance().getUtils().getMultiVersionItem(materialName, materialData);

        if (temp == null) return null;

        String name = f.getString("rule-items."+rule.name().toUpperCase()+".name");
        List<String> lore = new ArrayList<>();
        //convert the lore
        boolean allow = island.hasRule(rule);

        String allowed = f.getString("placeholders.allowed");
        String denied = f.getString("placeholders.denied");

        boolean glow = f.getBoolean("allowed-glow");
        ItemMeta meta = temp.getItemMeta();

        for (String s : f.getStringList("rule-items."+rule.name().toUpperCase()+".lore")){
            if (s.contains("%status%")){
                if (allow){
                    s = s.replace("%status%", allowed);
                    if (glow){

                        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                       // Glow.addGlow(temp);
                    }
                }else{
                    s = s.replace("%status%", denied);
                }
            }
            lore.add(s);
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(SkyBlock.getInstance().getUtils().color(name));
        meta.setLore(SkyBlock.getInstance().getUtils().color(lore));
        temp.setItemMeta(meta);
        return temp;
    }
}
