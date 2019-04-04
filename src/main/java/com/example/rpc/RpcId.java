package com.example.rpc;

import java.util.Random;

public class RpcId {
    static Random random = new Random(System.currentTimeMillis());

    static int getRpcId(){
        return random.nextInt(Integer.MAX_VALUE);
    }
}
