package de.dionik.testaufgabe.Commands;

import de.dionik.testaufgabe.Manager.AccountManager;
import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Manager.SaveManager;
import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.MySQL;
import de.dionik.testaufgabe.Utils.Scoreboards;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.out.println(Testaufgabe.getConsolePrefix() + Testaufgabe.getConsolePlayerCmd());
            return true;
        }
        Player p = (Player) sender;

        if (args.length < 1 || args.length > 4 || args.length == 3) {
            sendUsageOfAccount(p);
            return true;
        }

        if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("list")) {
                sendUsageOfAccount(p);
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (!p.hasPermission("testaufgabe.command.account.create")) {
                    p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                    return true;
                }

                AccountManager am = new AccountManager();
                am.createAccount(p);
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (!p.hasPermission("testaufgabe.command.account.list")) {
                    p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                    return true;
                }
                ResultSet rs = MySQL.getResultSet("SELECT ID, Cash FROM Accounts WHERE UUID = '" + p.getUniqueId().toString() + "'");

                try {
                    while (rs.next()) {
                        int cash = rs.getInt("Cash");

//                        p.sendMessage(Testaufgabe.getPrefix() + "Auf dem Konto mit der Kontonummer §c" + rs.getInt("ID") + " §7sind §8➟ §7" + cash + "€.");

                        TextComponent message = new TextComponent();
                        message.setText(Testaufgabe.getPrefix() + "Auf dem Konto mit der Kontonummer §c" + rs.getString("ID") + " §7sind §8➟ §7" + cash + "€.");
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Kontoauszug von: §c" + rs.getString("ID")).create()));
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/account transactions " + rs.getString("ID")));

                        p.spigot().sendMessage(message);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("delete") && !args[0].equalsIgnoreCase("transactions")) {
                sendUsageOfAccount(p);
                return true;
            }

            if (args[0].equalsIgnoreCase("delete")) {
                if (!p.hasPermission("testaufgabe.command.account.delete")) {
                    p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                    return true;
                }

                try {
                    int pID = Integer.parseInt(args[1]);
                    AccountManager am = new AccountManager();
                    CashManager cm = new CashManager();

                    if (am.existAccount(p.getUniqueId().toString(), args[1]) == false) {
                        p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe eine gültige Kontonummer ein!");
                        return true;
                    }

                    cm.addCash(p, am.getCash(args[1]));
                    am.deleteAccount(args[1]);
                    Testaufgabe.getAccountsMap().put(p, Testaufgabe.getAccountsMap().get(p) - 1);
                    p.sendMessage(Testaufgabe.getPrefix() + "Dein Konto mit der Kontonummer §c" + args[1] + " §7wurde gelöscht!");
                    Scoreboards.setScoreboard(p);
                } catch (NumberFormatException e) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe einen gültigen Wert ein!");
                    return true;
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("transactions")) {
                if (!p.hasPermission("testaufgabe.command.account.transactions")) {
                    p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                    return true;
                }

                AccountManager am = new AccountManager();

                if (!am.existAccount(p.getUniqueId().toString(), args[1])) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe eine gültige Kontonummer ein!");
                    return true;
                }
                p.sendMessage(Testaufgabe.getPrefix() + "Du erhälst nun den Kontoauszug mit den letzten 10 Transaktionen der Kontonummer §c" + args[1] + "§7:");
                boolean areTransactions = false;

                try {
                    //TODO
                    int count = 0;
                    int lastID = -1;

                    PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT ACCOUNTID, TARGETACCOUNTID, Cash, ID FROM Transaction ORDER BY ID ASC");
                    ResultSet rs = ps.executeQuery();

                    String target = "-";
                    String from = "-";
                    int cash = 0;

                    while (count != 10) {
                        while (rs.next()) {
        /*                    if (count == 0) {
                                ps = MySQL.getConnection().prepareStatement("SELECT ACCOUNTID, TARGETACCOUNTID, Cash, ID FROM Transaction ORDER BY ID ASC");
                                rs.next();
                            }*/

                            if (args[1].equalsIgnoreCase(rs.getString("TARGETACCOUNTID"))) {
                                from = rs.getString("ACCOUNTID");
                            } else {
                                target = rs.getString("TARGETACCOUNTID");
                            }
                            cash = rs.getInt("Cash");
                            lastID = rs.getInt("ID");
                        }

                        areTransactions = true;

                        if (!target.equalsIgnoreCase("-")) {
                            p.sendMessage("Nach " + target + ": " + cash);
                        }
                        else if (!from.equalsIgnoreCase("-")) {
                            p.sendMessage("Von " + from + ": " + cash);
                        } else {
                            p.sendMessage("Von System: " + cash);
                        }

                        count++;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!areTransactions) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Bei dem Konto mit der ID §c" + args[1] + " §7konnten keine Transaktionen gefunden werden!");
                    return true;
                }
            }
        }

        if (args.length == 4) {
            if (!p.hasPermission("testaufgabe.command.account.transaction")) {
                p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                return true;
            }

            if (!args[0].equalsIgnoreCase("transaction")) {
                sendUsageOfAccount(p);
                return true;
            }

            try {
                int kontonr = Integer.parseInt(args[1]);
                AccountManager am = new AccountManager();

                if (!am.existAccount(p.getUniqueId().toString(), args[1])) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe eine gültige Kontonummer ein, welche dir auch gehört!");
                    return true;
                }

                if (!am.existAccount(args[2])) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe eine gültige Kontonummer ein!");
                    return true;
                }

                try {
                    int pCash = Integer.parseInt(args[3]);
                    CashManager cm = new CashManager();

                    if (pCash < 1 || pCash > am.getCash(args[1])) {
                        p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe einen gültigen Wert ein!");
                        return true;
                    }

                    am.removeCash(args[1], args[2], pCash);
                    cm.removeGesamtVermögen(p, pCash);
                    am.addCash(args[2], args[1], pCash);

                    String uuid = "";
                    PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT UUID FROM Accounts WHERE ID = '" + args[2] + "'");
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        uuid = rs.getString("UUID");
                    }

                    Player target = Bukkit.getPlayer(UUID.fromString(uuid));

