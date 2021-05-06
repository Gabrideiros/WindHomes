
package me.gabrideiros.home.lib.utils;

public enum InventorySize {
    ONE_ROW(9),
    TWO_ROWS(18),
    THREE_ROWS(27),
    FOUR_ROWS(36),
    FIVE_ROWS(45),
    SIX_ROWS(54);

    private int slotsAmount;

    InventorySize(int slotsAmount) {
        this.slotsAmount = slotsAmount;
    }

    public int getSlotsAmount() {
        return slotsAmount;
    }
}
