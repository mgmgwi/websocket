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
package com.github.jtendermint.websocket.example;

import javax.websocket.CloseReason;

import com.github.jtendermint.crypto.ByteUtil;
import com.github.jtendermint.websocket.Websocket;
import com.github.jtendermint.websocket.WebsocketException;
import com.github.jtendermint.websocket.WebsocketStatus;
import com.github.jtendermint.websocket.jsonrpc.JSONRPC;
import com.github.jtendermint.websocket.jsonrpc.Method;
import com.github.jtendermint.websocket.jsonrpc.calls.StringParam;

public class StartupCounterExampleWebsocket {

    public static boolean keepRunning = true;

    public static void main(String[] args) {

        new Thread(StartupCounterExampleWebsocket::runloop).start();

        while (keepRunning) {
            sleep(2000);
        }

    }

    private static void runloop() {

        Websocket ws = new Websocket(new WebsocketStatus() {
            @Override
            public void wasOpened() {
                System.out.println("is open");
            }
            @Override
            public void wasClosed(CloseReason cr) {
                System.out.println("is closed");
                Thread.currentThread().interrupt();
            }
            @Override
            public void hadError(Throwable t) {
                t.printStackTrace();
            }
        });

        while (!ws.isOpen()) {
            try {
                System.out.println("connection try");
                ws.connect();
                System.out.println("sleeping 4000");
                sleep(4000);
            } catch (WebsocketException e) {
                System.err.println(e);
                sleep(4000);
            }
        }

        spamNumbers(ws, 0, 25);

        try {
            ws.disconnect();
        } catch (WebsocketException e) {
            e.printStackTrace();
        }
        keepRunning = false;

    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    private static void spamNumbers(Websocket s, int start, int end) {
        for (int i = start; i < end; i++) {
            // prepare JSON-RPC package
            System.out.println( ByteUtil.toString00(ByteUtil.toBytes(i)));
            JSONRPC j = new StringParam(Method.BROADCAST_TX_SYNC, ByteUtil.toBytes(i));

            System.out.println("Sending message #" + i + "(msg.id:" + j.id + ")");
            s.sendMessage(j, result -> {
                System.out.println(
                        "got answer " + result.id + " haserror?:" + result.hasError() + " hasresult?" + result.hasResult() + " result:" + result.getResult());

                if (result.hasError()) {
                    System.out.println(result.getError());
                }

            });

            sleep(600);
        }
    }
}
