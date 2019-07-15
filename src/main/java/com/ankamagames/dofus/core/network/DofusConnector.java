package com.ankamagames.dofus.core.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ankamagames.dofus.api.ApiServer;
import com.ankamagames.dofus.core.model.BotInfo;
import com.ankamagames.dofus.network.NetworkMessage;
import com.ankamagames.dofus.network.utils.DofusDataReader;
import com.ankamagames.dofus.network.utils.DofusDataWriter;
import com.ankamagames.dofus.util.FilesUtils;
import com.ankamagames.dofus.util.NetworkUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DofusConnector implements Runnable {

    private static final Logger log = Logger.getLogger(DofusConnector.class);

    private static final String IP = "52.17.231.202";
    private static final int PORT = 443;

    private Map<String, String> nameId;

    private Socket socket;
    private LatencyFrame latencyFrame = new LatencyFrame();

    private byte[] bigPacketData;
    private Message message;
    private int bigPacketId;
    private int bigPacketLengthToFull;

    private ApiServer server;
    private Processor processor;
    private BotInfo botInfo;

    public DofusConnector(ApiServer server) {
        try {
            this.processor = new Processor(this);
            this.server = server;
            nameId = FilesUtils.parseMessageNameId();
            socket = new Socket(IP, PORT);
        } catch (IOException e) {
            log.error(e);
            System.exit(1);
        }
    }


    @Override
    public void run() {
        try {
            if (socket == null) {
                return;
            }

            while (!this.socket.isClosed()) {
                Thread.sleep(400);
                if (!this.socket.isClosed()) {
                    InputStream data = this.socket.getInputStream();
                    int available = data.available();
                    byte[] buffer = new byte[available];
                    if (available > 0) {
                        latencyFrame.updateLatency();
                        try {
                            data.read(buffer, 0, available);
                            DofusDataReader reader = new DofusDataReader(new ByteArrayInputStream(buffer));
                            buildMessage(reader);
                        } catch (Exception e) {
                            log.error("Socket error", e);
                            System.exit(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Socket closed", e);
            System.exit(1);
        }
    }

    private void buildMessage(DofusDataReader reader) throws Exception {
        if (reader.available() <= 0) {
            return;
        }

        // Packet split
        if (bigPacketLengthToFull != 0) {
            if (reader.available() <= bigPacketLengthToFull) {
                bigPacketLengthToFull -= reader.available();
                byte[] destination = new byte[bigPacketData.length + reader.available()];
                System.arraycopy(bigPacketData, 0, destination, 0, bigPacketData.length);
                System.arraycopy(reader.readBytes(reader.available()), 0, destination, bigPacketData.length, reader.available());
                this.bigPacketData = destination;
            } else if (reader.available() > bigPacketLengthToFull) {
                byte[] destination = new byte[bigPacketData.length + bigPacketLengthToFull];
                System.arraycopy(bigPacketData, 0, destination, 0, bigPacketData.length);
                System.arraycopy(reader.readBytes(bigPacketLengthToFull), 0, destination, bigPacketData.length, bigPacketLengthToFull);
                bigPacketLengthToFull = 0;
                this.bigPacketData = destination;
            }
            if (bigPacketLengthToFull == 0) {
                TreatPacket(bigPacketId, bigPacketData);
                bigPacketData = null;
                bigPacketId = 0;
            }
        } else {
            if (this.message == null) {
                this.message = new Message();
            }
            message.build(reader);
            if (message.getId() != 0 && message.bigPacketLength == 0) {
                TreatPacket(message.getId(), message.getData());
            } else if (message.getId() != 0 && message.bigPacketLength != 0) {
                bigPacketLengthToFull = message.bigPacketLength;
                bigPacketId = message.getId();
                bigPacketData = message.getData();
            }
        }
        this.message = null;
        buildMessage(reader);
    }


    /**
     * Transfer the packets translated to the client and handle some of them.
     *
     * @param id      id of the packet
     * @param content content of the packet
     */
    private void TreatPacket(int id, byte[] content) {
        new Thread(() -> {
            DofusDataReader dataReader = new DofusDataReader(new ByteArrayInputStream(content));
            try {
                String className = nameId.get(String.valueOf(id));
                Object clazz = Class.forName(className).getConstructor().newInstance();
                NetworkMessage message = NetworkMessage.class.cast(clazz);
                message.deserialize(dataReader);
                className = className.substring(className.lastIndexOf(".") + 1);
                log.debug("[Receiving] packet : " + id + " - " + className);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.createObjectNode();
                JsonNode classNode = null;

                if (message.getClass().getDeclaredFields().length > 1 || !message.getClass().getSuperclass().getSimpleName().equals("NetworkMessage")) {
                    classNode = mapper.convertValue(message, JsonNode.class);
                }

                ((ObjectNode) rootNode).put("message", className);
                ((ObjectNode) rootNode).put("id", id);
                ((ObjectNode) rootNode).set("content", classNode);
                server.broadcast(mapper.writeValueAsString(rootNode));
                processor.processMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void sendToServer(NetworkMessage message) throws Exception {
        latencyFrame.latestSent();
        int id = message.getClass().getField("PROTOCOL_ID").getInt(null);
        String messageName = nameId.get(String.valueOf(id)).substring(nameId.get(String.valueOf(id)).lastIndexOf(".") + 1);
        log.debug("[Sending] packet : " + id + " - " + messageName);
        ByteArrayOutputStream bous = new ByteArrayOutputStream();
        DofusDataWriter writer = new DofusDataWriter(bous);
        message.serialize(writer);
        try {
            byte[] wrote = NetworkUtils.writePacket(writer, bous, id);
            socket.getOutputStream().write(wrote);
            socket.getOutputStream().flush();
        } catch (SocketException e) {
            log.error(e);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();
        JsonNode classNode = null;

        if (message.getClass().getDeclaredFields().length > 1 || !message.getClass().getSuperclass().getSimpleName().equals("NetworkMessage")) {
            classNode = mapper.convertValue(message, JsonNode.class);
        }

        ((ObjectNode) rootNode).put("message", messageName);
        ((ObjectNode) rootNode).put("id", id);
        ((ObjectNode) rootNode).set("content", classNode);
        server.broadcast(mapper.writeValueAsString(rootNode));
    }

    public BotInfo getBotInfo() {
        return botInfo;
    }

    public void setBotInfo(final BotInfo botInfo) {
        this.botInfo = botInfo;
    }

    public LatencyFrame getLatencyFrame() {
        return latencyFrame;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(final Socket socket) {
        this.socket = socket;
    }

    public ApiServer getServer() {
        return server;
    }
}
