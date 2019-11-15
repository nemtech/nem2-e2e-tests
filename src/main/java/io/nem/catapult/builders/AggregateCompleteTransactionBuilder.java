/**
*** Copyright (c) 2016-present,
*** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
***
*** This file is part of Catapult.
***
*** Catapult is free software: you can redistribute it and/or modify
*** it under the terms of the GNU Lesser General Public License as published by
*** the Free Software Foundation, either version 3 of the License, or
*** (at your option) any later version.
***
*** Catapult is distributed in the hope that it will be useful,
*** but WITHOUT ANY WARRANTY; without even the implied warranty of
*** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*** GNU Lesser General Public License for more details.
***
*** You should have received a copy of the GNU Lesser General Public License
*** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
**/

package io.nem.catapult.builders;

import java.io.DataInputStream;
import java.util.List;

/** Binary layout for an aggregate complete transaction. */
public final class AggregateCompleteTransactionBuilder extends TransactionBuilder {
    /** Aggregate transaction body. */
    private final AggregateTransactionBodyBuilder aggregateTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AggregateCompleteTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.aggregateTransactionBody = AggregateTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param transactionsHash Aggregate hash of an aggregate's transactions.
     * @param transactions Sub-transaction data (transactions are variable sized and payload size is in bytes).
     * @param cosignatures Cosignatures data (fills remaining body space after transactions).
     */
    protected AggregateCompleteTransactionBuilder(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final Hash256Dto transactionsHash, final List<EmbeddedTransactionBuilder> transactions, final List<CosignatureBuilder> cosignatures) {
        super(signature, signerPublicKey, version, network, type, fee, deadline);
        this.aggregateTransactionBody = AggregateTransactionBodyBuilder.create(transactionsHash, transactions, cosignatures);
    }

    /**
     * Creates an instance of AggregateCompleteTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param transactionsHash Aggregate hash of an aggregate's transactions.
     * @param transactions Sub-transaction data (transactions are variable sized and payload size is in bytes).
     * @param cosignatures Cosignatures data (fills remaining body space after transactions).
     * @return Instance of AggregateCompleteTransactionBuilder.
     */
    public static AggregateCompleteTransactionBuilder create(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final Hash256Dto transactionsHash, final List<EmbeddedTransactionBuilder> transactions, final List<CosignatureBuilder> cosignatures) {
        return new AggregateCompleteTransactionBuilder(signature, signerPublicKey, version, network, type, fee, deadline, transactionsHash, transactions, cosignatures);
    }

    /**
     * Gets aggregate hash of an aggregate's transactions.
     *
     * @return Aggregate hash of an aggregate's transactions.
     */
    public Hash256Dto getTransactionsHash() {
        return this.aggregateTransactionBody.getTransactionsHash();
    }

    /**
     * Gets sub-transaction data (transactions are variable sized and payload size is in bytes).
     *
     * @return Sub-transaction data (transactions are variable sized and payload size is in bytes).
     */
    public List<EmbeddedTransactionBuilder> getTransactions() {
        return this.aggregateTransactionBody.getTransactions();
    }

    /**
     * Gets cosignatures data (fills remaining body space after transactions).
     *
     * @return Cosignatures data (fills remaining body space after transactions).
     */
    public List<CosignatureBuilder> getCosignatures() {
        return this.aggregateTransactionBody.getCosignatures();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.aggregateTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of AggregateCompleteTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AggregateCompleteTransactionBuilder.
     */
    public static AggregateCompleteTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new AggregateCompleteTransactionBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] superBytes = super.serialize();
            dataOutputStream.write(superBytes, 0, superBytes.length);
            final byte[] aggregateTransactionBodyBytes = this.aggregateTransactionBody.serialize();
            dataOutputStream.write(aggregateTransactionBodyBytes, 0, aggregateTransactionBodyBytes.length);
        });
    }
}
