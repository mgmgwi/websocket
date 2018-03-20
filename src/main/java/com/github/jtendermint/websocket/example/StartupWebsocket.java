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
package com.github.jtendermint.websocket.example;

import com.github.jtendermint.websocket.Websocket;
import com.github.jtendermint.websocket.WebsocketException;
import com.github.jtendermint.websocket.jsonrpc.JSONRPC;
import com.github.jtendermint.websocket.jsonrpc.Method;
import com.github.jtendermint.websocket.jsonrpc.calls.StringParam;

public class StartupWebsocket {

    public static void main(String[] args) throws InterruptedException, WebsocketException {

        // create the websocket
        Websocket ws = new Websocket();

        System.out.println("waiting for open");
        ws.reconnectWebsocket();
        while (!ws.isOpen()) {
            Thread.sleep(200);
        }

        for (int i = 0; i < 10; i++) {

            final byte[] b = new byte[] { new Integer(i).byteValue() };

            JSONRPC tx = new StringParam(Method.BROADCAST_TX_COMMIT, b);
            ws.sendMessage(tx, result -> {

                System.out.println("TX: " + com.github.jtendermint.crypto.ByteUtil.toString00(b));
                if (result.hasResult()) {
                    System.out.println(result.getResult());
                } else if (result.hasError()) {
                    System.out.println(result.getError());
                }

                System.out.println();
            });

        }

        // wait some time, so we can receive a response
        Thread.sleep(5000);

        System.out.println("disconnecting");
        ws.disconnect();
    }
}
