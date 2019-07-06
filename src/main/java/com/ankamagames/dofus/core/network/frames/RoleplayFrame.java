package com.ankamagames.dofus.core.network.frames;

import com.ankamagames.dofus.core.network.DofusConnector;
import com.ankamagames.dofus.network.messages.game.context.GameMapMovementMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.CurrentMapMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.MapComplementaryInformationsDataInHavenBagMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.MapComplementaryInformationsDataMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.MapInformationsRequestMessage;
import com.ankamagames.dofus.network.types.game.context.roleplay.GameRolePlayActorInformations;

public class RoleplayFrame extends Frame {

    public RoleplayFrame(final DofusConnector connector) {
        super(connector);
    }

    public void process(final CurrentMapMessage message) throws Exception {
        this.connector.sendToServer(new MapInformationsRequestMessage(message.getMapId()));
    }

    public void process(final MapComplementaryInformationsDataMessage message){
        GameRolePlayActorInformations bot = message
            .getActors()
            .stream()
            .filter(actor -> actor.getContextualId() == this.connector.getBotInfo().getId())
            .findFirst()
            .get();

        this.connector.getBotInfo().setCellId(bot.getDisposition().getCellId());
        this.connector.getBotInfo().setMapId(message.getMapId());
    }

    public void process(final MapComplementaryInformationsDataInHavenBagMessage message){
        GameRolePlayActorInformations bot = message
            .getActors()
            .stream()
            .filter(actor -> actor.getContextualId() == this.connector.getBotInfo().getId())
            .findFirst()
            .get();

        this.connector.getBotInfo().setCellId(bot.getDisposition().getCellId());
    }

    public void process(final GameMapMovementMessage message){
        if (message.getActorId() == this.connector.getBotInfo().getId()){
            this.connector.getBotInfo().setCellId(message.getKeyMovements().get(message.getKeyMovements().size() - 1));
        }
    }
}
