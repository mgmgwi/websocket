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

import java.util.Map;

/**
 * Resultobject inside the resultobject XD
 */
public class JSONRPCResultObject {

    public int code;
    public CodeType codeType;
    public String data;
    public String log;

    /**
     * Convert the hashmap found in json to this object
     * 
     * @param hashmap
     * @return
     */
    public static JSONRPCResultObject get(Map<String, Object> hashmap) {
        Double code = (Double) hashmap.get("code");
        String data = (String) hashmap.get("data");
        String log = (String) hashmap.get("log");

        CodeType ct = CodeType.forNumber(code.intValue());

        JSONRPCResultObject o = new JSONRPCResultObject();
        o.code = code.intValue();
        o.codeType = ct;
        o.data = data;
        o.log = log;
        return o;
    }

    /**
     * Convert the hashmap found in json to this object
     * 
     * @param hashmap
     * @return
     */
    @SuppressWarnings("unchecked")
    public static JSONRPCResultObject get(Object object) {
        return get((Map<String, Object>) object);
    }
}
