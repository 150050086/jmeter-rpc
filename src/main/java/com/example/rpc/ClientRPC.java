package com.example.rpc;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;
import net.corda.core.identity.CordaX500Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kotlin.jvm.functions.Function1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class ClientRPC {
    private static final Logger logger = LoggerFactory.getLogger(ClientRPC.class);
    private static CordaRPCOps rpcOps;

    public ClientRPC(String node_addr, String username, String password){
        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse(node_addr);
        final CordaRPCClient client = new CordaRPCClient(nodeAddress, CordaRPCClientConfiguration.DEFAULT);
        final CordaRPCConnection connection = client.start(username, password);
        rpcOps = connection.getProxy();
    }

    public boolean createTransaction(int iouValue, String partyName){
        if (iouValue <= 0 ) {
            return false;
        }
        if (partyName == null) {
            return false;
        }
        Party otherParty = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse(partyName));
        if(otherParty == null)
            return false;

        try {
            String rpcid = Integer.toString(RpcId.getRpcId());
            /*
            final SignedTransaction signedTx = rpcOps
                    .startTrackedFlowDynamicDebug(com.example.flow.ExampleFlow.Initiator.class, rpcid, iouValue, otherParty)
                    .getReturnValue()
                    .get();
            */

            final CordaFuture future = rpcOps
                    .startTrackedFlowDynamicDebug(com.example.flow.ExampleFlow.Initiator.class, rpcid, iouValue, otherParty)
                    .getReturnValue();

            future.then(new Function1<CordaFuture, Void>() {
                @Override
                public Void invoke(CordaFuture f) {
                    try {
                        long start = System.currentTimeMillis();
                        String home_dir = System.getProperty("user.home");
                        BufferedWriter out = new BufferedWriter(new FileWriter(Paths.get(home_dir, "PartyA_rpc.log").toString(), true));
                        out.write("RPC_REQUEST_END " + rpcid + " " + Long.toString(start) + "\n");
                        out.close();
                    }
                    catch (IOException e) {
                        System.out.println("exception occoured" + e);
                    }
                    return null;
                }
            });

            future.get();
            return true;

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            System.out.println(msg);
            logger.error(msg, ex);
            return false;
        }
    }
}