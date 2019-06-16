package com.ankamagames.dofus.core.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import com.ankamagames.dofus.core.mapper.VersionMapper;
import com.ankamagames.dofus.core.model.Command;
import com.ankamagames.dofus.network.NetworkMessage;
import com.ankamagames.dofus.network.enums.ServerStatusEnum;
import com.ankamagames.dofus.network.messages.connection.HelloConnectMessage;
import com.ankamagames.dofus.network.messages.connection.IdentificationMessage;
import com.ankamagames.dofus.network.messages.connection.SelectedServerDataMessage;
import com.ankamagames.dofus.network.messages.connection.ServerSelectionMessage;
import com.ankamagames.dofus.network.messages.connection.ServersListMessage;
import com.ankamagames.dofus.network.messages.game.approach.AuthenticationTicketAcceptedMessage;
import com.ankamagames.dofus.network.messages.game.approach.AuthenticationTicketMessage;
import com.ankamagames.dofus.network.messages.game.approach.HelloGameMessage;
import com.ankamagames.dofus.network.messages.game.basic.BasicLatencyStatsMessage;
import com.ankamagames.dofus.network.messages.game.basic.SequenceNumberMessage;
import com.ankamagames.dofus.network.messages.game.basic.SequenceNumberRequestMessage;
import com.ankamagames.dofus.network.messages.game.character.choice.BasicCharactersListMessage;
import com.ankamagames.dofus.network.messages.game.character.choice.CharacterSelectionMessage;
import com.ankamagames.dofus.network.messages.game.character.choice.CharactersListMessage;
import com.ankamagames.dofus.network.messages.game.character.choice.CharactersListRequestMessage;
import com.ankamagames.dofus.network.messages.game.context.GameContextCreateMessage;
import com.ankamagames.dofus.network.messages.game.context.GameContextCreateRequestMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.CurrentMapMessage;
import com.ankamagames.dofus.network.messages.game.context.roleplay.MapInformationsRequestMessage;
import com.ankamagames.dofus.network.messages.game.initialization.CharacterLoadingCompleteMessage;
import com.ankamagames.dofus.network.messages.security.CheckIntegrityMessage;
import com.ankamagames.dofus.network.messages.security.ClientKeyMessage;
import com.ankamagames.dofus.network.messages.security.RawDataMessage;
import com.ankamagames.dofus.network.messages.web.haapi.HaapiApiKeyRequestMessage;
import com.ankamagames.dofus.network.messages.web.haapi.HaapiSessionMessage;
import com.ankamagames.dofus.network.types.connection.GameServerInformations;
import com.ankamagames.dofus.network.types.game.character.choice.CharacterBaseInformations;
import com.ankamagames.dofus.network.types.version.Version;
import com.ankamagames.dofus.network.types.version.VersionExtended;
import com.ankamagames.dofus.util.FilesUtils;


import static com.ankamagames.dofus.api.ApiHandler.CONNECT;
import static com.ankamagames.dofus.api.ApiHandler.SERVER_DOWN;
import static com.ankamagames.dofus.api.ApiHandler.STATUS;

/**
 * Handler specific to the game. The client will not have to worry about those.
 */
public class Handler {

    private static final Logger log = Logger.getLogger(Handler.class);

    private DofusConnector connector;
    private List<Integer> ticket;


    public Handler(final DofusConnector connector) {
        this.connector = connector;
    }

    public void handleMessage(final NetworkMessage message, final int id) throws Exception {
        switch (id) {
            case HelloConnectMessage.PROTOCOL_ID:
                handleHelloConnectMessage((HelloConnectMessage) message);
                break;
            case ServersListMessage.PROTOCOL_ID:
                handleServersListMessage((ServersListMessage) message);
                break;
            case HelloGameMessage.PROTOCOL_ID:
                handleHelloGameMessage();
                break;
            case RawDataMessage.PROTOCOL_ID:
                handleRawDataMessage();
                break;
            case SelectedServerDataMessage.PROTOCOL_ID:
                handleSelectedServerDataMessage((SelectedServerDataMessage) message);
                break;
            case SequenceNumberRequestMessage.PROTOCOL_ID:
                handleSequenceNumberRequestMessage();
                break;
            case BasicCharactersListMessage.PROTOCOL_ID:
                handleBasicLatencyStatsRequestMessage();
                break;
            case HaapiSessionMessage.PROTOCOL_ID:
                handleHaapiSessionMessage();
                break;
            case AuthenticationTicketAcceptedMessage.PROTOCOL_ID:
                handleAuthenticationTicketAcceptedMessage();
                break;
            case CharactersListMessage.PROTOCOL_ID:
                handleCharacterListMessage((CharactersListMessage) message);
                break;
            case CharacterLoadingCompleteMessage.PROTOCOL_ID:
                handleCharacterLoadingCompleteMessage();
                break;
            case CurrentMapMessage.PROTOCOL_ID:
                handleCurrentMapMessage((CurrentMapMessage) message);
                break;

        }
    }

