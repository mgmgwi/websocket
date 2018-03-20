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
package com.github.jtmsp.websocket.jsonrpc;

/**
 * Resultobject when communicating with tendermint node
 */
public class JSONRPCResult extends JSONRPC {

    // 0x0 bytes are for the blockchain
    public static final int ResultTypeGenesis = 0x01;
    public static final int ResultTypeBlockchainInfo = 0x02;
    public static final int ResultTypeBlock = 0x03;

    // 0x2 bytes are for the network
    public static final int ResultTypeStatus = 0x20;
    public static final int ResultTypeNetInfo = 0x21;
    public static final int ResultTypeDialSeeds = 0x22;

    // 0x4 bytes are for the consensus
    public static final int ResultTypeValidators = 0x40;
    public static final int ResultTypeDumpConsensusState = 0x41;

    // 0x6 bytes are for txs / the application
    public static final int ResultTypeBroadcastTx = 0x60;
    public static final int ResultTypeUnconfirmedTxs = 0x61;

    // 0x8 bytes are for events
    public static final int ResultTypeSubscribe = 0x80;
    public static final int ResultTypeUnsubscribe = 0x81;
    public static final int ResultTypeEvent = 0x82;

    // 0xa bytes for testing
    public static final int ResultTypeUnsafeSetConfig = 0xa0;
    public static final int ResultTypeUnsafeStartCPUProfiler = 0xa1;
    public static final int ResultTypeUnsafeStopCPUProfiler = 0xa2;
    public static final int ResultTypeUnsafeWriteHeapProfile = 0xa3;
    public static final int ResultTypeUnsafeFlushMempool = 0xa4;

    private ResultPayload result;
    private ErrorPayload error;

    public ResultPayload getResult() {
        return result;
    }

    public ErrorPayload getError() {
        return error;
    }

    public JSONRPCResult() {
        super("result");
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean hasResult() {
        return result != null;
    }

    @Override
    public String toString() {
        return "JSONRPCResult [result=" + result + ", error=" + error + ", jsonrpc=" + jsonrpc + ", method=" + method + ", id=" + id + "]";
    }

}
