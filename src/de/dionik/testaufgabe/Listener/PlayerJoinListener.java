package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.Scoreboards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Scoreboards.setScoreboard(p);

        e.setJoinMessage(Testaufgabe.getPrefix() + "Der Spieler " + p.getName() + " testet nun die Aufgabe von Dionik in §eVersion " + Testaufgabe.getInstance().getDescription().getVersion() + "§7.");
    }
}
