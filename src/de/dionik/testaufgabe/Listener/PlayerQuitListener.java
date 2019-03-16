package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Manager.AccountManager;
import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Testaufgabe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        CashManager cm = new CashManager();
        cm.setCash(p.getUniqueId().toString(), cm.getCash(p));

        AccountManager am = new AccountManager();
        am.setAccounts(p.getUniqueId().toString(), am.getAccounts(p));

        e.setQuitMessage(Testaufgabe.getPrefix() + "Der Spieler " + p.getName() + " ist zufrieden mit dem Testplugin von Dionik :)");
    }
}