//                      if (am.existAccount(all.getUniqueId().toString(), args[2])) {
                        if (target != null) {
                            target.sendMessage(Testaufgabe.getPrefix() + "Dir wurde §6" + pCash + "€ §7auf das Konto mit der ID §c" + args[2] + " §7überwiesen!");
                            cm.addGesamtVermögen(target, pCash);
                            Scoreboards.setScoreboard(target);
                        }

                    SaveManager sm = new SaveManager();
                    sm.log(args[1], args[2], pCash);
                    Scoreboards.setScoreboard(p);
                    p.sendMessage(Testaufgabe.getPrefix() + "Du hast §6" + pCash + "€ §7auf das Konto mit der ID §c" + args[2] + " §7überwiesen!");
                    return true;
                } catch (NumberFormatException e) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe einen gültigen Wert ein!");
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (NumberFormatException e) {
                p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe eine gültige Kontonummer ein!");
                return true;
            }
        }
        return false;
    }

    private void sendUsageOfAccount(Player p) {
        if (!p.hasPermission("testaufgabe.command.account.create")
                && !p.hasPermission("testaufgabe.command.account.list")
                && !p.hasPermission("testaufgabe.command.account.delete")
                && !p.hasPermission("testaufgabe.command.account.transactions")
                && !p.hasPermission("testaufgabe.command.account.transaction")) {
            p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
            return;
        }
        p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getUsage());

        if (p.hasPermission("testaufgabe.command.account.create")) {
            p.sendMessage("    §7/account create");
        }

        if (p.hasPermission("testaufgabe.command.account.list")) {
            p.sendMessage("    §7/account list");
        }

        if (p.hasPermission("testaufgabe.command.account.delete")) {
            p.sendMessage("    §7/account delete <kontonr>");
        }

        if (p.hasPermission("testaufgabe.command.account.transactions")) {
            p.sendMessage("    §7/account transactions <kontonr>");
        }

        if (p.hasPermission("testaufgabe.command.account.transaction")) {
            p.sendMessage("    §7/account transaction <kontonr> <überweisungsnr> <betrag>");
        }
    }
}