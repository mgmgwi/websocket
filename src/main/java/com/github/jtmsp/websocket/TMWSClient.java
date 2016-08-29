/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 
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
package com.github.jtmsp.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.Framedata.Opcode;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;

import com.github.jtmsp.websocket.jsonrpc.JSONRPC;
import com.github.jtmsp.websocket.jsonrpc.JSONRPCResult;
import com.google.gson.Gson;

public class TMWSClient extends WebSocketClient {

    private final Map<String, WSResponse> callbacks = new WeakHashMap<>();

    private final Gson gson = new Gson();

    private final Set<WSListener> listeners = new HashSet<WSListener>();

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /**
     * Creates a new Websocket client
     * @param url
     * @throws URISyntaxException If the URI is malformed
     */
    public TMWSClient(String url) throws URISyntaxException {
        this(new URI(url));
    }

    /**
     * Creates a new Websocket client
     * @param serverURI
     */
    public TMWSClient(URI serverURI) {
        super(serverURI, new Draft_17());
        executorService.scheduleAtFixedRate(() -> sendPing(), 7, 7, TimeUnit.SECONDS);
    }

    public void addListener(WSListener l) {
        listeners.add(l);
    }

    public void removeListener(WSListener l) {
        listeners.remove(l);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        listeners.forEach(l -> l.onOpen(handshakedata));
    }

    @Override
    public void onMessage(String message) {
        JSONRPCResult r = gson.fromJson(message, JSONRPCResult.class);
        if (r.id != null && !r.id.isEmpty()) {
            WSResponse cb = callbacks.get(r.id);
            if (cb != null) {
                cb.onJSONRPCResult(r);
            }
        }
    }

    public void send(JSONRPC jsonRpc, WSResponse callback) throws NotYetConnectedException {
        if (jsonRpc.id == null || jsonRpc.id.isEmpty())
            throw new IllegalArgumentException("jsonRPC.id cannot be null or empty");

        if (callback != null) {
            callbacks.put(jsonRpc.id, callback);
        }
        super.send(gson.toJson(jsonRpc));
    }

    /**
     * Use {@link #send(JSONRPC, WSResponse)} instead
     */
    @Override
    @Deprecated
    public void send(byte[] data) throws NotYetConnectedException {
        throw new IllegalAccessError("do not use this method, use send(JSONRPC,WSResponse) instead");
    }

    /**
     * Use {@link #send(JSONRPC, WSResponse)} instead
     */
    @Override
    @Deprecated
    public void send(String text) throws NotYetConnectedException {
        throw new IllegalAccessError("do not use this method, use send(JSONRPC,WSResponse) instead");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        executorService.shutdownNow();
        listeners.forEach(l -> l.onClose(code, getCodeName(code), reason, remote));
    }

    @Override
    public void onError(Exception ex) {
        listeners.forEach(l -> l.onError(ex));
    }

    @Override
    public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response)
            throws InvalidDataException {
        listeners.forEach(l -> l.onHandshake(request, response));
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata f) {
        super.onWebsocketPing(conn, f);
        listeners.forEach(l -> l.onPingReceived(conn, f));
    }

    @Override
    public void onWebsocketPong(WebSocket conn, Framedata f) {
        listeners.forEach(l -> l.onPongReceived(conn, f));
    }

    private String getCodeName(int code) {
        String codeName;
        switch (code) {
            case CloseFrame.NORMAL:
                codeName = "NORMAL";
                break;
            case CloseFrame.ABNORMAL_CLOSE:
                codeName = "ABNORMAL_CLOSE";
                break;
            case CloseFrame.BUGGYCLOSE:
                codeName = "BUGGYCLOSE";
                break;
            case CloseFrame.PROTOCOL_ERROR:
                codeName = "PROTOCOL_ERROR";
                break;
            default:
                codeName = "" + code;
        }
        return codeName;
    }

    private void sendPing() {
        FramedataImpl1 f = new FramedataImpl1(Opcode.PING);
        f.setFin(true);
        this.getConnection().sendFrame(f);
        listeners.forEach(l -> l.onPingSent());
    }

    public interface WSListener {

        default void onOpen(ServerHandshake handshakedata) {
        }

        default void onPingReceived(WebSocket conn, Framedata f) {
        }

        default void onPongReceived(WebSocket conn, Framedata f) {
        }

        default void onPingSent() {
        }

        default void onHandshake(ClientHandshake request, ServerHandshake response) {
        }

        default void onError(Exception ex) {
        }

        default void onClose(int code, String codeName, String reason, boolean remote) {
        }
    }

}
