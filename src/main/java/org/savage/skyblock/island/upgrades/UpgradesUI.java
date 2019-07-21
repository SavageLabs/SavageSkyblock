package org.savage.skyblock.island.upgrades;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.savage.skyblock.API.IslandUpgradeEvent;
import org.savage.skyblock.Materials;
import org.savage.skyblock.Placeholder;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UpgradesUI implements Listener {

    public static ItemStack createFadedItem(){
        String id = SkyBlock.getInstance().getConfig().getString("settings.faded-item.item-id");
        int data = SkyBlock.getInstance().getConfig().getInt("settings.faded-item.item-data");

        ItemStack item = Materials.requestXMaterial(id, (byte)data).parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SkyBlock.getInstance().getUtils().color("&8"));
        meta.setLore(new ArrayList<>());
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void upgradesClick(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();
        Inventory top = p.getOpenInventory().getTopInventory();
        Inventory bottom = p.getOpenInventory().getBottomInventory();
        String iName = e.getView().getTitle();
        Inventory i = e.getClickedInventory();

       // if (top != null && top.getTitle().equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().upgrades.getFileConfig().getString("upgrades-UI.gui-name")))){
        if (top != null && iName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("upgrades-UI.gui-name")))){
            e.setCancelled(true);
            if (i != null && i.equals(top)){
                ItemStack clicked = e.getCurrentItem();
                Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());

                if (clicked != null && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()){
                    String name = clicked.getItemMeta().getDisplayName();


                    double money = SkyBlock.getInstance().getUtils().getBalance(p.getUniqueId());

                    if (island != null) {
                        YamlConfiguration f = SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig();
                        for (Upgrade upgrade : Upgrade.values()) {
                            String upgradeName = upgrade.getName();
                            if (f.getConfigurationSection("upgrades."+upgradeName) == null) continue;
                            //int upgradeID = upgrade.getId();
                            if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("upgrades."+upgradeName+".name")))) {
                                int currentTier = island.getUpgradeTier(upgrade);
                                int maxTier = Upgrade.Upgrades.getMaxTier(upgrade);
                                int nextTier = Math.addExact(currentTier, 1);
                                double tierCost = f.getDouble("upgrades."+upgradeName+".tiers."+nextTier+".cost");
                                //int upgradeValue = f.getInt("upgrades."+upgradeName+".tiers."+nextTier+".upgrade-value");

                                if (currentTier >= maxTier) {
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("upgrade_max"));
                                    p.closeInventory();

                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            openUpgradesUI(p);
                                        }
                                    }.runTaskLater(SkyBlock.getInstance(), 10L);
                                    return;
                                }

                                if (money >= tierCost){
                                    //upgrade it
                                    IslandUpgradeEvent event = new IslandUpgradeEvent(upgrade, nextTier,island, p);
                                    Bukkit.getServer().getPluginManager().callEvent(event);

                                    if (!event.isCancelled()) {
                                        island.setUpgradeTier(event.getUpgrade(), event.getNewTier());
                                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("upgrade_purchased").replace("%upgrade%", SkyBlock.getInstance().getUtils().capitalizeFirstLetter(event.getUpgrade().getName().replace("-", " "))).replace("%tier%", event.getNewTier()+""));

                                        for (UUID uuid : island.getAllPlayers()){
                                            if (Bukkit.getPlayer(uuid) != null && !uuid.equals(p.getUniqueId())){
                                                Bukkit.getPlayer(uuid).sendMessage(SkyBlock.getInstance().getUtils().getMessage("upgrade_purchased_everyone").replace("%player%", p.getName()).replace("%upgrade%", SkyBlock.getInstance().getUtils().capitalizeFirstLetter(event.getUpgrade().getName().replace("-", " "))).replace("%tier%", event.getNewTier()+""));
                                            }
                                        }

                                        SkyBlock.getInstance().getUtils().takeMoney(p.getUniqueId(), tierCost);
                                        p.closeInventory();

                                        new BukkitRunnable(){
                                            @Override
                                            public void run() {
                                                openUpgradesUI(p);
                                            }
                                        }.runTaskLater(SkyBlock.getInstance(), 10L);
                                        return;
                                    }
                                }else{
                                    p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("upgrade_denied"));
                                    p.closeInventory();

                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            openUpgradesUI(p);
                                        }
                                    }.runTaskLater(SkyBlock.getInstance(), 10L);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void openUpgradesUI(Player p){
        Inventory i = Bukkit.createInventory(null, SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getInt("upgrades-UI.rows")*9,
                SkyBlock.getInstance().getUtils().color(SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("upgrades-UI.gui-name")));

        for (String s : SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig().getString("upgrades-UI.faded-slots").split(",")){
            int slot = Integer.parseInt(s);
            i.setItem(slot -1, createFadedItem());
        }

        YamlConfiguration f = SkyBlock.getInstance().getFileManager().getUpgrades().getFileConfig();

        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());

        if (island != null) {
            for (Upgrade upgrade : Upgrade.values()) {
                String upgradeName = upgrade.getName();
                if (f.getConfigurationSection("upgrades." + upgradeName) != null) {
                    boolean enabled = f.getBoolean("upgrades."+upgradeName+".enabled");
                    if (!enabled) continue;
                    int slot = f.getInt("upgrades." + upgradeName + ".slot");
                    String itemID = f.getString("upgrades." + upgradeName + ".item-id");
                    int itemData = f.getInt("upgrades." + upgradeName + ".item-data");
                    int itemAmount = f.getInt("upgrades." + upgradeName + ".item-amount");
                    String name = f.getString("upgrades." + upgradeName + ".name");
                    List<String> lore = f.getStringList("upgrades." + upgradeName + ".lore");

                    if (upgrade.equals(Upgrade.GENERATOR)){
                        lore = Placeholder.convertPlaceholders(lore, island, upgrade);
                    }else {
                        List<String> l = new ArrayList<>();
                        for (String s : lore) {
                            l.add(Placeholder.convertPlaceholders(s, island, upgrade));
                        }
                        lore = l;
                    }

                    ItemStack item = SkyBlock.getInstance().getUtils().createItem(itemID, itemData, name, lore, itemAmount);

                    i.setItem(slot - 1, item);
                }
            }
        }
        p.openInventory(i);
    }
}