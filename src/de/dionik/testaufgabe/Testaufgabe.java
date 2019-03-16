package de.dionik.testaufgabe;

import de.dionik.testaufgabe.Commands.AccountCommand;
import de.dionik.testaufgabe.Commands.CashCommand;
import de.dionik.testaufgabe.Listener.*;
import de.dionik.testaufgabe.Manager.AccountManager;
import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Utils.MySQL;
import de.dionik.testaufgabe.Utils.Scoreboards;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Testaufgabe extends JavaPlugin {

    private static File file = new File("plugins/Testaufgabe", "config.yml");
    private static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    private static HashMap<Player, Integer> cashMap = new HashMap<>();
    private static HashMap<Player, Integer> accountsMap = new HashMap<>();
    private static HashMap<Player, Integer> totalAssetsMap = new HashMap<>();

    private static Plugin instance;

    private static String prefix = "§c[§7Kyudo§c] §7";
    private static String consolePrefix = "[Testaufgabe-Kyudo] ";
    private static String consolePlayerCmd = "Um diesen Befehl ausführen zu können musst du ein Spieler sein.";
    private static String noPerm = "§aDu hast §6keinen Zugriff §aauf diesen Befehl.";
    private static String usage = "Benutzung: ";

    private static double percentage = Testaufgabe.getCfg().getDouble("Rewards.fiveMinutes-Percentage") / 100;

    public void onEnable() {
        instance = this;

        System.out.println(getConsolePrefix() + "Plugin v" + getInstance().getDescription().getVersion() + " by Dionik.");

        existFile();
        setDefault();

        registerCommands();
        registerEvents();

        if (!getCfg().getString("MySQL.user").equalsIgnoreCase("user")
                && !getCfg().getString("MySQL.password").equalsIgnoreCase("password")
                && !getCfg().getString("MySQL.database").equalsIgnoreCase("database")) {
                    MySQL.connect();
        } else {
            System.out.println(getConsolePrefix() + "Verbindung zur MySQL-Datenbank konnte nicht hergestellt werden.");
            System.out.println(getConsolePrefix() + "Bitte bearbeite die config.yml, um eine Verbindung zur MySQL-Datenbank aufzubauen.");
            getInstance().getPluginLoader().disablePlugin(this);
        }

        if (MySQL.isConnected()) {
            MySQL.createTable();
        }

        CashManager cm = new CashManager();
        cm.addPlayersWithCashInHashMap();

        AccountManager am = new AccountManager();
        am.addPlayersWithAccountsInHashMap();

        fiveMinutesAddition();
    }

    public void onDisable() {
        CashManager cm = new CashManager();
        cm.updateCashInMySQL();

        AccountManager am = new AccountManager();
        am.updateAccountsInMySQL();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer(Testaufgabe.getPrefix() + "Der Server restartet!");
        }
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static FileConfiguration getCfg() {
        return cfg;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String getNoPerm() {
        return noPerm;
    }

    public static String getUsage() {
        return usage;
    }

    public static String getConsolePrefix() {
        return consolePrefix;
    }

    public static String getConsolePlayerCmd() {
        return consolePlayerCmd;
    }

    @Override
    public File getFile() {
        return file;
    }

    public static HashMap<Player, Integer> getCashMap() {
        return cashMap;
    }

    public static HashMap<Player, Integer> getAccountsMap() {
        return accountsMap;
    }

    public static HashMap<Player, Integer> getTotalAssetsMap() {
        return totalAssetsMap;
    }

    public void registerCommands() {
        getCommand("cash").setExecutor(new CashCommand());
        getCommand("account").setExecutor(new AccountCommand());
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerItemConsumeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    private void fiveMinutesAddition() {
        CashManager cm = new CashManager();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    double reward = cm.getCash(all) * percentage;
                    cm.addCash(all, (int) reward);
                    cm.addGesamtVermögen(all, (int) reward);
                    Scoreboards.setScoreboard(all);
                    all.sendMessage(Testaufgabe.getPrefix() + "Dir wurde die 5 Minuten Vergütung erstattet!");
                }
            }
        }, 0, 20 * 5 * 60);
    }

    private void existFile() {
        File dir = new File(file.getParent());

        if (!dir.exists()) {
            dir.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            cfg.load(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void setDefault() {
        cfg.options().copyDefaults(true);

        cfg.addDefault("MySQL.host", "localhost");
        cfg.addDefault("MySQL.user", "user");
        cfg.addDefault("MySQL.password", "password");
        cfg.addDefault("MySQL.database", "database");
        cfg.addDefault("MySQL.port", 3306);

        cfg.addDefault("Rewards.Animalkill", 50);
        cfg.addDefault("Rewards.EatMeat", 40);
        cfg.addDefault("Rewards.FeedAnimal", 10);
        cfg.addDefault("Rewards.fiveMinutes-Percentage", 10.0);
        cfg.addDefault("Rewards.Mobkill", 25);
        cfg.addDefault("Rewards.PlayerkillsPlayer-Percentage", 10.0);

        saveFile();
    }

    private void saveFile() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
        Informationen zum Sourcecode der Testaufgabe | Dionik

        Die Textnachrichten sind sehr serverorientiert gehalten.
        Der Source ist hier nicht zeilensparend, sondern etwas länger, dafür verständlicher gestaltet. Deshalb wurden
        auch keine einzelnen Erklärungen an Methoden o.ä. angehangen, bei Fragen kann sich gerne an mich gewandt werden.

        - Permissions -
            -> Cash-Befehl
                /cash info -> testaufgabe.command.cash.info
                /cash info <spieler> -> testaufgabe.command.cash.info.others
                /cash transaction <kontonr> <betrag> -> testaufgabe.command.cash.transaction.konto
                /cash transaction <spieler> <betrag> -> testaufgabe.command.cash.transaction.spieler

            -> Account-Befehl
                /account create -> testaufgabe.command.account.create
                /account list -> testaufgabe.command.account.list
                /account delete <kontonr> -> testaufgabe.command.account.delete
                /account transactions <kontonr> -> testaufgabe.command.account.transactions
                /account transaction <kontonr> <betrag> -> testaufgabe.command.account.transaction

        - API -
            -> CashManager - Für das Managen des Bargeldes
            -> AccountManager - Für das Verwalten der Konten

        Vielen Dank für die Bearbeitung meiner Testaufgabe.
        Mit freundlichen Grüßen, Finn M.

        TODO:
            - Blockinteraktionen
            - /account-Command Kontoauszüge
 */

/* plugin.yml

name: ${project.artifactId}
version: ${project.version}
author: Dionik
main: ${project.groupId}.Testaufgabe

commands:
  cash:
  account:
 */