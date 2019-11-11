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
import java.nio.ByteBuffer;

/** Binary layout for an embedded transfer transaction. */
public final class EmbeddedTransferTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Transfer transaction body. */
    private final TransferTransactionBodyBuilder transferTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedTransferTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.transferTransactionBody = TransferTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param recipientAddress Recipient address.
     * @param mosaics Attached mosaics.
     * @param message Attached message.
     */
    protected EmbeddedTransferTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final UnresolvedAddressDto recipientAddress, final List<UnresolvedMosaicBuilder> mosaics, final ByteBuffer message) {
        super(signerPublicKey, version, network, type);
        this.transferTransactionBody = TransferTransactionBodyBuilder.create(recipientAddress, mosaics, message);
    }

    /**
     * Creates an instance of EmbeddedTransferTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param recipientAddress Recipient address.
     * @param mosaics Attached mosaics.
     * @param message Attached message.
     * @return Instance of EmbeddedTransferTransactionBuilder.
     */
    public static EmbeddedTransferTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final UnresolvedAddressDto recipientAddress, final List<UnresolvedMosaicBuilder> mosaics, final ByteBuffer message) {
        return new EmbeddedTransferTransactionBuilder(signerPublicKey, version, network, type, recipientAddress, mosaics, message);
    }

    /**
     * Gets recipient address.
     *
     * @return Recipient address.
     */
    public UnresolvedAddressDto getRecipientAddress() {
        return this.transferTransactionBody.getRecipientAddress();
    }

    /**
     * Gets attached mosaics.
     *
     * @return Attached mosaics.
     */
    public List<UnresolvedMosaicBuilder> getMosaics() {
        return this.transferTransactionBody.getMosaics();
    }

    /**
     * Gets attached message.
     *
     * @return Attached message.
     */
    public ByteBuffer getMessage() {
        return this.transferTransactionBody.getMessage();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.transferTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedTransferTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedTransferTransactionBuilder.
     */
    public static EmbeddedTransferTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedTransferTransactionBuilder(stream);
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
            final byte[] transferTransactionBodyBytes = this.transferTransactionBody.serialize();
            dataOutputStream.write(transferTransactionBodyBytes, 0, transferTransactionBodyBytes.length);
        });
    }
}
