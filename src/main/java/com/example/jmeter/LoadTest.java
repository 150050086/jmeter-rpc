package com.example.jmeter;

import com.example.rpc.ClientRPC;

class LoadTest {
    private ClientRPC clientRPC;

    LoadTest(String node_addr, String username, String password) throws Exception{
        if(node_addr == null)
            throw new Exception("node_addr param is null");
        if(username == null)
            throw new Exception("username param is null");
        if(password == null)
            throw new Exception("password param is null");

        clientRPC = new ClientRPC(node_addr, username, password);
    }

    String generateLoad(int iouValue, String otherParty) throws Exception{
        if(clientRPC.createTransaction(iouValue, otherParty))
            return "Success";
        else
            throw new Exception("Failed");
    }

}