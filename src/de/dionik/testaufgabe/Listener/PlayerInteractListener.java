package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.GOLD_BLOCK) {
                e.setCancelled(true);

                Inventory inv = Bukkit.createInventory(null, 9 * 6, Testaufgabe.getPrefix() + "Alle Konten | Seite 1");

                ItemStack glas = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
                ItemMeta glasmeta = glas.getItemMeta();
                glasmeta.setDisplayName(" ");
                glas.setItemMeta(glasmeta);

                ItemStack arrowleft = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta arrowleftmeta = (SkullMeta) arrowleft.getItemMeta();
                arrowleftmeta.setDisplayName("§c✘ Vorherige Seite ✘");
                arrowleftmeta.setOwner("MHF_ArrowLeft");
                arrowleft.setItemMeta(arrowleftmeta);

                ItemStack arrowright = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta arrowrightmeta = (SkullMeta) arrowright.getItemMeta();

                if (Testaufgabe.getAccountsMap().get(p) < 46) {
                    arrowrightmeta.setDisplayName("§c✘ Nächste Seite ✘");
                } else {
                    arrowrightmeta.setDisplayName("§a➡ Nächste Seite ➡");
                }
                arrowrightmeta.setOwner("MHF_ArrowRight");
                arrowright.setItemMeta(arrowrightmeta);

                inv.setItem(45, glas);
                inv.setItem(46, glas);
                inv.setItem(48, glas);
                inv.setItem(49, glas);
                inv.setItem(50, glas);
                inv.setItem(52, glas);
                inv.setItem(53, glas);
                inv.setItem(47, arrowleft);
                inv.setItem(51, arrowright);

                ResultSet rs = MySQL.getResultSet("SELECT ID FROM Accounts WHERE UUID = '" + p.getUniqueId().toString() + "'");

                try {
                    int anzahl = 0;
                    while (rs.next()) {
                        String ID = rs.getString("ID");

                        ItemStack konto = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                        SkullMeta kontometa = (SkullMeta) konto.getItemMeta();
                        kontometa.setDisplayName("§6" + ID);
                        kontometa.setOwner("MHF_Exclamation");
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add("§7➤ §aKlicke, um den Kontoauszug zu erhalten");
                        kontometa.setLore(lore);
                        konto.setItemMeta(kontometa);

                        if (anzahl < 45) {
                            inv.setItem(anzahl, konto);
                        }
                        anzahl++;
                    }
                } catch (SQLException ev) {
                    ev.printStackTrace();
                }

                p.openInventory(inv);
            }
        }
    }
}
