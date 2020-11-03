package org.p2p.solanaj.rpc;

public class RpcException extends Exception {
    private final static long serialVersionUID = 8315999767009642193L;

    public RpcException(String message) {
        super(message);
    }
}
