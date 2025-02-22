package me.gabrideiros.home.lib;

import com.google.common.collect.Maps;


import me.gabrideiros.home.lib.buttons.ClickAction;
import me.gabrideiros.home.lib.buttons.ItemButton;
import me.gabrideiros.home.lib.menus.InventoryGUI;
import me.gabrideiros.home.lib.menus.PaginatedGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
public class PaginatedGUIBuilder {
    private final String name;
    private final char[] shape;
    private List<ItemButton> content;
    private ClickAction contentDefaultAction;
    private final ItemButton[] hotbar = new ItemButton[9];
    private ItemButton borderButton;
    private ItemStack nextPageItem;
    private ItemStack previousPageItem;
    private final Map<Integer, ItemButton> buttons = Maps.newHashMap();
    private boolean defaultCancell = false;
    private boolean defaultAllCancell = false;

    public PaginatedGUIBuilder(String name, String shape) {
        this.name = name;
        this.shape = shape.toCharArray();

        if (this.shape.length > 45 || (this.shape.length % 9) != 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    public PaginatedGUIBuilder setBorder(Material material, int amount, String name, String... lore) {
        return setBorder(new ItemButton(material, amount, name, lore));
    }

    public PaginatedGUIBuilder setBorder(ItemButton button) {
        this.borderButton = button;
        return this;
    }


    public PaginatedGUIBuilder setNextPageItem(Material material, int amount, String name, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return setNextPageItem(item);
    }

    public PaginatedGUIBuilder setNextPageItem(ItemStack button) {
        this.nextPageItem = button;
        return this;
    }

    public PaginatedGUIBuilder setPreviousPageItem(Material material, int amount, String name, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return setPreviousPageItem(item);
    }

    public PaginatedGUIBuilder setPreviousPageItem(ItemStack button) {
        this.previousPageItem = button;
        return this;
    }

    public PaginatedGUIBuilder setHotbarButton(byte pos, ItemButton button) {
        if (pos > 8) {
            throw new IndexOutOfBoundsException();
        }

        hotbar[pos] = button;
        return this;
    }

    public PaginatedGUIBuilder setContent(List<ItemButton> content) {
        this.content = content;
        return this;
    }

    public PaginatedGUIBuilder setContent(ItemButton... content) {
        this.content = Arrays.asList(content);
        return this;
    }

    public PaginatedGUIBuilder setContentDefaultAction(ClickAction action) {
        this.contentDefaultAction = action;
        return this;
    }

    public boolean isDefaultCancell() {
        return defaultCancell;
    }

    public PaginatedGUIBuilder setDefaultCancell(boolean defaultCancell) {
        this.defaultCancell = defaultCancell;
        return this;
    }

    public boolean isDefaultAllCancell() {
        return defaultAllCancell;
    }

    public PaginatedGUIBuilder setDefaultAllCancell(boolean defaultAllCancell) {
        this.defaultAllCancell = defaultAllCancell;
        return this;
    }

    public PaginatedGUIBuilder setButton(int slot, ItemButton button) {
        buttons.put(slot, button);
        return this;
    }

    public PaginatedGUI build() {
        int contentSize = 0;
        for (char c : shape) {
            if (c == '#') {
                contentSize++;
            }
        }

        final int size = content.size() != 0 ? content.size() : 1;

        final int amountOfPages = (int) ((size / (float) contentSize) + 0.99);

        PaginatedGUI paginatedGUI = new PaginatedGUI();

        int currentItem = 0;
        for (int pageI = 0; pageI < amountOfPages; pageI++) {
            InventoryGUI page = new InventoryGUI(
                    name.replace("{page}", pageI + ""),
                    (shape.length + 9)
            );

            buttons.forEach(page::setButton);

            page.setDefaultAllCancell(defaultAllCancell);
            page.setDefaultCancell(defaultCancell);

            for (int i = 0; i < shape.length; i++) {
                final char current = shape[i];

                if (current == '>' && pageI < (amountOfPages-1)) {
                    final ItemButton btn = new ItemButton(nextPageItem);
                    btn.addAction(ClickType.RIGHT, (InventoryClickEvent e) ->
                            paginatedGUI.showNext((Player) e.getWhoClicked()));

                    btn.addAction(ClickType.LEFT, (InventoryClickEvent e) ->
                            paginatedGUI.showNext((Player) e.getWhoClicked()));

                    page.setButton(i, btn);
                    continue;
                }

                if (current == '<' && pageI != 0) {
                    final ItemButton btn = new ItemButton(previousPageItem);
                    btn.addAction(ClickType.RIGHT, (InventoryClickEvent e) ->
                            paginatedGUI.showPrevious((Player) e.getWhoClicked()));

                    btn.addAction(ClickType.LEFT, (InventoryClickEvent e) ->
                            paginatedGUI.showPrevious((Player) e.getWhoClicked()));

                    page.setButton(i, btn);
                    continue;
                }

                if (current == '#') {
                    if (currentItem < content.size()) {
                        ItemButton cItem = content.get(currentItem++);
                        if (cItem.getDefaultAction() == null && contentDefaultAction != null) {
                            cItem.setDefaultAction(contentDefaultAction);
                        }

                        page.setButton(i, cItem);
                    }
                    continue;
                }
                if (borderButton != null) {
                    page.setButton(i, borderButton);
                }
            }

            for (int hotbarI = 0; hotbarI < hotbar.length; hotbarI++) {
                final ItemButton item = hotbar[hotbarI];
                if (item != null) {
                    page.setButton(shape.length + hotbarI, item);
                }
            }

            paginatedGUI.addPage(page);
        }

        return paginatedGUI;
    }
}
