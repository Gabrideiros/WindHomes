package me.gabrideiros.home.menu;

import me.gabrideiros.home.Main;
import me.gabrideiros.home.controller.UserController;
import me.gabrideiros.home.enums.Type;
import me.gabrideiros.home.lib.PaginatedGUIBuilder;
import me.gabrideiros.home.lib.buttons.ItemButton;
import me.gabrideiros.home.listener.BaseListener;
import me.gabrideiros.home.model.User;
import me.gabrideiros.home.service.UserService;
import me.gabrideiros.home.util.ActionBar;
import me.gabrideiros.home.util.Locations;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class HomesMenu {

    private final Main plugin;
    private final UserController userController;
    private final UserService userService;

    public HomesMenu(Main plugin, UserController userController, UserService userService) {
        this.plugin = plugin;
        this.userController = userController;
        this.userService = userService;
    }

    public void open(Player player) {

        User user = userController.get(player.getName());

        PaginatedGUIBuilder gui = new PaginatedGUIBuilder(
                "Suas homes",
                "xxxxxxxxx" +
                        "x#######x" +
                        "<#######>" +
                        "x#######x" +
                        "xxxxxxxxx"
        )
                .setNextPageItem(Material.ARROW, 1, "§aPróxima página")
                .setPreviousPageItem(Material.ARROW, 1, "§aPágina anterior")

                .setDefaultAllCancell(true);

        if (user == null || user.getHomes().size() == 0) {
            gui.setButton(22, new ItemButton(Material.WEB, "§c=//=", "§7Você não possui nenhuma home."));
            gui.setContent(new ArrayList<>());
        } else {

            AtomicInteger i = new AtomicInteger(0);

            List<ItemButton> items = user.getHomes().values().stream().map(home -> {

                Location location = Locations.deserialize(home.getLocation());

                return new ItemButton(
                        Material.WOOD_DOOR,
                        "§a#" + i.incrementAndGet() + " Home " + home.getName(),
                        "§fMundo: §7" + location.getWorld().getName(),
                        "§fCoordenadas: §7" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ(),
                        "",
                        "§aSua home é " + (home.getType().equals(Type.PUBLIC) ? "pública \n§8* /home " + player.getName() + ":" + home.getName() : "privada") + ".",
                        "",
                        "§fClique direito: §7Deletar home",
                        "§fClique esquerdo: §7Teleportar",
                        "§fScroll: §7Definir como " + (home.getType().equals(Type.PUBLIC) ? "privada" : "pública")

                ).
                        addAction(ClickType.RIGHT, e -> {

                            user.getHomes().remove(home.getName());

                            CompletableFuture.runAsync(() -> {
                                if (user.getHomes().size() < 1) userService.delete(user);
                            });

                            open(player);

                        })
                        .addAction(ClickType.LEFT, e -> {

                            player.closeInventory();

                            if (player.hasPermission("home.bypass")) {

                                BaseListener.time.remove(player.getName());

                                player.teleport(location);

                                ActionBar.send(player, "§eTeleportado com sucesso!");
                                return;
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

                        })
                        .addAction(ClickType.MIDDLE, e -> {

                            home.setType(home.getType().equals(Type.PUBLIC) ? Type.PRIVATE : Type.PUBLIC);

                            open(player);

                        });


            }).collect(Collectors.toList());

            gui.setContent(items);
        }

        gui.build().show(player);
    }
}
