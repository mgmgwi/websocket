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
package com.github.jtmsp.websocket.jsonrpc.calls;

import com.github.jtmsp.websocket.ByteUtil;
import com.github.jtmsp.websocket.jsonrpc.JSONRPC;
import com.github.jtmsp.websocket.jsonrpc.Method;

public class StringParam extends JSONRPC {

    public final String[] params;

    public StringParam(Method m, String tx00Formatted) {
        super(m);
        params = new String[] { tx00Formatted };
    }

    public StringParam(Method m, byte[] bytes) {
        super(m);
        params = new String[] { ByteUtil.toString00(bytes) };
    }

    public StringParam(Method m, byte[][] bytes) {
        super(m);

        params = new String[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            params[i] = ByteUtil.toString00(bytes[i]);
        }
    }

    public StringParam(Method m, String[] tx00Formatted) {
        super(m);
        params = tx00Formatted;
    }
}
