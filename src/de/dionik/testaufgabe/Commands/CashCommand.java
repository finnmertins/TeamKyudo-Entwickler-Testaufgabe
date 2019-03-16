package de.dionik.testaufgabe.Commands;

import de.dionik.testaufgabe.Manager.AccountManager;
import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.MySQL;
import de.dionik.testaufgabe.Utils.Scoreboards;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CashCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.out.println(Testaufgabe.getConsolePrefix() + Testaufgabe.getConsolePlayerCmd());
            return true;
        }
        Player p = (Player) sender;

        if (args.length < 1 || args.length > 3) {
            sendUsageOfCash(p);
            return true;
        }

        if (args.length == 1) {
            if (!p.hasPermission("testaufgabe.command.cash.info")) {
                p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                return true;
            }

            if(!args[0].equalsIgnoreCase("info")) {
                sendUsageOfCash(p);
                return true;
            }

            CashManager cm = new CashManager();
            if (!cm.isCached(p)) {
                p.sendMessage(Testaufgabe.getPrefix() + "§cDein Bargeldbetrag wird noch abgerufen!");
                return true;
            }

            p.sendMessage(Testaufgabe.getPrefix() + "Dein Bargeld: §c" + cm.getCash(p) + "€");
            return true;
        }

        if (args.length == 2) {
            if (!p.hasPermission("testaufgabe.command.cash.info.others")) {
                p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                return true;
            }

            if(!args[0].equalsIgnoreCase("info")) {
                sendUsageOfCash(p);
                return true;
            }

            String target = args[1];
            CashManager cm = new CashManager();

            if (Bukkit.getPlayer(target) != null) {
                Player player = Bukkit.getPlayer(target);

                if (!cm.isCached(player)) {
                    if (p == player) {
                        p.sendMessage(Testaufgabe.getPrefix() + "§cDein Bargeldbetrag wird noch abgerufen!");
                        return true;
                    } else {
                        player.sendMessage(Testaufgabe.getPrefix() + "§cDer Bargeldbetrag von §e" + player.getName() +  " §cwird noch abgerufen!");
                        return true;
                    }
                }

                if (p == player) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Dein Bargeld: §c" + cm.getCash(p) + "€");
                    return true;
                }

                p.sendMessage(Testaufgabe.getPrefix() + player.getName() + "'s Bargeld: §c" + cm.getCash(player) + "€");
                return true;
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(Testaufgabe.getInstance(), new Runnable() {

                    @Override
                    public void run() {
                        if (MySQL.getResult("SELECT Name FROM Players WHERE Name = '" + target + "'") == null) {
                            p.sendMessage(Testaufgabe.getPrefix() + "§c" + target + " §7wurde nicht gefunden.");
                            return;
                        }
                        String uuid = (String) MySQL.getResult("SELECT UUID FROM Players WHERE Name = '" + target + "'");
                        int cash = (int) MySQL.getResult("SELECT Cash FROM Players WHERE UUID = '" + uuid + "'");

                        p.sendMessage(Testaufgabe.getPrefix() + target + "'s Bargeld: §c" + cash + "€");
                        return;
                    }
                });
            }
        }
        if (args.length == 3) {
            if (!p.hasPermission("testaufgabe.command.cash.transaction.konto") && !p.hasPermission("testaufgabe.command.cash.transaction.spieler")) {
                p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                return true;
            }

            if(!args[0].equalsIgnoreCase("transaction")) {
                sendUsageOfCash(p);
                return true;
            }

            try {
                int checkKontoNummer = Integer.parseInt(args[1]);
                String pKontoNummer = args[1];

                if (!p.hasPermission("testaufgabe.command.cash.transaction.konto")) {
                    p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                    return true;
                }

                AccountManager am = new AccountManager();

                if (!am.existAccount(p.getUniqueId().toString(), pKontoNummer)) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Du besitzt keinen Account mit der Kontonummer §c" + pKontoNummer + "§7!");
                    return true;
                }

                try {
                    int pBargeld = Integer.parseInt(args[2]);
                    int pBargeldPositive = pBargeld * -1;

                    CashManager cm = new CashManager();

                    if (pBargeld < 0) {
                        if (pBargeldPositive > am.getCash(pKontoNummer)) {
                            p.sendMessage(Testaufgabe.getPrefix() + "Du hast nicht genügend Geld auf diesem Konto!");
                            return true;
                        }
                        am.removeCash(pKontoNummer, "p", pBargeldPositive);
                        cm.addCash(p, pBargeldPositive);
                        p.sendMessage(Testaufgabe.getPrefix() + "Du hast §6" + pBargeldPositive + "€ §7von deinem Konto mit der Kontonummer §c" + pKontoNummer + " §7abgehoben!");
                        Scoreboards.setScoreboard(p);
                        return true;
                    }
                    if (pBargeld == 0) {
                        p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe einen gültigen Wert ein!");
                        return true;
                    }
                    if (pBargeld > 0) {
                        if (pBargeld > cm.getCash(p)) {
                            p.sendMessage(Testaufgabe.getPrefix() + "Du hast nicht so viel Bargeld!");
                            return true;
                        }
                        am.addCash(pKontoNummer, "p", pBargeld);
                        cm.removeCash(p, pBargeld);
                        p.sendMessage(Testaufgabe.getPrefix() + "Du hast §6" + pBargeld + "€ §7auf dein Konto mit der Kontonummer §c" + pKontoNummer + " §7überwiesen!");
                        Scoreboards.setScoreboard(p);
                        return true;
                    }
                } catch (NumberFormatException ex) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe einen gültigen Wert ein!");
                    return true;
                }
            } catch (NumberFormatException e) {
                if (!p.hasPermission("testaufgabe.command.cash.transaction.spieler")) {
                    p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
                    return true;
                }

                if (Bukkit.getPlayer(args[1]) == null) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Du kannst kein Bargeld an §c" + args[1] + " §7überweisen, da der Spieler nicht online ist!");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == p) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Du kannst dir nicht selbst Bargeld überweisen!");
                    return true;
                }

                try {
                    int pBargeld = Integer.parseInt(args[2]);

                    if (pBargeld < 1) {
                        p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe einen gültigen Wert ein!");
                        return true;
                    }
                    CashManager cm = new CashManager();

                    if (pBargeld > cm.getCash(p)) {
                        p.sendMessage(Testaufgabe.getPrefix() + "Du hast nicht so viel Bargeld!");
                        return true;
                    }

                    cm.removeCash(p, pBargeld);
                    cm.addCash(target, pBargeld);

                    cm.removeGesamtVermögen(p, pBargeld);
                    cm.addGesamtVermögen(target, pBargeld);

                    p.sendMessage(Testaufgabe.getPrefix() + "Du hast §6" + target.getName() + " §7eine Summe von §c" + pBargeld + "€ §7überwiesen!");
                    target.sendMessage(Testaufgabe.getPrefix() + "§6" + p.getName() + " §7hat dir §c" + pBargeld + "€ §7überwiesen!");

                    Scoreboards.setScoreboard(p);
                    Scoreboards.setScoreboard(target);
                    return true;
                } catch (NumberFormatException ex) {
                    p.sendMessage(Testaufgabe.getPrefix() + "Bitte gebe einen gültigen Wert ein!");
                    return true;
                }
            }
        }
        return false;
    }

    private void sendUsageOfCash(Player p) {
        if (!p.hasPermission("testaufgabe.command.cash.info")
                && !p.hasPermission("testaufgabe.command.cash.info.others")
                && !p.hasPermission("testaufgabe.command.cash.transaction.konto")
                && !p.hasPermission("testaufgabe.command.cash.transaction.spieler")) {
            p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getNoPerm());
            return;
        }
        p.sendMessage(Testaufgabe.getPrefix() + Testaufgabe.getUsage());

        if (p.hasPermission("testaufgabe.command.cash.info")) {
            if (p.hasPermission("testaufgabe.command.cash.info.others")) {
                p.sendMessage("    §7/cash <info> (<spieler>)");
            } else {
                p.sendMessage("    §7/cash <info>");
            }
        }

        if (p.hasPermission("testaufgabe.command.cash.info.others") && !p.hasPermission("testaufgabe.command.cash.info")) {
            p.sendMessage("    §7/cash <info> <spieler>");
        }

        if (p.hasPermission("testaufgabe.command.cash.transaction.konto")) {
            p.sendMessage("    §7/cash <transaction> <spieler> <betrag>");
        }

        if (p.hasPermission("testaufgabe.command.cash.transaction.spieler")) {
            p.sendMessage("    §7/cash <transaction> <kontonr> <betrag>");
        }
    }
}