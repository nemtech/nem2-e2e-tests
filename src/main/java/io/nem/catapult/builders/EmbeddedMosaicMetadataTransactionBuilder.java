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
import java.nio.ByteBuffer;

/** Binary layout for an embedded mosaic metadata transaction. */
public final class EmbeddedMosaicMetadataTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Mosaic metadata transaction body. */
    private final MosaicMetadataTransactionBodyBuilder mosaicMetadataTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedMosaicMetadataTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.mosaicMetadataTransactionBody = MosaicMetadataTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param targetPublicKey Metadata target public key.
     * @param scopedMetadataKey Metadata key scoped to source, target and type.
     * @param targetMosaicId Target mosaic identifier.
     * @param valueSizeDelta Change in value size in bytes.
     * @param value Difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value).
     */
    protected EmbeddedMosaicMetadataTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final KeyDto targetPublicKey, final long scopedMetadataKey, final UnresolvedMosaicIdDto targetMosaicId, final short valueSizeDelta, final ByteBuffer value) {
        super(signerPublicKey, version, network, type);
        this.mosaicMetadataTransactionBody = MosaicMetadataTransactionBodyBuilder.create(targetPublicKey, scopedMetadataKey, targetMosaicId, valueSizeDelta, value);
    }

    /**
     * Creates an instance of EmbeddedMosaicMetadataTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param targetPublicKey Metadata target public key.
     * @param scopedMetadataKey Metadata key scoped to source, target and type.
     * @param targetMosaicId Target mosaic identifier.
     * @param valueSizeDelta Change in value size in bytes.
     * @param value Difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value).
     * @return Instance of EmbeddedMosaicMetadataTransactionBuilder.
     */
    public static EmbeddedMosaicMetadataTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final KeyDto targetPublicKey, final long scopedMetadataKey, final UnresolvedMosaicIdDto targetMosaicId, final short valueSizeDelta, final ByteBuffer value) {
        return new EmbeddedMosaicMetadataTransactionBuilder(signerPublicKey, version, network, type, targetPublicKey, scopedMetadataKey, targetMosaicId, valueSizeDelta, value);
    }

    /**
     * Gets metadata target public key.
     *
     * @return Metadata target public key.
     */
    public KeyDto getTargetPublicKey() {
        return this.mosaicMetadataTransactionBody.getTargetPublicKey();
    }

    /**
     * Gets metadata key scoped to source, target and type.
     *
     * @return Metadata key scoped to source, target and type.
     */
    public long getScopedMetadataKey() {
        return this.mosaicMetadataTransactionBody.getScopedMetadataKey();
    }

    /**
     * Gets target mosaic identifier.
     *
     * @return Target mosaic identifier.
     */
    public UnresolvedMosaicIdDto getTargetMosaicId() {
        return this.mosaicMetadataTransactionBody.getTargetMosaicId();
    }

    /**
     * Gets change in value size in bytes.
     *
     * @return Change in value size in bytes.
     */
    public short getValueSizeDelta() {
        return this.mosaicMetadataTransactionBody.getValueSizeDelta();
    }

    /**
     * Gets difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value).
     *
     * @return Difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value).
     */
    public ByteBuffer getValue() {
        return this.mosaicMetadataTransactionBody.getValue();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.mosaicMetadataTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedMosaicMetadataTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedMosaicMetadataTransactionBuilder.
     */
    public static EmbeddedMosaicMetadataTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedMosaicMetadataTransactionBuilder(stream);
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
            final byte[] mosaicMetadataTransactionBodyBytes = this.mosaicMetadataTransactionBody.serialize();
            dataOutputStream.write(mosaicMetadataTransactionBodyBytes, 0, mosaicMetadataTransactionBodyBytes.length);
        });
    }
}
