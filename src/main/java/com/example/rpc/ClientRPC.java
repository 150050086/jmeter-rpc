package com.example.rpc;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.transactions.SignedTransaction;
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
    private static CordaRPCConnection connection;
    private static CordaRPCOps rpcOps;

    public ClientRPC(String node_addr, String username, String password){
        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse(node_addr);
        final CordaRPCClient client = new CordaRPCClient(nodeAddress, CordaRPCClientConfiguration.DEFAULT);
        connection = client.start(username, password);
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
            final SignedTransaction signedTx = rpcOps
                    .startFlowDynamic(com.example.flow.ExampleFlow.Initiator.class, iouValue, otherParty)
                    .getReturnValue()
                    .get();

            /*
            final FlowHandle<SignedTransaction> handle = rpcOps
                    .startFlowDynamic(com.example.flow.ExampleFlow.Initiator.class, iouValue, otherParty);
            final String flowid = handle.getId().toString();
            final CordaFuture<SignedTransaction> future = handle.getReturnValue();

            future.then(new Function1<CordaFuture<SignedTransaction>, Void>() {
                @Override
                public Void invoke(CordaFuture f) {
                    try {
                        long start = System.currentTimeMillis();
                        String home_dir = System.getProperty("user.home");
                        BufferedWriter out = new BufferedWriter(new FileWriter(Paths.get(home_dir, "Initiator.log").toString(), true));
                        out.write("RPC_REQUEST_END " + flowid + " " + Long.toString(start) + "\n");
                        out.close();
                    }
                    catch (IOException e) {
                        System.out.println("Exception occurred" + e);
                    }
                    return (null);
                }
            });

            future.get();
            */

            // Closing the connection is causing some errors, need to revisit this!
            //connection.notifyServerAndClose();
            return true;

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            System.out.println(msg);
            logger.error(msg, ex);
            return false;
        }
    }
}