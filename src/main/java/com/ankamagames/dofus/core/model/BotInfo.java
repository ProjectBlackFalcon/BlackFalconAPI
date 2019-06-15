package com.ankamagames.dofus.core.model;

public class BotInfo {

    private String account;
    private String password;
    private String name;
    private String server;

    public BotInfo(final String account, final String password, final String name, final String server) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.server = server;
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

    public String getServer() {
        return server;
    }

    public void setServer(final String server) {
        this.server = server;
    }
}
