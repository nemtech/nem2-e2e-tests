package io.nem.automationHelpers.auth;

import io.nem.automationHelpers.network.SocketClient;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PublicKey;

/**
 * Verifies a connection with a catapult server.
 * @class Verifier
 *
 * @fires status Messages about verification progress.
 * @fires verify The verification result.
 */

public class VerifyServer {

    VerifyServerHandler verifyHandler;

    public VerifyServer(SocketClient socket, KeyPair clientKeyPair, KeyPair serverKeyPair, ConnectionSecurityMode mode) {
        this.verifyHandler = new VerifyServerHandler(socket, clientKeyPair, serverKeyPair, mode);
    }

    public void verifyConnection() throws VerifyPeerException {
        this.verifyHandler.process();
    }
}
