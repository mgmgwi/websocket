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
package com.github.jtmsp.websocket.example;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Date;

import org.java_websocket.handshake.ServerHandshake;

import com.github.jtmsp.websocket.TMWSClient;
import com.github.jtmsp.websocket.TMWSClient.WSListener;
import com.github.jtmsp.websocket.jsonrpc.JSONRPC;
import com.github.jtmsp.websocket.jsonrpc.Method;

public class StartupCounterExampleWebsocket {

    public static boolean keepRunning = true;

    public static void main(String[] args) throws InterruptedException {

        new Thread(StartupCounterExampleWebsocket::runloop).start();

        while (keepRunning) {
            Thread.sleep(2000);
        }

    }

    private static void runloop() {
        try {
            System.out.println("Starting Websocket");
            TMWSClient cli = new TMWSClient("http://localhost:46657/websocket");

            // Add some Listeners to see whats going on
            cli.addListener(new WSListener() {
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println(new Date() + " connection is open");
                }

                public void onClose(int code, String codeName, String reason, boolean remote) {
                    System.out.println(new Date() + " was closed: " + codeName + " " + code);
                    keepRunning = false;
                }
            });

            // Start the actual connection
            cli.connectBlocking();

            // Send numbers 0 to 99 in 1 second intervals
            for (int i = 0; i < 100; i++) {
                ByteBuffer b = ByteBuffer.allocate(4);
                b.putInt(i);

                // prepare JSON-RPC package
                JSONRPC j = new JSONRPC(Method.BROADCAST_TX_ASYNC, b.array());

                System.out.println("Sending:+ " + i);
                final int finali = i;
                cli.send(j, c -> {
                    System.out.println("Receiving: " + finali + " " + c.result);
                });

                Thread.sleep(1000);
            }

        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
