package io.nem.automationHelpers.network;

import io.nem.automationHelpers.auth.ConnectionSecurityMode;
import io.nem.automationHelpers.auth.VerifyPeerException;
import io.nem.automationHelpers.auth.VerifyServer;
import io.nem.core.crypto.KeyPair;

import java.net.Socket;

public class AuthenticatedSocket extends SocketClient {

    KeyPair keyPair = new KeyPair();

    public AuthenticatedSocket(Socket socket, KeyPair keyPairServer) throws VerifyPeerException {
        super(socket);
        VerifyServer verifyServer = new VerifyServer(this, keyPair, keyPairServer, ConnectionSecurityMode.NONE);
        verifyServer.verifyConnection();
    }

    public static AuthenticatedSocket CreateAuthenticatedSocket(SocketClient socketClient, KeyPair keyPairServer) throws VerifyPeerException {
        return new AuthenticatedSocket(socketClient.socket, keyPairServer);
    }
}
