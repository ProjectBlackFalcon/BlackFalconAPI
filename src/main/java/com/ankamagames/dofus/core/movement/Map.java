package com.ankamagames.dofus.core.movement;

import java.util.List;

public class Map {

    private boolean isUsingNewMovementSystem;
    private List<CellData> cells;

    public Map() {
    }

    public Map(final boolean isUsingNewMovementSystem, final List<CellData> cells) {
        this.isUsingNewMovementSystem = isUsingNewMovementSystem;
        this.cells = cells;
    }

    public boolean isUsingNewMovementSystem() {
        return isUsingNewMovementSystem;
    }

    public void setUsingNewMovementSystem(final boolean usingNewMovementSystem) {
        isUsingNewMovementSystem = usingNewMovementSystem;
    }

    public List<CellData> getCells() {
        return cells;
    }

    public void setCells(final List<CellData> cells) {
        this.cells = cells;
    }

    public boolean NoEntitiesOnCell(int cellId)
    {
        return true;
    }

}
