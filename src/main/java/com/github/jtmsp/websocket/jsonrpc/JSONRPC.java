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
package com.github.jtmsp.websocket.jsonrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.jtmsp.websocket.ByteUtil;

/**
 * Specification of Tendermint-Node JSON objects
 */
public class JSONRPC {

    public String jsonrpc = "2.0";
    public String method;
    public List<String> params;
    public String id;

    public static long ID = 0;

    /**
     * Empty constructor for marshallers
     */
    public JSONRPC() {
        // empty
    }

    /**
     * Creates a new rpc call with 0 Parameters
     * @param method the endpoint methond
     */
    public JSONRPC(Method method) {
        this.method = method.getMethodString();
        this.params = new ArrayList<String>();
        this.id = "" + ID;

        ID++;
    }

    /**
     * Creates a new rpc call
     * @param method the endpoint method
     * @param params the parameters for this method
     */
    public JSONRPC(Method method, List<String> params) {
        this.method = method.getMethodString();
        this.params = params;
        this.id = "" + ID;

        ID++;
    }

    /**
     * Creates a new rpc call
     * @param method the endpoint method
     * @param params the parameters for this method
     */
    public JSONRPC(Method method, String... params) {
        this(method, Arrays.asList(params));
    }

    /**
     * Creates a new rpc call
     * @param method the endpoint methond
     * @param marshalledObject an already json-marchalled object
     */
    public JSONRPC(Method method, String marshalledObject) {
        this.method = method.getMethodString();
        this.id = "" + ID;

        List<String> ps = new ArrayList<>();
        ps.add(ByteUtil.toString00(marshalledObject.getBytes()));
        this.params = ps;

        ID++;
    }

    /**
     * Creates a new rpc call
     * @param method the endpoint methond
     * @param bytes a byte-array for parameter
     */
    public JSONRPC(Method method, byte[] bytes) {
        this.method = method.getMethodString();
        this.id = "" + ID;

        List<String> ps = new ArrayList<>();
        ps.add(ByteUtil.toString00(bytes));
        this.params = ps;

        ID++;
    }

    @Override
    public String toString() {
        return "JSONRPC [jsonrpc=" + jsonrpc + ", method=" + method + ", params=" + params + ", id=" + id + "]";
    }
}
