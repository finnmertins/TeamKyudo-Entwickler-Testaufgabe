package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Manager.AccountManager;
import de.dionik.testaufgabe.Manager.CashManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKickListener implements Listener {

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Player p = e.getPlayer();

        CashManager cm = new CashManager();
        cm.setCash(p.getUniqueId().toString(), cm.getCash(p));

        AccountManager am = new AccountManager();
        am.setAccounts(p.getUniqueId().toString(), am.getAccounts(p));

        e.setLeaveMessage(null);
    }
}
