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

import com.github.jtmsp.websocket.Websocket;
import com.github.jtmsp.websocket.jsonrpc.JSONRPC;

public class StartupWebsocket {

    public static void main(String[] args) throws InterruptedException {

        // create the websocket
        Websocket ws = new Websocket();

        System.out.println("waiting for open");
        ws.reconnectWebsocket();
        while (!ws.isOpen()) {
            Thread.sleep(200);
        }

        System.out.println("sending message");
        ws.sendMessage(JSONRPC.broadcastTXAsync("this is my testmessage"), response -> {
            System.out.println("i got a response");
        });

        // wait some time, so we can receive a response
        Thread.sleep(5000);

        System.out.println("disconnecting");
        ws.disconnect();
    }
}
