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

import java.util.Map;

import com.github.jtmsp.websocket.Websocket;
import com.github.jtmsp.websocket.WebsocketException;
import com.github.jtmsp.websocket.jsonrpc.JSONRPC;
import com.github.jtmsp.websocket.jsonrpc.Method;
import com.github.jtmsp.websocket.jsonrpc.calls.StringParam;

public class StartupWebsocket {

    public static void main(String[] args) throws InterruptedException, WebsocketException {

//        {
//            System.out.println("target: BC278E0416E162CE6406A951A3D2C9AAE3C272F4");
//
//            // target BC278E0416E162CE6406A951A3D2C9AAE3C272F4
//            final byte[] b = new byte[] { new Integer(0).byteValue() };
//            byte[] b2 = new byte[] { 1, 1, b[0] };
//
//            System.out.println(RipeMD160.hashToStringBytes(b));
//            System.out.println(RipeMD160.hashToStringBytes(b2));
//
//            System.exit(0);
//        }

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
                Map<String, Object> rs = (Map<String, Object>) result.result.get(1);

                rs.forEach((key, value) -> {
                    System.out.println(key + " : " + value);
                });

                // JSONRPCResultObject resOb = JSONRPCResultObject.get(result.result.get(1));
                // System.out.println(resOb.data);

                System.out.println();
            });

        }

        // hash=BC278E0416E162CE6406A951A3D2C9AAE3C272F4
        // hash=F17854A977F6FA7EEA1BD758E296710B86F72F3D
        // hash=E286960272CE828CA930B0CFD5A62270216D2CC0
        // hash=3AD11182CAEB2826616A2B32C5153D43C14BCD29
        // hash=75F74A61F382B4A9D2F50C1E9987FCB58BB59E12

        // wait some time, so we can receive a response
        Thread.sleep(5000);

        System.out.println("disconnecting");
        ws.disconnect();
    }
}
