package com.ankamagames.dofus.core.movement;

public class CellData {

    private boolean mov;
    private boolean nonWalkableDuringFight;
    private int floor;
    private int moveZone;
    private boolean los;
    private int speed;
    private int losmov = 3;

    public CellData(final boolean mov, final boolean nonWalkableDuringFight, final int floor, final int moveZone, final boolean los, final int speed) {
        this.mov = mov;
        this.nonWalkableDuringFight = nonWalkableDuringFight;
        this.floor = floor;
        this.moveZone = moveZone;
        this.los = los;
        this.speed = speed;
    }

    public CellData() {
    }

    public boolean isMov() {
        return mov;
    }

    public void setMov(final boolean mov) {
        this.mov = mov;
    }

    public boolean isNonWalkableDuringFight() {
        return nonWalkableDuringFight;
    }

    public void setNonWalkableDuringFight(final boolean nonWalkableDuringFight) {
        this.nonWalkableDuringFight = nonWalkableDuringFight;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(final int floor) {
        this.floor = floor;
    }

    public int getMoveZone() {
        return moveZone;
    }

    public void setMoveZone(final int moveZone) {
        this.moveZone = moveZone;
    }

    public boolean isLos() {
        return los;
    }

    public void setLos(final boolean los) {
        this.los = los;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(final int speed) {
        this.speed = speed;
    }

    public int getLosmov() {
        return losmov;
    }

    public void setLosmov(final int losmov) {
        this.losmov = losmov;
    }
}
