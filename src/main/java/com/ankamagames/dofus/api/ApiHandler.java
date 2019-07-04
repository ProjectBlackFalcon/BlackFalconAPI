package com.ankamagames.dofus.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.ankamagames.dofus.core.model.BotInfo;
import com.ankamagames.dofus.core.model.Command;
import com.ankamagames.dofus.core.movement.CellData;
import com.ankamagames.dofus.core.movement.CellMovement;
import com.ankamagames.dofus.core.movement.Movement;
import com.ankamagames.dofus.core.network.DofusConnector;

/**
 * Handler external to the game. It will be only actions that the player/client wants.
 */
public class ApiHandler {

    private static final Logger log = Logger.getLogger(ApiHandler.class);

    private DofusConnector connector;

    public static final String CONNECT = "connect";
    public static final String MOVE = "move";
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
            case MOVE:
                handleMoveMessage(command.getParameters());
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

    @SuppressWarnings("unchecked")
    private void handleMoveMessage(final Map<String, Object> parameters) {
        com.ankamagames.dofus.core.movement.Map map = new com.ankamagames.dofus.core.movement.Map();
        map.setUsingNewMovementSystem((Boolean) parameters.get("isUsingNewMovementSystem"));

        List<CellData> cells = ((List<List<Object>>) parameters.get("cells"))
            .stream()
            .map(cell ->
                new CellData(
                    (boolean) cell.get(0),
                    (boolean) cell.get(1),
                    (int) cell.get(2),
                    (int) cell.get(3),
                    (boolean) cell.get(4),
                    (int) cell.get(5)
                )
            )
            .collect(Collectors.toList());
        map.setCells(cells);

        Movement movement = new Movement(connector, map);
        int targetCell = (Integer) parameters.get("target_cell");
        CellMovement mov = movement.moveToCell(targetCell);

        if (mov == null) {
            throw new Error(String.format("Cannot move from %s to %s on map %s",
                this.connector.getBotInfo().getCellId(), targetCell, this.connector.getBotInfo().getMapId())
            );
        }

        try {
            mov.performMovement();
        } catch (Exception e) {
            throw new Error("Failed to move", e);
        }
    }


}