    private void handleHelloConnectMessage(final HelloConnectMessage message) throws Exception {
        byte[] key = new byte[message.getKey().size()];
        for (int i = 0; i < message.getKey().size(); i++) {
            key[i] = message.getKey().get(i).byteValue();
        }

        Version version = FilesUtils.getVersion();
        version.setBuildType(0);

        VersionExtended versionExtended = VersionMapper.versionToVersionExtended(version);
        versionExtended.setInstall(1);
        versionExtended.setTechnology(1);

        byte[] credentials = Crypto.encrypt(
            key,
            connector.getBotInfo().getAccount(),
            connector.getBotInfo().getPassword(),
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

        this.connector.sendToServer(identificationMessage);
    }

    private void handleServersListMessage(final ServersListMessage message) throws Exception {
        Optional<GameServerInformations> serverInfoOpt = message.getServers().stream()
            .filter(server -> server.getId() == this.connector.getBotInfo().getServerId())
            .findFirst();

        if (!serverInfoOpt.isPresent()) {
            throw new Error("Server ID does not exist");
        }

        GameServerInformations serverInfo = serverInfoOpt.get();

        if (serverInfo.getStatus() != ServerStatusEnum.ONLINE.value() &&
            serverInfo.getStatus() != ServerStatusEnum.STATUS_UNKNOWN.value()) {
            Command command = new Command(CONNECT, Map.of(STATUS, SERVER_DOWN));
            this.connector.getServer().broadcast(Command.serialize(command));
        }

        this.connector.sendToServer(new ServerSelectionMessage(this.connector.getBotInfo().getServerId()));
    }

    private void handleHelloGameMessage() throws Exception {
        byte[] encryptedTicket = new byte[ticket.size()];
        for (int i = 0; i < ticket.size(); i++) {
            encryptedTicket[i] = ticket.get(i).byteValue();
        }
        ticket = null;
        String decryptedTicket = Crypto.decryptAESkey(encryptedTicket);
        this.connector.sendToServer(new AuthenticationTicketMessage("fr", decryptedTicket));
    }

    private void handleRawDataMessage() throws Exception {
        List<Integer> tt = new ArrayList<>();
        for (int i = 0; i <= 255; i++) {
            int rand = ThreadLocalRandom.current().nextInt(-127, 127);
            tt.add(rand);
        }
        this.connector.sendToServer(new CheckIntegrityMessage(tt));
    }

    private void handleSelectedServerDataMessage(final SelectedServerDataMessage message) throws IOException {
        ticket = message.getTicket();
        this.connector.getSocket().close();
        this.connector.setSocket(new Socket(message.getAddress(), message.getPorts().get(0)));
    }

    private void handleSequenceNumberRequestMessage() throws Exception {
        this.connector.sendToServer(new SequenceNumberMessage(this.connector.getLatencyFrame().sequence++));
    }

    private void handleBasicLatencyStatsRequestMessage() throws Exception {
        BasicLatencyStatsMessage basicLatencyStatsMessage = new BasicLatencyStatsMessage(
            this.connector.getLatencyFrame().getLatencyAvg(),
            this.connector.getLatencyFrame().getSamplesCount(),
            this.connector.getLatencyFrame().GetSamplesMax()
        );
        this.connector.sendToServer(basicLatencyStatsMessage);
    }

    private void handleHaapiSessionMessage() throws Exception {
        this.connector.sendToServer(new HaapiApiKeyRequestMessage());
    }

    private void handleAuthenticationTicketAcceptedMessage() throws Exception {
        this.connector.sendToServer(new CharactersListRequestMessage());
    }

    private void handleCharacterListMessage(final CharactersListMessage message) throws Exception {
        Optional<CharacterBaseInformations> characterOpt = message.getCharacters().stream()
            .filter(charac -> charac.getName().equals(this.connector.getBotInfo().getName()))
            .findFirst();

        if (!characterOpt.isPresent()) {
            throw new Error("Character not available");
        }

        CharacterBaseInformations character = characterOpt.get();

        this.connector.sendToServer(new CharacterSelectionMessage(character.getId()));
    }

    private void handleCharacterLoadingCompleteMessage() throws Exception {
        String key = FlashKeyGenerator.getRandomFlashKey(this.connector.getBotInfo().getName());
        this.connector.sendToServer(new ClientKeyMessage(key));
        this.connector.sendToServer(new GameContextCreateRequestMessage());
    }

    private void handleCurrentMapMessage(final CurrentMapMessage message) throws Exception {
        this.connector.sendToServer(new MapInformationsRequestMessage(message.getMapId()));
    }
}
