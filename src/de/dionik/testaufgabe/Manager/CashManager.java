package de.dionik.testaufgabe.Manager;

import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CashManager {

    public CashManager() {}

    public void loadCash(Player p) {
        if (MySQL.getResult("SELECT UUID FROM Players WHERE UUID = '" + p.getUniqueId().toString() + "'") == null) {
            MySQL.prepareStatement("INSERT INTO Players(UUID, Name, Cash, Accounts) VALUES ('" + p.getUniqueId().toString() + "', '" + p.getName() + "', 0, 0)");
            Testaufgabe.getCashMap().put(p, 0);
        } else {
            int cash = (int) MySQL.getResult("SELECT Cash FROM Players WHERE UUID = '" + p.getUniqueId().toString() + "'");
            Testaufgabe.getCashMap().put(p, cash);
        }
        loadGesamtVermögen(p);
    }

    public void loadGesamtVermögen(Player p) {
        ResultSet rs = MySQL.getResultSet("SELECT Cash FROM Accounts WHERE UUID = '" + p.getUniqueId().toString() + "'");
        int gesamtvermögen = 0;

        try {
            while (rs.next()) {
                gesamtvermögen += rs.getInt("Cash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Testaufgabe.getTotalAssetsMap().put(p, gesamtvermögen + Testaufgabe.getCashMap().get(p));
    }

    public boolean isCached(Player p) {
        return Testaufgabe.getCashMap().containsKey(p);
    }

    public Integer getCash(Player p) {
        return Testaufgabe.getCashMap().get(p);
    }

    public Integer getCash(String uuid) {
        int cash = (int) MySQL.getResult("SELECT Cash FROM Players WHERE UUID = '" + uuid + "'");
        return cash;
    }

    public void setCash(Player p, int pCash) {
        Testaufgabe.getCashMap().put(p, pCash);
    }

    public void setCash(String uuid, int pCash) {
        MySQL.prepareStatement("UPDATE Players SET Cash = '" + pCash + "' WHERE UUID = '" + uuid + "'");
    }

    public void addCash(Player p, int pCash) {
        int cash = getCash(p);
        cash += pCash;

        setCash(p, cash);
    }

    public void addCash(String uuid, int pCash) {
        int cash = (int) MySQL.getResult("SELECT Cash FROM Players WHERE UUID = '" + uuid + "'");

        MySQL.prepareStatement("UPDATE Players SET Cash = '" + (cash + pCash) + "' WHERE UUID = '" + uuid + "'");

    }

    public void removeCash(Player p, int pCash) {
        int cash = getCash(p);
        cash -= pCash;

        setCash(p, cash);
    }

    public void removeCash(String uuid, int pCash) {
        int cash = (int) MySQL.getResult("SELECT Cash FROM Players WHERE UUID = '" + uuid + "'");

        MySQL.prepareStatement("UPDATE Players SET Cash = '" + (cash - pCash) + "' WHERE UUID = '" + uuid + "'");
    }

    public void addGesamtVermögen(Player p, int pCash) {
        int gesamtvermögen =  Testaufgabe.getTotalAssetsMap().get(p);
        Testaufgabe.getTotalAssetsMap().put(p, gesamtvermögen + pCash);
    }

    public void removeGesamtVermögen(Player p, int pCash) {
        int gesamtvermögen =  Testaufgabe.getTotalAssetsMap().get(p);
        Testaufgabe.getTotalAssetsMap().put(p, gesamtvermögen - pCash);
    }

    public static void updateCashInMySQL() {
        CashManager cm = new CashManager();

        for(Player p : Bukkit.getOnlinePlayers()) {
            cm.setCash(p.getUniqueId().toString(), cm.getCash(p));
        }
    }

    public void addPlayersWithCashInHashMap() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            loadCash(p);
        }
    }
}