package io.nem.automationHelpers.Infrastructure;

import io.nem.automationHelpers.network.SocketClient;
import io.nem.automationHelpers.packet.Packet;
import io.nem.automationHelpers.packet.PacketType;
import io.nem.sdk.model.transaction.SignedTransaction;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TransactionConnection {

    SocketClient socketClient;

    public TransactionConnection(SocketClient socket)
    {
        this.socketClient = socket;
    }

    public void announce(SignedTransaction transaction) throws IOException, DecoderException
    {
        ByteBuffer ph = Packet.CreatePacketByteBuffer(PacketType.PUSH_TRANSACTIONS, Hex.decodeHex(transaction.getPayload()));
        socketClient.Write(ph);
    }
}
