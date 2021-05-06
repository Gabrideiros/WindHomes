package me.gabrideiros.home.commands;

import me.gabrideiros.home.Main;
import me.gabrideiros.home.controller.UserController;
import me.gabrideiros.home.enums.Type;
import me.gabrideiros.home.model.Home;
import me.gabrideiros.home.model.User;
import me.gabrideiros.home.service.UserService;
import me.gabrideiros.home.util.Locations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class SetHome implements CommandExecutor {

    private final Main plugin;

    private final UserController userController;
    private final UserService userService;

    public SetHome(Main plugin, UserController userController, UserService userService) {
        this.plugin = plugin;
        this.userController = userController;
        this.userService = userService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cComando apenas para jogadores!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("§c[!] Utilize /sethome <nome>.");
            return true;
        }

        String home = args[0];

        if (home.length() > 10) {
            player.sendMessage("§cUtilize no máximo 10 caracteres!");
            return true;
        }

        User user = userController.getByName(player.getName()).orElse(new User(
                player.getName(),
                new HashMap<>()
        ));

        int amount = 0;

        for (String homes : plugin.getConfig().getStringList("Homes")) {

            String[] split = homes.split(":");

            String permission = split[0];
            int max = Integer.parseInt(split[1]);

            if (player.hasPermission(permission)) amount = max;
        }

        if (user.getHomes().size() == amount) {
            player.sendMessage("§cVocê só pode criar no máximo " + amount + " homes!");
            return true;
        }

        if (user.getHomes().containsKey(home)) {
            player.sendMessage("§cVocê já possui uma home com este nome!");
            return true;
        }

        String location = Locations.serialize(player.getLocation());

        user.getHomes().put(home, new Home(home, location, Type.PRIVATE));

        CompletableFuture.runAsync(() -> { if (!userController.getMap().containsKey(player.getName())) { userController.add(user); userService.insert(user); }});

        player.sendMessage("§eHome §f" + home + "§e setada com sucesso!");

        return true;
    }
}
