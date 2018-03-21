package com.github.jtendermint.websocket.jsonrpc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonRPCResultTest {

    private final String abciInfo = "{\"jsonrpc\":\"2.0\",\"id\":\"\",\"result\":{\"response\":{\"data\":\"NO_INFO\"}}}";

    private final String dumpconsensusinfo = "{\"jsonrpc\":\"2.0\",\"id\":\"\",\"result\":{\"round_state\":{\"Height\":39,\"Round\":0,\"Step\":1,\"StartTime\":\"2018-03-21T16:33:18.411755922+01:00\",\"CommitTime\":\"2018-03-21T16:33:17.411755922+01:00\",\"Validators\":{\"validators\":[{\"address\":\"6B0E644EA24E7A20F1443BE814D30E211C179C0A\",\"pub_key\":{\"type\":\"ed25519\",\"data\":\"CAD0A6358D6BE07DB575A8D359DED0731703D65E7651C429072D1A6498CC8B3C\"},\"voting_power\":10,\"accum\":0}],\"proposer\":{\"address\":\"6B0E644EA24E7A20F1443BE814D30E211C179C0A\",\"pub_key\":{\"type\":\"ed25519\",\"data\":\"CAD0A6358D6BE07DB575A8D359DED0731703D65E7651C429072D1A6498CC8B3C\"},\"voting_power\":10,\"accum\":0}},\"Proposal\":null,\"ProposalBlock\":null,\"ProposalBlockParts\":null,\"LockedRound\":0,\"LockedBlock\":null,\"LockedBlockParts\":null,\"Votes\":{},\"CommitRound\":-1,\"LastCommit\":{},\"LastValidators\":{\"validators\":[{\"address\":\"6B0E644EA24E7A20F1443BE814D30E211C179C0A\",\"pub_key\":{\"type\":\"ed25519\",\"data\":\"CAD0A6358D6BE07DB575A8D359DED0731703D65E7651C429072D1A6498CC8B3C\"},\"voting_power\":10,\"accum\":0}],\"proposer\":{\"address\":\"6B0E644EA24E7A20F1443BE814D30E211C179C0A\",\"pub_key\":{\"type\":\"ed25519\",\"data\":\"CAD0A6358D6BE07DB575A8D359DED0731703D65E7651C429072D1A6498CC8B3C\"},\"voting_power\":10,\"accum\":0}}},\"peer_round_states\":{}}}";

    private final String broadcastTxSuccess = "{\"jsonrpc\":\"2.0\",\"id\":\"\",\"result\":{\"code\":0,\"data\":\"\",\"log\":\"\",\"hash\":\"7B24AD4E68DB369FAECA80E65E73DC270C460C44\"}}";

    private final String broadcastTxFail = "{\"jsonrpc\":\"2.0\",\"id\":\"\",\"error\":{\"code\":-32603,\"message\":\"Internal error\",\"data\":\"Error broadcasting transaction: Tx already exists in cache\"}}";

    private Gson gson;

    @Before
    public void setup() {
        gson = new GsonBuilder().create();
    }

    @Test
    public void testBroadCastTxSuccess() {
        JSONRPCResult tx = gson.fromJson(broadcastTxSuccess, JSONRPCResult.class);

        assertEquals(tx.getId(), "");
        assertEquals(tx.hasError(), false);
        assertEquals(tx.hasResult(), true);
        
        assertEquals(tx.getResult().get("code").getAsInt(), CodeType.OK.getNumber());
        assertEquals(tx.getResult().get("data").getAsString(), "");
        assertEquals(tx.getResult().get("log").getAsString(), "");
        assertEquals(tx.getResult().get("hash").getAsString(), "7B24AD4E68DB369FAECA80E65E73DC270C460C44");
    }

    @Test
    public void testBroadCastTxFail() {
        JSONRPCResult tx = gson.fromJson(broadcastTxFail, JSONRPCResult.class);

        assertEquals(tx.getId(), "");
        assertEquals(tx.hasError(), true);
        assertEquals(tx.hasResult(), false);

        assertEquals(-32603, tx.getError().getCode());
        assertEquals("Internal error", tx.getError().getMessage());
        assertEquals("Error broadcasting transaction: Tx already exists in cache", tx.getError().getData());
    }

    @Test
    public void testABCIInfo() {
        JSONRPCResult tx = gson.fromJson(abciInfo, JSONRPCResult.class);
        
        assertEquals(tx.getId(), "");
        assertEquals(tx.hasError(), false);
        assertEquals(tx.hasResult(), true);

        assertEquals(tx.getResult().get("response").getAsJsonObject().get("data").getAsString(), "NO_INFO");
    }
    
    @Test
    public void testDumpConsensusInfo() {
        JSONRPCResult tx = gson.fromJson(dumpconsensusinfo, JSONRPCResult.class);
        
        assertEquals(tx.getId(), "");
        assertEquals(tx.hasError(), false);
        assertEquals(tx.hasResult(), true);
        
        assertEquals(tx.getResult().get("round_state").getAsJsonObject().get("Height").getAsInt(), 39);
    }

}
