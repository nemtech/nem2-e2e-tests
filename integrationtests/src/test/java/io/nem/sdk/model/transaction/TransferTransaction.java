/*
 * Copyright 2018 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.EntityTypeBuilder;
import io.nem.catapult.builders.TransferTransactionBuilder;
import io.nem.catapult.builders.UnresolvedMosaicBuilder;
import com.google.flatbuffers.FlatBufferBuilder;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.Validate;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The transfer transactions object contain data about transfers of mosaics and message to another account.
 *
 * @since 1.0
 */
public class TransferTransaction extends Transaction {
    private final Address recipient;
    private final List<Mosaic> mosaics;
    private final Message message;
    private final Schema schema = new TransferTransactionSchema();

    public TransferTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Address recipient, List<Mosaic> mosaics, Message message, String signature, PublicAccount signer, TransactionInfo transactionInfo) {
        this(networkType, version, deadline, fee, recipient, mosaics, message, Optional.of(signature), Optional.of(signer), Optional.of(transactionInfo));
    }

    public TransferTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Address recipient, List<Mosaic> mosaics, Message message) {
        this(networkType, version, deadline, fee, recipient, mosaics, message, Optional.empty(), Optional.empty(), Optional.empty());
    }

    private TransferTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, Address recipient, List<Mosaic> mosaics, Message message, Optional<String> signature, Optional<PublicAccount> signer, Optional<TransactionInfo> transactionInfo) {
        super(TransactionType.TRANSFER, networkType, version, deadline, fee, signature, signer, transactionInfo);
        Validate.notNull(recipient, "Recipient must not be null");
        Validate.notNull(mosaics, "Mosaics must not be null");
        Validate.notNull(message, "Message must not be null");
        this.recipient = recipient;
        this.mosaics = mosaics;
        this.message = message;
    }

    /**
     * Create a transfer transaction object.
     *
     * @param deadline    - The deadline to include the transaction.
     * @param recipient   - The recipient of the transaction.
     * @param mosaics     - The array of mosaics.
     * @param message     - The transaction message.
     * @param networkType - The network type.
     * @return a TransferTransaction instance
     */
    public static TransferTransaction create(Deadline deadline, Address recipient, List<Mosaic> mosaics, Message message, NetworkType networkType) {
        return new TransferTransaction(networkType, 3, deadline, BigInteger.valueOf(0), recipient, mosaics, message);
    }

    /**
     * Returns address of the recipient.
     *
     * @return recipient address
     */
    public Address getRecipient() {
        return recipient;
    }

    /**
     * Returns list of mosaic objects.
     *
     * @return Link<{ @ link   Mosaic }>
     */
    public List<Mosaic> getMosaics() {
        return mosaics;
    }

    /**
     * Returns transaction message.
     *
     * @return Message
     */
    public Message getMessage() {
        return message;
    }

    byte[] generateBytes() {
        TransferTransactionBuilder txBuilder = new TransferTransactionBuilder();
        txBuilder.setDeadline(getDeadline().getInstant());
        txBuilder.setFee(0);

        // Create Mosaics
        ArrayList<UnresolvedMosaicBuilder> unresolvedMosaicArrayList = new ArrayList<>(mosaics.size());
        for (int i = 0; i < mosaics.size(); ++i) {
            Mosaic mosaic = mosaics.get(i);
            UnresolvedMosaicBuilder mosaicBuilder = new UnresolvedMosaicBuilder();
            mosaicBuilder.setAmount(mosaic.getAmount().longValue());
            mosaicBuilder.setMosaicid(mosaic.getId().getId().longValue());
            unresolvedMosaicArrayList.add(mosaicBuilder);
        }
        txBuilder.setMosaics(unresolvedMosaicArrayList);

        // Create Message
        final byte byteMessageType = (byte)message.getType();
        final byte[] bytePayload = message.getPayload().getBytes(StandardCharsets.UTF_8) ;
        final ByteBuffer messageBuffer = ByteBuffer.allocate(bytePayload.length + 1 /* for the message type */);
        messageBuffer.put(byteMessageType);
        messageBuffer.put(bytePayload);
        txBuilder.setMessage(messageBuffer);
        txBuilder.setType(EntityTypeBuilder.TRANSFER_TRANSACTION_BUILDER);

        final byte[] address = new Base32().decode(getRecipient().plain().getBytes(StandardCharsets.UTF_8));
        final ByteBuffer addressBuffer = ByteBuffer.wrap(address);
        txBuilder.setRecipient(addressBuffer);

        final int version = (int) Long.parseLong(Integer.toHexString(getNetworkType().getValue()) + "0" + Integer.toHexString(getVersion()), 16);
        txBuilder.setVersion((short)version);

        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        txBuilder.setSigner(signerBuffer);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);
        txBuilder.setSignature(signatureBuffer);

        byte[] bytes = null;
        try {
            bytes = txBuilder.serialize();
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
            sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
            sizeBuffer.putInt(bytes.length);
            System.arraycopy(sizeBuffer.array(), 0, bytes, 0, 4);
        }catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return bytes;
    }
}
