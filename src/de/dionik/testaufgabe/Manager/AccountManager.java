package de.dionik.testaufgabe.Manager;

import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.MySQL;
import de.dionik.testaufgabe.Utils.Scoreboards;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

public class AccountManager {

    public AccountManager() {}

    public void loadAccounts(Player p) {
            int accounts = (int) MySQL.getResult("SELECT Accounts FROM Players WHERE UUID = '" + p.getUniqueId().toString() + "'");
            Testaufgabe.getAccountsMap().put(p, accounts);
    }

    public boolean isCached(Player p) {
        return Testaufgabe.getAccountsMap().containsKey(p);
    }

    public void createAccount(Player p) {
        String id = generateID();

        Bukkit.getScheduler().runTaskAsynchronously(Testaufgabe.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (MySQL.getResult("SELECT ID FROM Accounts WHERE ID = '" + id + "'") == null) {
                    MySQL.prepareStatement("INSERT INTO Accounts(UUID, ID, Cash) VALUES ('" + p.getUniqueId().toString() + "', '" + id + "', 0)");

                    setAccounts(p, getAccounts(p) + 1);

                    p.sendMessage(Testaufgabe.getPrefix() + "§aEin neues Konto mit der §cID " + id + " §awurde für dich erstellt.");
                } else {
                    createAccount(p);
                }
            }
        });
        Bukkit.getScheduler().scheduleSyncDelayedTask(Testaufgabe.getInstance(), new Runnable() {
            @Override
            public void run() {
                Scoreboards.setScoreboard(p);
            }
        }, 5);
    }

    public void deleteAccount(String pID) {
        MySQL.prepareStatement("DELETE FROM Accounts WHERE ID = " + pID);
    }

    public Integer getAccounts(Player p) {
        return Testaufgabe.getAccountsMap().get(p);
    }

    public Integer getAccounts(String uuid) {
        int accounts = (int) MySQL.getResult("SELECT Accounts FROM Players WHERE UUID = '" + uuid + "'");
        return accounts;
    }

    public void setAccounts(Player p, int pAccounts) {
        Testaufgabe.getAccountsMap().put(p, pAccounts);
    }

    public void setAccounts(String uuid, int pAccounts) {
        MySQL.prepareStatement("UPDATE Players SET Accounts = '" + pAccounts + "' WHERE UUID = '" + uuid + "'");
    }

    public Integer getCash(String pID) {
        int cash = (int) MySQL.getResult("SELECT Cash FROM Accounts WHERE ID = '" + pID + "'");
        return cash;
    }

    public void setCash(String pID, String pFrom, int pCash) {
        MySQL.prepareStatement("UPDATE Accounts SET Cash = '" + pCash + "' WHERE ID = '" + pID + "'");

        SaveManager sm = new SaveManager();
        sm.log(pFrom, pID, pCash);
    }

    public void addCash(String pID, String pFrom, int pCash) {
        int cash = getCash(pID);
        cash += pCash;

        setCash(pID, pFrom, cash);
    }

    public void removeCash(String pID, String pFrom, int pCash) {
        int cash = getCash(pID);
        cash -= pCash;

        setCash(pID, pFrom, cash);
    }

    public static void updateAccountsInMySQL() {
        AccountManager am = new AccountManager();

        for(Player p : Bukkit.getOnlinePlayers()) {
            am.setAccounts(p.getUniqueId().toString(), am.getAccounts(p));
        }
    }

    public void addPlayersWithAccountsInHashMap() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            loadAccounts(p);
        }
    }

    public boolean existAccount(String uuid, String pAccountID) {
        ResultSet rs = MySQL.getResultSet("SELECT ID FROM Accounts WHERE ID = '" + pAccountID + "' AND UUID = '" + uuid + "'");

        try {
            if(rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean existAccount(String pAccountID) {
        ResultSet rs = MySQL.getResultSet("SELECT ID FROM Accounts WHERE ID = '" + pAccountID + "'");

        try {
            if(rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private String generateID() {
        String id = "";

        for (int i = 0; i < 6; i++) {
            int number = (int) (Math.random() * 10/* + 0*/);
            id += Integer.toString(number);
        }
        return id;
    }
}