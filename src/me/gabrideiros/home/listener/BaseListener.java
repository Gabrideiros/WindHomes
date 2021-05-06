package me.gabrideiros.home.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class BaseListener implements Listener {

    public static List<String> time;

    public BaseListener() { time = new ArrayList<>(); }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        time.remove(player.getName());

    }
}
