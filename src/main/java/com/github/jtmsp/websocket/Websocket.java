package com.github.jtmsp.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.glassfish.tyrus.ext.client.java8.SessionBuilder;

import com.github.jtmsp.websocket.jsonrpc.JSONRPC;
import com.github.jtmsp.websocket.jsonrpc.JSONRPCResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Websocket wrapper for tendermint JSON-RPC
 * 
 * @author @wolfposd
 *
 */
public class Websocket {

    public static String DEFAULT_DESTINATION = "ws://127.0.0.1:46657/websocket";

    private Session wsSession;
    private Map<String, WSResponse> callbacks = new HashMap<>();
    private Gson gson = new Gson();
    private URI destination;
    private WebsocketStatus status;

    /**
     * Creates a new websocket to the default destination<br>
     * Websocket must be opened with {@link #reconnectWebsocket()}
     */
    public Websocket() {
        this(null);
    }

    /**
     * Creates a new websocket to the default destination<br>
     * Websocket must be opened with {@link #reconnectWebsocket()}
     * 
     * @param status
     *            will be notified about status changes, can be
     *            <code>null</code>
     */
    public Websocket(WebsocketStatus status) {
        try {
            destination = new URI(DEFAULT_DESTINATION);
        } catch (URISyntaxException e) {
        }
        this.status = status;

        if (this.status == null)
            this.status = new WebsocketStatus() {
            };
    }

    /**
     * Creates a new websocket to the destination<br>
     * Websocket must be opened with {@link #reconnectWebsocket()}
     * 
     * @param destination
     *            destination URI
     * @param status
     *            will be notified about status changes, can be
     *            <code>null</code>
     */
    public Websocket(URI destination, WebsocketStatus status) {
        this.destination = destination;
        this.status = status;
        if (this.status == null)
            this.status = new WebsocketStatus() {
            };
    }

    /**
     * Tries to open this websocket, if its already opened nothing happens
     */
    public void reconnectWebsocket() {

        if (wsSession == null || !wsSession.isOpen()) {
            try {
                wsSession = new SessionBuilder().uri(destination) //
                        .onOpen(this::onOpen) //
                        .onError(this::onError) //
                        .onClose(this::onClose) //
                        .messageHandler(String.class, this::onMessage) //
                        .connect();
            } catch (IOException | DeploymentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Disconnects this websocket<br>
     * It will send a NORMAL_CLOSURE to the WebsocketStatus
     */
    public void disconnect() {
        try {
            if (wsSession != null) {
                wsSession.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Manual Close"));
            }
        } catch (IOException e) {
        }
    }

    /**
     * Is this websocket connection open?
     */
    public boolean isOpen() {
        return wsSession.isOpen();
    }

    /**
     * Sends a message towards the node, notifies the callback on response
     * 
     * @param rpc
     *            message to send
     * @param callback
     *            callback to notify
     */
    public void sendMessage(JSONRPC rpc, WSResponse callback) {
        callbacks.put(rpc.id, callback);
        wsSession.getAsyncRemote().sendText(gson.toJson(rpc));
    }

    private void onOpen(Session s, EndpointConfig ec) {
        status.wasOpened();
    }

    private void onError(Session s, Throwable t) {
        status.hadError(t);
    }

    private void onClose(Session s, CloseReason cr) {
        status.wasClosed(cr);
    }

    private void onMessage(String m) {
        try {
            JSONRPCResult result = gson.fromJson(m, JSONRPCResult.class);

            WSResponse cb = callbacks.get(result.id);
            if (cb != null) {
                cb.onJSONRPCResult(result);
                callbacks.remove(result.id);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
}
