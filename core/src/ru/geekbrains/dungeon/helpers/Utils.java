package ru.geekbrains.dungeon.helpers;

public class Utils {
    public static int getCellsIntDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public static float getCellsFloatDistance(int x1, int y1, int x2, int y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public static boolean isCellsAreNeighbours(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1) == 1;
    }
}
