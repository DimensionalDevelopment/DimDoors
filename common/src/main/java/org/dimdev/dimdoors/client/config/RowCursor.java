package org.dimdev.dimdoors.client.config;

public final class RowCursor {
    private final int startY;
    private final int minY;
    private final int maxY;
    private final int rowHeight;
    private int index;

    RowCursor(int startY, int minY, int maxY, int rowHeight) {
        this.startY = startY;
        this.minY = minY;
        this.maxY = maxY;
        this.rowHeight = rowHeight;
    }

    int next() {
        return this.startY + this.index++ * this.rowHeight;
    }

    boolean isVisible(int y) {
        return y >= this.minY && y + 20 <= this.maxY;
    }
}
