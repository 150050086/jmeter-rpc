package com.example.jmeter;

import com.example.rpc.ClientRPC;

/* Singleton approach */
class LoadTest {
    private static LoadTest new_instance = null;
    private ClientRPC clientRPC;

    private LoadTest(String node_addr, String username, String password) throws Exception{
        clientRPC = new ClientRPC(node_addr, username, password);
    }

    static LoadTest getInstance(String node_addr, String username, String password) throws Exception{
        if(node_addr == null)
            throw new Exception("node_addr param is null");
        if(username == null)
            throw new Exception("username param is null");
        if(password == null)
            throw new Exception("password param is null");

        if(new_instance == null){
            System.out.println("Creating new instance");
            new_instance = new LoadTest(node_addr, username, password);
        }
        else
            System.out.println("Using old instance");

        new_instance = new LoadTest(node_addr, username, password);
        return new_instance;
    }

    String generateLoad(int iouValue, String otherParty) throws Exception{
        if(clientRPC.createTransaction(iouValue, otherParty))
            return "Success";
        else
            throw new Exception("Failed");
    }

}