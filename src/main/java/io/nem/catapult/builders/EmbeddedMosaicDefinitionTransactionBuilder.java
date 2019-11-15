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
import java.util.EnumSet;

/** Binary layout for an embedded mosaic definition transaction. */
public final class EmbeddedMosaicDefinitionTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Mosaic definition transaction body. */
    private final MosaicDefinitionTransactionBodyBuilder mosaicDefinitionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedMosaicDefinitionTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.mosaicDefinitionTransactionBody = MosaicDefinitionTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param id Mosaic identifier.
     * @param duration Mosaic duration.
     * @param nonce Mosaic nonce.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     */
    protected EmbeddedMosaicDefinitionTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final MosaicIdDto id, final BlockDurationDto duration, final MosaicNonceDto nonce, final EnumSet<MosaicFlagsDto> flags, final byte divisibility) {
        super(signerPublicKey, version, network, type);
        this.mosaicDefinitionTransactionBody = MosaicDefinitionTransactionBodyBuilder.create(id, duration, nonce, flags, divisibility);
    }

    /**
     * Creates an instance of EmbeddedMosaicDefinitionTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param id Mosaic identifier.
     * @param duration Mosaic duration.
     * @param nonce Mosaic nonce.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @return Instance of EmbeddedMosaicDefinitionTransactionBuilder.
     */
    public static EmbeddedMosaicDefinitionTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final MosaicIdDto id, final BlockDurationDto duration, final MosaicNonceDto nonce, final EnumSet<MosaicFlagsDto> flags, final byte divisibility) {
        return new EmbeddedMosaicDefinitionTransactionBuilder(signerPublicKey, version, network, type, id, duration, nonce, flags, divisibility);
    }

    /**
     * Gets mosaic identifier.
     *
     * @return Mosaic identifier.
     */
    public MosaicIdDto getId() {
        return this.mosaicDefinitionTransactionBody.getId();
    }

    /**
     * Gets mosaic duration.
     *
     * @return Mosaic duration.
     */
    public BlockDurationDto getDuration() {
        return this.mosaicDefinitionTransactionBody.getDuration();
    }

    /**
     * Gets mosaic nonce.
     *
     * @return Mosaic nonce.
     */
    public MosaicNonceDto getNonce() {
        return this.mosaicDefinitionTransactionBody.getNonce();
    }

    /**
     * Gets mosaic flags.
     *
     * @return Mosaic flags.
     */
    public EnumSet<MosaicFlagsDto> getFlags() {
        return this.mosaicDefinitionTransactionBody.getFlags();
    }

    /**
     * Gets mosaic divisibility.
     *
     * @return Mosaic divisibility.
     */
    public byte getDivisibility() {
        return this.mosaicDefinitionTransactionBody.getDivisibility();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.mosaicDefinitionTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedMosaicDefinitionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedMosaicDefinitionTransactionBuilder.
     */
    public static EmbeddedMosaicDefinitionTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedMosaicDefinitionTransactionBuilder(stream);
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
            final byte[] mosaicDefinitionTransactionBodyBytes = this.mosaicDefinitionTransactionBody.serialize();
            dataOutputStream.write(mosaicDefinitionTransactionBodyBytes, 0, mosaicDefinitionTransactionBodyBytes.length);
        });
    }
}
