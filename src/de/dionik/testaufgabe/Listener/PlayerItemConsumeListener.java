package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.Scoreboards;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsumeListener implements Listener {

    private static int reward = Testaufgabe.getCfg().getInt("Rewards.EatMeat");

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();

        if (e.getItem().getType() == Material.PORK
                || e.getItem().getType() == Material.GRILLED_PORK
                || e.getItem().getType() == Material.RAW_BEEF
                || e.getItem().getType() == Material.COOKED_BEEF
                || e.getItem().getType() == Material.ROTTEN_FLESH
                || e.getItem().getType() == Material.RABBIT
                || e.getItem().getType() == Material.COOKED_RABBIT
                || e.getItem().getType() == Material.MUTTON
                || e.getItem().getType() == Material.COOKED_MUTTON) {

            CashManager cm = new CashManager();

            if (cm.getCash(p) >= reward) {
                cm.removeCash(p, reward);
                cm.removeGesamtVermögen(p, reward);
                p.sendMessage(Testaufgabe.getPrefix() + "Du hast §e" + reward + " Coins §7für das Essen von Fleisch abgezogen bekommen!");
                Scoreboards.setScoreboard(p);
            } else {
                p.sendMessage(Testaufgabe.getPrefix() + "Du hast §e" + cm.getCash(p) + " Coins §7für das Essen von Fleisch abgezogen bekommen!");
                cm.removeCash(p, cm.getCash(p));
                cm.removeGesamtVermögen(p, cm.getCash(p));
                Scoreboards.setScoreboard(p);
            }
        }
    }
}
