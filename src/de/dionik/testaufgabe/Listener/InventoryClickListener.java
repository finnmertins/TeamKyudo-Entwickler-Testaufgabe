package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Testaufgabe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getInventory().getTitle().startsWith(Testaufgabe.getPrefix() + "Alle Konten | Seite ")) {
            e.setCancelled(true);

            if (e.getCurrentItem().getType() == Material.SKULL_ITEM) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c✘ Vorherige Seite ✘")) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Du kannst die vorherige Seite nicht öffnen, da sie nicht existiert!");
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c✘ Nächste Seite ✘")) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Du kannst die nächste Seite nicht öffnen, da sie nicht existiert!");
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§6")) {
                    Bukkit.dispatchCommand(p, "account transactions " + e.getCurrentItem().getItemMeta().getDisplayName().replace("§6", ""));
                    p.closeInventory();
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("a➡ Nächste Seite ➡")) {
                    //loadPage();
                }
            }
        }
    }
}
