package com.ankamagames.dofus.core.movement;

import java.util.List;

import com.ankamagames.dofus.core.network.DofusConnector;
import com.ankamagames.dofus.network.messages.game.context.GameMapMovementConfirmMessage;
import com.ankamagames.dofus.network.messages.game.context.GameMapMovementRequestMessage;
import com.ankamagames.dofus.util.ThreadUtils;

public class CellMovement {

    public int startCell;
    public int endCell;
    public MovementPath path;
    private DofusConnector connector;

    public CellMovement(MovementPath path, DofusConnector connector) {
        // Movement
        this.startCell = path.CellStart.CellId;
        this.endCell = path.CellEnd.CellId;
        this.path = path;
        this.connector = connector;
    }

    public void performMovement() throws Exception {
        if (path == null) return;

        List<Integer> keys = MapMovementAdapter.GetServerMovement(path);

        this.connector.sendToServer(new GameMapMovementRequestMessage(keys, this.connector.getBotInfo().getMapId()));
        if (!this.connector.getBotInfo().isFighting()) {
            if (path.Cells.size() >= 4) {
                int time = MovementVelocity.getPathVelocity(path, MovementVelocity.MovementTypeEnum.RUNNING);
                ThreadUtils.sleep(time);
            } else if (this.connector.getBotInfo().isRidingMount()) {
                int time = MovementVelocity.getPathVelocity(path, MovementVelocity.MovementTypeEnum.MOUNTED);
                ThreadUtils.sleep(time);
            } else {
                int time = MovementVelocity.getPathVelocity(path, MovementVelocity.MovementTypeEnum.WALKING);
                ThreadUtils.sleep(time);
            }

            this.connector.sendToServer(new GameMapMovementConfirmMessage());
        } else {
            if (path.Cells.size() >= 3) {
                int time = MovementVelocity.getPathVelocity(path, MovementVelocity.MovementTypeEnum.RUNNING);
                ThreadUtils.sleep(time);
            } else {
                int time = MovementVelocity.getPathVelocity(path, MovementVelocity.MovementTypeEnum.WALKING);
                ThreadUtils.sleep(time);
            }
        }
    }
}
