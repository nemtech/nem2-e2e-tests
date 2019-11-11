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

/** Binary layout for an embedded hash lock transaction. */
public final class EmbeddedHashLockTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Hash lock transaction body. */
    private final HashLockTransactionBodyBuilder hashLockTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedHashLockTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.hashLockTransactionBody = HashLockTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param mosaic Lock mosaic.
     * @param duration Number of blocks for which a lock should be valid.
     * @param hash Lock hash.
     */
    protected EmbeddedHashLockTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final UnresolvedMosaicBuilder mosaic, final BlockDurationDto duration, final Hash256Dto hash) {
        super(signerPublicKey, version, network, type);
        this.hashLockTransactionBody = HashLockTransactionBodyBuilder.create(mosaic, duration, hash);
    }

    /**
     * Creates an instance of EmbeddedHashLockTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param mosaic Lock mosaic.
     * @param duration Number of blocks for which a lock should be valid.
     * @param hash Lock hash.
     * @return Instance of EmbeddedHashLockTransactionBuilder.
     */
    public static EmbeddedHashLockTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final UnresolvedMosaicBuilder mosaic, final BlockDurationDto duration, final Hash256Dto hash) {
        return new EmbeddedHashLockTransactionBuilder(signerPublicKey, version, network, type, mosaic, duration, hash);
    }

    /**
     * Gets lock mosaic.
     *
     * @return Lock mosaic.
     */
    public UnresolvedMosaicBuilder getMosaic() {
        return this.hashLockTransactionBody.getMosaic();
    }

    /**
     * Gets number of blocks for which a lock should be valid.
     *
     * @return Number of blocks for which a lock should be valid.
     */
    public BlockDurationDto getDuration() {
        return this.hashLockTransactionBody.getDuration();
    }

    /**
     * Gets lock hash.
     *
     * @return Lock hash.
     */
    public Hash256Dto getHash() {
        return this.hashLockTransactionBody.getHash();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.hashLockTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedHashLockTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedHashLockTransactionBuilder.
     */
    public static EmbeddedHashLockTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedHashLockTransactionBuilder(stream);
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
            final byte[] hashLockTransactionBodyBytes = this.hashLockTransactionBody.serialize();
            dataOutputStream.write(hashLockTransactionBodyBytes, 0, hashLockTransactionBodyBytes.length);
        });
    }
}
