package org.savage.skyblock.island.permissions;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.savage.skyblock.SkyBlock;
import org.savage.skyblock.island.Island;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.savage.skyblock.island.upgrades.UpgradesUI.createFadedItem;

public class PermissionUI implements Listener {

    private static HashMap<UUID, Role> roleMap = new HashMap<>();

    @EventHandler
    public void close(InventoryCloseEvent e){
        Player p = (Player)e.getPlayer();
        roleMap.remove(p.getUniqueId());
    }

    @EventHandler
    public void click(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();

        Inventory top = p.getOpenInventory().getTopInventory();

        if (top == null) return;

        String name = e.getView().getTitle();
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig();

        ItemStack clicked = e.getCurrentItem();

        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());

        if (roleMap.containsKey(p.getUniqueId())){
            e.setCancelled(true);
            if (island == null) return;
            if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

            Role role = roleMap.get(p.getUniqueId());
            if (role == null) return;

            for (Perm perm : Perm.values()){
                ItemStack permItem = createPermItem(island, role, perm);
                if (clicked.equals(permItem)) {
                    if (island.hasPerm(SkyBlock.getInstance().getIslandUtils().getRole(p.getUniqueId(), island), perm)) {
                        //toggle it on
                        island.modifyPerms(role, perm, !island.hasPerm(role, perm));
                        //update the invy for the player...
                        ItemStack newItem = createPermItem(island, role, perm);
                        int slot = e.getSlot();
                        top.setItem(slot, newItem);
                        p.updateInventory();
                    }else{
                        p.sendMessage(SkyBlock.getInstance().getUtils().getMessage("noPermissionIsland"));
                    }
                }
            }
            return;
        }

        if (name.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(f.getString("island-perms.perms-menu.name")))){
            e.setCancelled(true);
            if (island == null) return;
            if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

            String clickName = clicked.getItemMeta().getDisplayName();

            String visitor = f.getString("island-perms.perms-menu.items.visitor.name");
            String member = f.getString("island-perms.perms-menu.items.member.name");
            String officer = f.getString("island-perms.perms-menu.items.officer.name");
            String coowner = f.getString("island-perms.perms-menu.items.coowner.name");

            if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(visitor))){
                openPermissionRoleUI(p, Role.VISITOR);
            }
            if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(member))){
                openPermissionRoleUI(p, Role.MEMBER);
            }
            if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(officer))){
                openPermissionRoleUI(p, Role.OFFICER);
            }
            if (clickName.equalsIgnoreCase(SkyBlock.getInstance().getUtils().color(coowner))){
                openPermissionRoleUI(p, Role.COOWNER);
            }
        }
    }

    public static void openPermissionUI(Player p){
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig();
        //open the main permission ui to player
        Inventory i = Bukkit.createInventory(null, f.getInt("island-perms.perms-menu.rows")*9, SkyBlock.getInstance().getUtils().color(f.getString("island-perms.perms-menu.name")));

        for (String s : SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("island-perms.perms-menu.faded-slots").split(",")){
            int slot = Integer.parseInt(s);
            i.setItem(slot -1, createFadedItem());
        }

        for (Role role : Role.values()){
            if (role.equals(Role.OWNER)) continue;
            String roleName = role.name().toLowerCase();

            String materialID = f.getString("island-perms.perms-menu.items."+roleName+".item-id");
            int materialData = f.getInt("island-perms.perms-menu.items."+roleName+".item-data");

            ItemStack itemTemp = SkyBlock.getInstance().getUtils().getMultiVersionItem(materialID, materialData);

            ItemMeta meta = itemTemp.getItemMeta();

            String name = f.getString("island-perms.perms-menu.items."+roleName+".name");
            List<String> lore = f.getStringList("island-perms.perms-menu.items."+roleName+".lore");
            meta.setDisplayName(SkyBlock.getInstance().getUtils().color(name));
            meta.setLore(SkyBlock.getInstance().getUtils().color(lore));
            itemTemp.setItemMeta(meta);

            int slot = f.getInt("island-perms.perms-menu.items."+roleName+".slot");
            i.setItem(slot - 1, itemTemp);
        }
        //do faded

        p.openInventory(i);
    }

    public static void openPermissionRoleUI(Player p, Role role){
        //open specified role permission ui to player
        roleMap.remove(p.getUniqueId());

        if (role.equals(Role.OWNER)) return;

        String roleName = role.name().toLowerCase();

        FileConfiguration f1 = SkyBlock.getInstance().getFileManager().getGuis().getFileConfig();

        Island island = SkyBlock.getInstance().getIslandUtils().getIsland(p.getUniqueId());

        Inventory i = Bukkit.createInventory(null, f1.getInt("island-perms.perms-menu."+roleName+".rows")*9, SkyBlock.getInstance().getUtils().color(f1.getString("island-perms.perms-menu."+roleName+".name")));

        for (String s : SkyBlock.getInstance().getFileManager().getGuis().getFileConfig().getString("island-perms.perms-menu."+roleName+".faded-slots").split(",")){
            int slot = Integer.parseInt(s);
            i.setItem(slot -1, createFadedItem());
        }

        for (Perm perm : Perm.values()){
            ItemStack item = createPermItem(island, role, perm);
            i.addItem(item);
        }

        p.closeInventory();
        p.openInventory(i);

        roleMap.put(p.getUniqueId(), role);

    }

    private static ItemStack createPermItem(Island island, Role role, Perm perm){
        FileConfiguration f = SkyBlock.getInstance().getFileManager().getPermissions().getFileConfig();
        String materialName = f.getString("permission-items."+perm.name().toUpperCase()+".item-id");
        int materialData = f.getInt("permission-items."+perm.name().toUpperCase()+".item-data");

        ItemStack temp = SkyBlock.getInstance().getUtils().getMultiVersionItem(materialName, materialData);

        if (temp == null) return null;

        String name = f.getString("permission-items."+perm.name().toUpperCase()+".name");
        List<String> lore = new ArrayList<>();
        //convert the lore
        boolean allow = island.hasPerm(role, perm);

        String allowed = f.getString("placeholders.allowed");
        String denied = f.getString("placeholders.denied");

        boolean glow = f.getBoolean("allowed-glow");

        ItemMeta meta = temp.getItemMeta();

        for (String s : f.getStringList("permission-items."+perm.name().toUpperCase()+".lore")){
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