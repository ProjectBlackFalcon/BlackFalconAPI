package com.ankamagames.dofus.api;

import java.util.Map;

import com.ankamagames.dofus.core.model.BotInfo;
import com.ankamagames.dofus.core.model.Command;
import com.ankamagames.dofus.core.network.DofusConnector;

/**
 * Handler external to the game. It will be only actions that the player/client wants.
 */
public class ApiHandler {

    private DofusConnector connector;

    public static final String CONNECT = "connect";
    public static final String STATUS = "status";
    public static final String SERVER_DOWN = "server down";

    public ApiHandler(final ApiServer server) {
        connector = new DofusConnector(server);
    }

    public void handleMessage(final Command command) {
        switch (command.getCommand()) {
            case CONNECT:
                handleConnectMessage(command.getParameters());
                break;
        }
    }

    private void handleConnectMessage(final Map<String, Object> parameters) {
        BotInfo botInfo = new BotInfo(
            (String) parameters.get("username"),
            (String) parameters.get("password"),
            (String) parameters.get("name"),
            (int) parameters.get("serverId")
        );
        this.connector.setBotInfo(botInfo);

        Thread connectorThread = new Thread(connector);
        connectorThread.start();
    }
}
