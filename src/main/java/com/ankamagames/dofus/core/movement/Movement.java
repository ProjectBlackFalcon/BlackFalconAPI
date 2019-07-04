package com.ankamagames.dofus.core.movement;

import java.util.ArrayList;
import java.util.List;

import com.ankamagames.dofus.core.network.DofusConnector;
import com.ankamagames.dofus.util.Astar;


public class Movement {

    private DofusConnector connector;
    private Map map;

    public Movement(final DofusConnector connector, final Map map) {
        this.connector = connector;
        this.map = map;
    }

    public CellMovement moveToCell(int cellId) {
        if (this.map.getCells().get(cellId).isMov()) {
            if (this.connector.getBotInfo().isFighting()) {
                return new CellMovement(new Pathfinder(this.map).findPath(this.connector.getBotInfo().getCellId(), cellId, false, false), this.connector);
            } else {
                return new CellMovement(new Pathfinder(this.map).findPath(this.connector.getBotInfo().getCellId(), cellId), this.connector);
            }
        } else {
            return null;
        }
    }

    private boolean noObstacle(int random) {
        if (random == this.connector.getBotInfo().getCellId())
            return true;
        List<int[]> blocked = new ArrayList<>();
        for (int i = 0; i < this.map.getCells().size(); i++) {
            if (!this.map.getCells().get(i).isMov()) {
                blocked.add(new int[]{i % 14, i / 14});
            }
        }
        if (this.connector.getBotInfo().isFighting()) {
            //TODO ADD Monster to blocked cells (check DATBOT)
        }
        Astar a;
        if (this.connector.getBotInfo().isFighting()) {
            a = new Astar(this.connector.getBotInfo().getCellId() % 14, this.connector.getBotInfo().getCellId() / 14, random % 14, random / 14, blocked, false);
        } else {
            a = new Astar(this.connector.getBotInfo().getCellId() % 14, this.connector.getBotInfo().getCellId() / 14, random % 14, random / 14, blocked, true);
        }
        return a.getPath() != null;
    }

}
