package com.example.rpc;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import net.corda.core.identity.CordaX500Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            final SignedTransaction signedTx = rpcOps
                    .startTrackedFlowDynamic(com.example.flow.ExampleFlow.Initiator.class, iouValue, otherParty)
                    .getReturnValue()
                    .get();

            return true;

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return false;
        }

    }
}