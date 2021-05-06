package me.gabrideiros.home.commands;

import me.gabrideiros.home.Main;
import me.gabrideiros.home.controller.UserController;
import me.gabrideiros.home.enums.Type;
import me.gabrideiros.home.listener.BaseListener;
import me.gabrideiros.home.model.User;
import me.gabrideiros.home.util.ActionBar;
import me.gabrideiros.home.util.Locations;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Home implements CommandExecutor {

    private final Main plugin;
    private final UserController userController;

    public Home(Main plugin, UserController userController) {
        this.plugin = plugin;
        this.userController = userController;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cComando apenas para jogadores!");
            return true;
        }

        Player player = (Player) sender;


        if (args.length == 0 || !args[0].contains(":")) {
            plugin.menu.open(player);
            return true;
        }

        String home = args[0];

        String[] split = home.split(":");

        if (split.length < 1 || split.length > 3) {
            player.sendMessage("§c[!] Utilize /home <player:nome>.");
            return true;
        }

        String owner = split[0];

        if (player.getName().equalsIgnoreCase(owner)) {
            player.sendMessage("§cVocê não pode acessar sua própria home pública!");
            return true;
        }

        User user = userController.get(owner);

        if (user == null) {
            player.sendMessage("§cEste jogador não possui nenhuma home!");
            return true;
        }

        if (split.length == 1) {
            if (user.getPublic().size() > 0) player.sendMessage("§eHomes públicas de " + owner + ": §f" + String.join(", ", user.getPublic()) + "§e.");
            return true;
        }

        String name = split[1];


        me.gabrideiros.home.model.Home h = user.getHomes().get(name);

        if (h == null) {
            player.sendMessage("§cNenhuma home com este nome foi encontrada!");
            if (user.getPublic().size() > 0) player.sendMessage("§eHomes públicas de " + owner + ": §f" + String.join(", ", user.getPublic()) + "§e.");
            return true;
        }

        if (h.getType().equals(Type.PRIVATE)) {
            player.sendMessage("§cEsta home não é pública!");
            return true;
        }

        Location location = Locations.deserialize(h.getLocation());

        if (player.hasPermission("home.bypass")) {

            BaseListener.time.remove(player.getName());

            player.teleport(location);

            ActionBar.send(player, "§eTeleportado com sucesso!");
            return true;
        }

        BaseListener.time.add(player.getName());

        new BukkitRunnable() {

            int i = 3;

            @Override
            public void run() {

                if (BaseListener.time.contains(player.getName())) {

                    if (i > 0) {
                        ActionBar.send(player, "§eTeleportando-se em §f" + i + "§e segundos");
                        --i;
                    } else {

                        BaseListener.time.remove(player.getName());

                        player.teleport(location);

                        player.sendMessage("§eTeleportado com sucesso!");
                        cancel();
                    }

                } else {

                    if (player.isOnline()) ActionBar.send(player, "§cTeleporte cancelado!");
                    cancel();
                }

            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L);

        return true;
    }
}