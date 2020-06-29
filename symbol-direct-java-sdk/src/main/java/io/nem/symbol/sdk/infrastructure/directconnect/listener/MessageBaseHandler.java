package io.nem.symbol.sdk.infrastructure.directconnect.listener;

import org.zeromq.ZMQ.Socket;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public abstract class MessageBaseHandler implements MessageHandler {
	protected DataInputStream toInputStream(final byte[] bytes) {
		return new DataInputStream(new ByteArrayInputStream(bytes));
	}

	protected void failIfMoreMessageAvailable(final Socket socket, final String message) {
		if (socket.hasReceiveMore()) {
			new IllegalStateException("More message available: " + message);
		}
	}
}
