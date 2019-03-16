package de.dionik.testaufgabe.Listener;

import de.dionik.testaufgabe.Manager.CashManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) {
        Player p = e.getPlayer();

        e.setCancelled(true);

        CashManager cm = new CashManager();

        TextComponent name = new TextComponent();
        name.setText("§7" + p.getName());
        name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7" + p.getName() + "'s Bargeld: §c" + cm.getCash(p)).create()));
        name.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/cash transaction " + p.getName() + " "));

        TextComponent extra = new TextComponent();
        extra.setText(":§f " + e.getMessage());

        name.addExtra(extra);

        for (Player all : Bukkit.getOnlinePlayers()) {
            all.spigot().sendMessage(name);
        }
    }
}
