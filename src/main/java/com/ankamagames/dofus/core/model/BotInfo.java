package com.ankamagames.dofus.core.model;

public class BotInfo {

    private String account;
    private String password;
    private String name;
    private int serverId;

    private boolean ridingMount;
    private boolean moving;
    private boolean fighting;

    private int cellId;
    private double mapId;
    private double id;

    public BotInfo(final String account, final String password, final String name, final int serverId) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.serverId = serverId;
    }

    public double getId() {
        return id;
    }

    public void setId(final double id) {
        this.id = id;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(final int cellId) {
        this.cellId = cellId;
    }

    public boolean isFighting() {
        return fighting;
    }

    public void setFighting(final boolean fighting) {
        this.fighting = fighting;
    }

    public double getMapId() {
        return mapId;
    }

    public void setMapId(final double mapId) {
        this.mapId = mapId;
    }

    public boolean isRidingMount() {
        return ridingMount;
    }

    public void setRidingMount(final boolean ridingMount) {
        this.ridingMount = ridingMount;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(final boolean moving) {
        this.moving = moving;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(final int serverId) {
        this.serverId = serverId;
    }
}
