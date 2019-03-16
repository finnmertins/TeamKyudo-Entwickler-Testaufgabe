package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Manager.CashManager;
import de.dionik.testaufgabe.Testaufgabe;
import de.dionik.testaufgabe.Utils.Scoreboards;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    private static int reward = Testaufgabe.getCfg().getInt("Rewards.Mobkill");
    private static int reward2 = Testaufgabe.getCfg().getInt("Rewards.Animalkill");

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) {
            if (e.getEntity().getType() == EntityType.BLAZE
                    || e.getEntity().getType() == EntityType.CAVE_SPIDER
                    || e.getEntity().getType() == EntityType.CREEPER
                    || e.getEntity().getType() == EntityType.ENDER_DRAGON
                    || e.getEntity().getType() == EntityType.ENDERMAN
                    || e.getEntity().getType() == EntityType.ENDERMITE
                    || e.getEntity().getType() == EntityType.GHAST
                    || e.getEntity().getType() == EntityType.GIANT
                    || e.getEntity().getType() == EntityType.GUARDIAN
                    //|| e.getEntity().getType() == EntityType.IRON_GOLEM
                    || e.getEntity().getType() == EntityType.MAGMA_CUBE
                    || e.getEntity().getType() == EntityType.PIG_ZOMBIE
                    || e.getEntity().getType() == EntityType.SILVERFISH
                    || e.getEntity().getType() == EntityType.SKELETON
                    || e.getEntity().getType() == EntityType.SLIME
                    || e.getEntity().getType() == EntityType.SPIDER
                    || e.getEntity().getType() == EntityType.WITCH
                    || e.getEntity().getType() == EntityType.WITHER
                    || e.getEntity().getType() == EntityType.ZOMBIE) {

                Player p = e.getEntity().getKiller();

                CashManager cm = new CashManager();
                cm.addCash(p, reward);
                cm.addGesamtVermögen(p, reward);

                p.sendMessage(Testaufgabe.getPrefix() + "Du hast §e" + reward + " Coins §7für das Töten feindlicher Kreaturen erhalten!");
                Scoreboards.setScoreboard(p);
            }

            if (e.getEntity().getType() == EntityType.PIG
                    || e.getEntity().getType() == EntityType.RABBIT
                    || e.getEntity().getType() == EntityType.CHICKEN
                    || e.getEntity().getType() == EntityType.BAT
                    || e.getEntity().getType() == EntityType.COW
                    || e.getEntity().getType() == EntityType.HORSE
                    || e.getEntity().getType() == EntityType.MUSHROOM_COW
                    || e.getEntity().getType() == EntityType.OCELOT
                    || e.getEntity().getType() == EntityType.SHEEP
                    //|| e.getEntity().getType() == EntityType.SNOWMAN
                    || e.getEntity().getType() == EntityType.SQUID
                    || e.getEntity().getType() == EntityType.VILLAGER
                    || e.getEntity().getType() == EntityType.WOLF) {

                Player p = e.getEntity().getKiller();

                CashManager cm = new CashManager();

                if (cm.getCash(p) >= reward2) {
                    cm.removeCash(p, reward2);
                    cm.removeGesamtVermögen(p, reward2);
                    p.sendMessage(Testaufgabe.getPrefix() + "Du hast §e" + reward2 + " Coins §7für das Töten friedlicher Tiere abgezogen bekommen!");
                    Scoreboards.setScoreboard(p);
                } else {
                    cm.removeGesamtVermögen(p, cm.getCash(p));
                    cm.removeCash(p, cm.getCash(p));
                    Scoreboards.setScoreboard(p);
                    p.sendMessage(Testaufgabe.getPrefix() + "Du hast §e" + cm.getCash(p) + " Coins §7für das Töten friedlicher Tiere abgezogen bekommen!");
                }
            }
        }
    }
}
