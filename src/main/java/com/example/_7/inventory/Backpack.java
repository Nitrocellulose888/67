package com.example._7.inventory;

import com.example._7.item.Item;
import com.example._7.item.shape.GridOffset;
import com.example._7.item.shape.ItemShape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 背包（格子 + 放置道具）
 */
public class Backpack {
    private static final int DEFAULT_ROWS = 5;
    private static final int DEFAULT_COLS = 5;

    private final int rows;
    private final int cols;
    private List<PlacedItem> placedItems;

    public Backpack() {
        this(DEFAULT_ROWS, DEFAULT_COLS);
    }

    public Backpack(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Backpack size must be positive.");
        }

        this.rows = rows;
        this.cols = cols;
        this.placedItems = new ArrayList<>();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<PlacedItem> getPlacedItems() {
        return List.copyOf(placedItems);
    }

    /*
     * 嘗試把道具放進背包。
     * 成功：加入 placedItems，回傳 true
     * 失敗：不改變背包，回傳 false
     */
    public boolean tryPlaceItem(PlacedItem placedItem) {
        if (placedItem == null) {
            return false;
        }

        if (!canPlace(placedItem)) {
            return false;
        }

        placedItems.add(placedItem);
        return true;
    }

    /*
     * 檢查一個道具以目前 position + rotation 是否能放進背包。
     *
     * 適用於：
     * 1. 新道具首次擺放
     * 2. 移動後判斷
     * 3. 旋轉後判斷
     */
    public boolean canPlace(PlacedItem candidate) {
        return canPlace(candidate, null);
    }

    /*
     * internal version：
     * ignoredItem 用於「旋轉自己」或「移動自己」時，
     * 避免候選道具和它原本的自己發生碰撞判定。
     */
    private boolean canPlace(PlacedItem candidate, PlacedItem ignoredItem) {
        if (candidate == null) {
            return false;
        }

        Set<GridPosition> candidateCells = getOccupiedBackpackCells(candidate);

        // 1. 邊界檢查
        for (GridPosition cell : candidateCells) {
            if (!isInsideBackpack(cell)) {
                return false;
            }
        }

        // 2. 與其他已放置道具檢查重疊
        for (PlacedItem existingItem : placedItems) {
            if (existingItem == ignoredItem) {
                continue;
            }

            Set<GridPosition> existingCells = getOccupiedBackpackCells(existingItem);

            for (GridPosition cell : candidateCells) {
                if (existingCells.contains(cell)) {
                    return false;
                }
            }
        }

        return true;
    }

    /*
     * 嘗試將已放在背包中的道具順時針旋轉 90 度。
     *
     * 旋轉後若仍合法，保留旋轉結果。
     * 若超界或撞到其他道具，恢復原本 rotation。
     */
    public boolean tryRotateItem(PlacedItem placedItem) {
        if (placedItem == null || !placedItems.contains(placedItem)) {
            return false;
        }

        Rotation originalRotation = placedItem.getRotation();

        placedItem.rotateClockwise();

        if (canPlace(placedItem, placedItem)) {
            return true;
        }

        placedItem.setRotation(originalRotation);
        return false;
    }

    /*
     * 將某個 PlacedItem 的局部 shape cell，
     * 轉換成背包上的實際格子座標。
     */
    private Set<GridPosition> getOccupiedBackpackCells(PlacedItem placedItem) {
        Set<GridPosition> occupiedPositions = new HashSet<>();

        GridPosition basePosition = placedItem.getPosition();
        ItemShape currentShape = placedItem.getCurrentShape();

        for (GridOffset offset : currentShape.occupiedCells()) {
            int actualRow = basePosition.row() + offset.row();
            int actualCol = basePosition.col() + offset.col();

            occupiedPositions.add(new GridPosition(actualRow, actualCol));
        }

        return occupiedPositions;
    }

    private boolean isInsideBackpack(GridPosition position) {
        return position.row() >= 0
                && position.row() < rows
                && position.col() >= 0
                && position.col() < cols;
    }

    public boolean tryMoveItem(PlacedItem placedItem, GridPosition newPosition) {
        if (placedItem == null || newPosition == null) {
            return false;
        }

        if (!placedItems.contains(placedItem)) {
            return false;
        }

        GridPosition originalPosition = placedItem.getPosition();

        placedItem.setPosition(newPosition);

        if (canPlace(placedItem, placedItem)) {
            return true;
        }

        placedItem.setPosition(originalPosition);
        return false;
    }

    public boolean removeItem(PlacedItem placedItem) {
        if (placedItem == null) {
            return false;
        }
        return placedItems.remove(placedItem);
    }

    // 讓 BattleEngine 可以呼叫 取得背包中的物品名單
    public List<Item> getBattleItems() {
        List<Item> battleItems = new ArrayList<>();

        for (PlacedItem placedItem : placedItems) {
            battleItems.add(placedItem.getItem());
        }

        return List.copyOf(battleItems);
    }
}