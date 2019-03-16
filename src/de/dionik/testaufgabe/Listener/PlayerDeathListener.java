package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.Scoreboards;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private static double percentage = Testaufgabe.getCfg().getDouble("Rewards.PlayerkillsPlayer-Percentage") / 100;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        if (e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            Player killed = e.getEntity().getPlayer();

            CashManager cm = new CashManager();
            double number = cm.getCash(killed) * percentage;

            if (number > 0) {
                cm.addCash(killer, (int) number);
                cm.addGesamtVermögen(killer, (int) number);
                killer.sendMessage(Testaufgabe.getPrefix() + "Du hast §e" + (int) number + " Coins §7für das Töten von §c" + killed.getName() + " §7erhalten!");
                Scoreboards.setScoreboard(killer);
            }
        }
    }
}