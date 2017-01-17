/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2017 
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
package com.github.jtmsp.websocket.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.glassfish.tyrus.ext.client.java8.SessionBuilder;

import com.github.jtmsp.websocket.ByteUtil;
import com.github.jtmsp.websocket.jsonrpc.JSONRPC;
import com.github.jtmsp.websocket.jsonrpc.JSONRPCResult;
import com.github.jtmsp.websocket.jsonrpc.Method;
import com.github.jtmsp.websocket.jsonrpc.calls.StringParam;
import com.google.gson.Gson;

public class StartupCounterExampleWebsocket {

    public static boolean keepRunning = true;
    private static Gson gson = new Gson();

    public static void main(String[] args) throws InterruptedException {

        new Thread(StartupCounterExampleWebsocket::runloop).start();

        while (keepRunning) {
            Thread.sleep(2000);
        }

    }

    private static void runloop() {
        Session wsSession = null;
        try {
            wsSession = new SessionBuilder().uri(new URI("ws://127.0.0.1:46657/websocket"))//
                    .onOpen(StartupCounterExampleWebsocket::onOpen)//
                    .onClose((s, c) -> {
                        System.out.println("ON CLOSE" + s.getRequestURI() + " reason: " + c.getReasonPhrase());
                    }) //
                    .onError((s, t) -> {
                        System.out.println("ON ERROR:" + s + " throws:" + t.getMessage());
                    }) //
                    .messageHandler(String.class, StartupCounterExampleWebsocket::onMessage)//
                    .connect();
        } catch (IOException | DeploymentException | URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("waiting for open");
        while (!wsSession.isOpen()) {
            sleep(500);
        }

        spamNumbers(wsSession, 400, 500);

    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    private static void onOpen(Session s, EndpointConfig ec) {
        System.out.println("yaaaay, we're open");
    }

    private static void spamNumbers(Session s, int start, int end) {
        for (int i = start; i < end; i++) {
            // prepare JSON-RPC package
            JSONRPC j = new StringParam(Method.BROADCAST_TX_SYNC, ByteUtil.toBytes(i));

            System.out.println("Sending message #" + i);
            s.getAsyncRemote().sendText(gson.toJson(j));

            sleep(1000);
        }

        try {
            s.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Manual Close"));
        } catch (IOException e) {
        }
    }

    private static void onMessage(String message) {
        JSONRPCResult result = gson.fromJson(message, JSONRPCResult.class);
        System.out.println("got answer to #" + result.id);
    }

}
