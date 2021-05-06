package me.gabrideiros.home.lib.menus;

import me.gabrideiros.home.lib.buttons.ClickAction;
import me.gabrideiros.home.lib.buttons.ItemButton;
import me.gabrideiros.home.lib.utils.InventorySize;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public class InventoryGUI implements InventoryHolder {
    private final Inventory inv;
    private Map<Integer, ItemButton> callbacks;
    private boolean defaultCancell = false;
    private boolean defaultAllCancell = false;

    public InventoryGUI(final String title, final InventorySize size) {
        this(title, size.getSlotsAmount());
    }

    public InventoryGUI(final String title, final int size) {
        inv = Bukkit.createInventory(this, size, title);
        callbacks = new HashMap<>();
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void setButton(int pos, ItemButton button) {
        inv.setItem(pos, button.getItem());
        callbacks.put(pos, button);
    }

    public void removeButton(int slot) {
        inv.clear(slot);
        callbacks.remove(slot);
    }

    public void show(Player player) {
        player.openInventory(inv);
    }

    public boolean isDefaultCancell() {
        return defaultCancell;
    }

    public void setDefaultCancell(boolean defaultCancell) {
        this.defaultCancell = defaultCancell;
    }

    public boolean isDefaultAllCancell() {
        return defaultAllCancell;
    }

    public void setDefaultAllCancell(boolean defaultAllCancell) {
        this.defaultAllCancell = defaultAllCancell;
    }

    public void onClick(InventoryClickEvent event) {
        if (!callbacks.containsKey(event.getRawSlot())) {
            return;
        }
        if (defaultCancell || defaultAllCancell)
            event.setCancelled(true);

        final ClickAction action = callbacks.get(event.getRawSlot()).getAction(event.getClick());
        if (action != null) {
            action.run(event);
        }
    }
}
