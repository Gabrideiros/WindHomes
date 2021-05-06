package me.gabrideiros.home;

import me.gabrideiros.home.commands.*;
import me.gabrideiros.home.controller.UserController;
import me.gabrideiros.home.database.Database;
import me.gabrideiros.home.database.MySQL;
import me.gabrideiros.home.database.SQLite;
import me.gabrideiros.home.lib.listeners.InventoryListener;
import me.gabrideiros.home.listener.BaseListener;
import me.gabrideiros.home.menu.HomesMenu;
import me.gabrideiros.home.service.UserService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Database database;

    private UserController userController;
    private UserService userService;

    public HomesMenu menu;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        startConnection();

        userController = new UserController();
        userService = new UserService(this, userController, database);

        menu = new HomesMenu(this, userController, userService);

        getServer().getPluginManager().registerEvents(new BaseListener(), this);

        new InventoryListener(this);

        getCommand("home").setExecutor(new Home(this, userController));
        getCommand("sethome").setExecutor(new SetHome(this, userController, userService));

    }

    @Override
    public void onDisable() {
        userService.save();
    }

    public void startConnection() {

        ConfigurationSection cs = getConfig().getConfigurationSection("Connection");

        if (cs.getString("Type").equalsIgnoreCase("MySQL"))
            database = new MySQL(this, cs.getString("Host"), cs.getInt("Port"), cs.getString("Database"), cs.getString("Username"), cs.getString("Password"));
        else
            database = new SQLite(this, "database.db");

        database.openConnection();
        database.sendCommand("CREATE TABLE IF NOT EXISTS homes (name VARCHAR(16), value LONGTEXT);");

    }
}
