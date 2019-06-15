package com.ankamagames.dofus.core.network;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ankamagames.dofus.network.NetworkMessage;
import com.ankamagames.dofus.network.messages.connection.HelloConnectMessage;
import com.ankamagames.dofus.network.messages.connection.IdentificationMessage;
import com.ankamagames.dofus.network.types.version.Version;
import com.ankamagames.dofus.network.types.version.VersionExtended;
import com.ankamagames.dofus.util.FilesUtils;

/**
 * Handler specific to the game. The client will not have to worry about those.
 */
public class Handler {

    private static final Logger log = Logger.getLogger(Handler.class);

    private DofusConnector dofusConnector;

    public Handler(final DofusConnector dofusConnector) {
        this.dofusConnector = dofusConnector;
    }

    public void handleMessage(final NetworkMessage message, final int id) throws Exception {
        switch (id) {
            case 3:
                handleHelloConnectMessage((HelloConnectMessage) message);
                break;
        }
    }

    private void handleHelloConnectMessage(final HelloConnectMessage message) throws Exception {
        byte[] key = new byte[message.getKey().size()];
        for (int i = 0; i < message.getKey().size(); i++) {
            key[i] = message.getKey().get(i).byteValue();
        }

        Version version = FilesUtils.getVersion();
        VersionExtended versionExtended = new VersionExtended(
            version.getMajor(),
            version.getMinor(),
            version.getRelease(),
            version.getRevision(),
            version.getPatch(),
            0, 1, 1
        );

        byte[] credentials = Crypto.encrypt(
            key,
            dofusConnector.getBotInfo().getAccount(),
            dofusConnector.getBotInfo().getPassword(),
            message.getSalt()
        );

        List<Integer> credentialsArray = new ArrayList<>();
        for (byte b : credentials) {
            credentialsArray.add((int) b);
        }

        IdentificationMessage identificationMessage = new IdentificationMessage(
            versionExtended,
            "fr",
            credentialsArray,
            0,
            true,
            false,
            false,
            0,
            new ArrayList<>()
        );

        this.dofusConnector.sendToServer(identificationMessage, IdentificationMessage.PROTOCOL_ID);
    }
}
