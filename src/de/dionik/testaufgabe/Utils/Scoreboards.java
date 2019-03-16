package de.dionik.testaufgabe.Utils;

import com.google.common.collect.Lists;
import de.dionik.testaufgabe.Manager.AccountManager;
import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Testaufgabe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.List;

public class Scoreboards {

    public static void setScoreboard(Player p) {
        //if(p.getScoreboard() == null) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        //}
        Scoreboard board = p.getScoreboard();
        Objective obj;

        if (board.getObjective(p.getName()) != null) {
            board.getObjective(p.getName()).unregister();
        }
        obj = board.registerNewObjective(p.getName(), "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§3TeamKyudo");

        CashManager cm = new CashManager();

        if (!cm.isCached(p)) {
            cm.loadCash(p);
        }

        AccountManager am = new AccountManager();

        if (!am.isCached(p)) {
            am.loadAccounts(p);
        }

        int bargeld = cm.getCash(p);
        int konten = am.getAccounts(p);
        int gesamtvermögen = Testaufgabe.getTotalAssetsMap().get(p);

        List<String> Scores = Lists.reverse(Arrays.asList("§1", "§8➤ §cBargeld", bargeld + "€",
                "§2", "§8➤ §aKonten", "" + konten,
                "§3", "§8➤ §bGesamtvermögen", "§f" + gesamtvermögen + "€"));
        for (Integer i = 0; i < Scores.size(); i++) {
            obj.getScore(Scores.get(i)).setScore(i);
        }
        p.setScoreboard(board);
    }
}