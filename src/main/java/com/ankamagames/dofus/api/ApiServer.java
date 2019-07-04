package com.ankamagames.dofus.api;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.ankamagames.dofus.core.model.Command;

/**
 * A simple WebSocketServer to communicate with the client.
 */
public class ApiServer extends WebSocketServer {

    private static final Logger log = Logger.getLogger(ApiServer.class);

    private ApiHandler handler;

    public ApiServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.debug(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has connected!");
        this.handler = new ApiHandler(this);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log.debug(conn + " has disconnected! Exiting...");
        System.exit(1);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Command command = Command.deserialize(message);
        log.debug(command);
        handler.handleMessage(command);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log.error(ex);
        System.exit(1);
    }

    @Override
    public void onStart() {
        log.info("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}
