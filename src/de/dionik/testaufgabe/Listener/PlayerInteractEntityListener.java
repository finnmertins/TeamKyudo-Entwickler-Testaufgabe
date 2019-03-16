package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.Scoreboards;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {

    private static int reward = Testaufgabe.getCfg().getInt("Rewards.FeedAnimal");

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();

        if (e.getRightClicked() instanceof Animals) {
            if (p.getInventory().getItemInHand().getType() == Material.CARROT
                    || p.getInventory().getItemInHand().getType() == Material.POTATO
                    || p.getInventory().getItemInHand().getType() == Material.SEEDS
                    || p.getInventory().getItemInHand().getType() == Material.WHEAT
                    || p.getInventory().getItemInHand().getType() == Material.MELON_SEEDS
                    || p.getInventory().getItemInHand().getType() == Material.GOLDEN_CARROT
                    || p.getInventory().getItemInHand().getType() == Material.APPLE
                    || p.getInventory().getItemInHand().getType() == Material.SUGAR
                    || p.getInventory().getItemInHand().getType() == Material.BREAD
                    || p.getInventory().getItemInHand().getType() == Material.GOLDEN_APPLE
                    || p.getInventory().getItemInHand().getType() == Material.RAW_BEEF
                    || p.getInventory().getItemInHand().getType() == Material.COOKED_BEEF
                    || p.getInventory().getItemInHand().getType() == Material.RAW_CHICKEN
                    || p.getInventory().getItemInHand().getType() == Material.COOKED_CHICKEN
                    || p.getInventory().getItemInHand().getType() == Material.RAW_BEEF
                    || p.getInventory().getItemInHand().getType() == Material.PORK
                    || p.getInventory().getItemInHand().getType() == Material.GRILLED_PORK
                    || p.getInventory().getItemInHand().getType() == Material.MUTTON
                    || p.getInventory().getItemInHand().getType() == Material.COOKED_MUTTON
                    || p.getInventory().getItemInHand().getType() == Material.RABBIT
                    || p.getInventory().getItemInHand().getType() == Material.COOKED_RABBIT
                    || p.getInventory().getItemInHand().getType() == Material.RAW_FISH
                    || p.getInventory().getItemInHand().getType() == Material.COOKED_FISH
                    || p.getInventory().getItemInHand().getType() == Material.ROTTEN_FLESH) {
                CashManager cm = new CashManager();
                cm.addCash(p, reward);
                cm.addGesamtVermögen(p, reward);
                Scoreboards.setScoreboard(p);
                p.sendMessage(Testaufgabe.getPrefix() + "Du hast §e" + reward + " Coins §7für das Füttern von Tieren erhalten!");
            }
        }
    }
}