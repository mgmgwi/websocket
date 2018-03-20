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
package com.github.jtendermint.websocket.jsonrpc;

/**
 * Tendermint-JSON-RPC Methods<br>
 * see <a href="https://github.com/tendermint/tendermint/wiki/RPC#endpoints">RPC#Endpoints</a> for more info
 */
public enum Method {

    /** query the node-app, path=x, data=x, prove={true/false}; use: MixedParam(ABCI_QUERY, new Object[]{"value","value",true})    */
    ABCI_QUERY("abci_query"), //
    NET_INFO("net_info"), //
    STATUS("status"), //
    DUMP_CONSENSUS_STATE("dump_consensus_state"), //
    /** Shows unconfirmed transactions*/
    UNCONFIRMED_TXS("unconfirmed_txs"), //
    /** number of unconfirmed txs*/
    NUM_UNCONFIRMED_TXS("num_unconfirmed_txs"), //
    /** takes TX as parameter */
    BROADCAST_TX_SYNC("broadcast_tx_sync"), //
    /** takes TX as parameter */
    BROADCAST_TX_ASYNC("broadcast_tx_async"), //
    /** takes TX as parameter */
    BROADCAST_TX_COMMIT("broadcast_tx_commit"), //
    /** Retrieves information about the given block, takes height as parameter */
    BLOCK_HEIGHT("block"), //
    /** subscribes to an event, takes event as parameter*/
    SUBSCRIBE_EVENT("subscribe"), //
    /** Query a TX by its hash, hash=X prove={true/false}; use: MixedParam(TX, new Object[]{"HASHVALUEHERE",true})*/
    TX("tx");

    private final String methodString;

    Method(String method) {
        this.methodString = method;
    }

    public String getMethodString() {
        return methodString;
    }

}