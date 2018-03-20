/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2017 - 2018
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.jtendermint.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Decoder;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.Session;
import javax.websocket.ClientEndpointConfig.Configurator;

import org.glassfish.tyrus.ext.client.java8.SessionBuilder;

import com.github.jtendermint.websocket.jsonrpc.JSONRPC;
import com.github.jtendermint.websocket.jsonrpc.JSONRPCResult;
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

    private SessionBuilder builder;

    /**
     * Creates a new websocket to the default destination<br>
     * Websocket must be opened with {@link #reconnectWebsocket()}
     */
    public Websocket() {
        this(DEFAULT_DESTINATION, null);
    }

    /**
     * Creates a new websocket to the default destination<br>
     * Websocket must be opened with {@link #reconnectWebsocket()}
     * 
     * @param status
     *            will be notified about status changes, can be <code>null</code>
     * @throws URISyntaxException 
     */
    public Websocket(WebsocketStatus status) {
        this(DEFAULT_DESTINATION, status);
    }

    
    public Websocket(String uriString, WebsocketStatus status) {
        try {
            setup(status, new URI(uriString));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Creates a new websocket to the destination<br>
     * Websocket must be opened with {@link #reconnectWebsocket()}
     * 
     * @param destination
     *            destination URI
     * @param status
     *            will be notified about status changes, can be <code>null</code>
     */
    public Websocket(URI destination, WebsocketStatus status) {
       setup(status, destination);
    }

    private void setup(WebsocketStatus status, URI destination) {
        this.destination = destination;
        this.status = status;
        if (this.status == null)
            this.status = new WebsocketStatus() {
            };

        builder = new SessionBuilder().uri(destination) //
                .onOpen(this::onOpen) //
                .onError(this::onError) //
                .onClose(this::onClose) //
                .messageHandler(String.class, this::onMessage);
    }
    
    /**
     * Tries to open this websocket, if its already opened nothing happens
     * 
     * @throws WebsocketException
     */
    public void reconnectWebsocket() throws WebsocketException {

        System.out.println("wssession: " + wsSession);
        if (wsSession != null) {
            System.out.println("is open? " + wsSession.isOpen());
        }
        
        if (wsSession == null || !wsSession.isOpen()) {
            System.out.println("connecting");
            try {
                wsSession = builder.connect();
            } catch (IOException | DeploymentException e) {
                throw new WebsocketException(e);
            }
        }
    }

    /**
     * Tries to open this websocket, if its already opened nothing happens
     * 
     * @throws WebsocketException
     */
    public void connect() throws WebsocketException {
        this.reconnectWebsocket();
    }
    
    /**
     * Connect to the remote (server) endpoint asynchronously.<br>
     * Does not append callbacks or responses, need to handle manually<br>
     * Raw websocket session
     * 
     * @return Future of session
     */
    public CompletableFuture<Session> connectAsync() {
        return builder.connectAsync();
    }

    /**
     * Connect to the remote (server) endpoint asynchronously.<br>
     * Does not append callbacks or responses, need to handle manually<br>
     * Raw websocket session
     * 
     * @param service
     *            executor service used for executing the connect() method.
     * @return completable future returning Session when created.
     */
    public CompletableFuture<Session> connectExecutor(ExecutorService service) {
        return builder.connectAsync(service);
    }

    /**
     * Disconnects this websocket<br>
     * It will send a NORMAL_CLOSURE to the WebsocketStatus
     * 
     * @throws WebsocketException
     */
    public void disconnect() throws WebsocketException {
        try {
            if (wsSession != null) {
                wsSession.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Manual Close"));
            }
        } catch (IOException e) {
            throw new WebsocketException(e);
        }
    }

    /**
     * Is this websocket connection open?
     */
    public boolean isOpen() {
        return wsSession != null && wsSession.isOpen();
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
            status.hadError(e);
        }
    }
}
