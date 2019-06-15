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

    public ApiHandler(final ApiServer server) {
        connector = new DofusConnector(server);
    }

    public void handleMessage(final Command command) {
        switch (command.getCommand()) {
            case "connect":
                handleConnectMessage(command.getParameters());
                break;
        }
    }

    private void handleConnectMessage(final Map<String, String> parameters) {
        BotInfo botInfo = new BotInfo(
            parameters.get("account"),
            parameters.get("password"),
            parameters.get("name"),
            parameters.get("server")
        );
        this.connector.setBotInfo(botInfo);

        Thread connectorThread = new Thread(connector);
        connectorThread.start();
    }
}
