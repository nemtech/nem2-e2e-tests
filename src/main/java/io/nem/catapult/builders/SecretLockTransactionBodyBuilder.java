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

/** Binary layout for a secret lock transaction. */
public final class SecretLockTransactionBodyBuilder {
    /** Secret. */
    private final Hash256Dto secret;
    /** Locked mosaic. */
    private final UnresolvedMosaicBuilder mosaic;
    /** Number of blocks for which a lock should be valid. */
    private final BlockDurationDto duration;
    /** Hash algorithm. */
    private final LockHashAlgorithmDto hashAlgorithm;
    /** Locked mosaic recipient address. */
    private final UnresolvedAddressDto recipientAddress;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected SecretLockTransactionBodyBuilder(final DataInputStream stream) {
        this.secret = Hash256Dto.loadFromBinary(stream);
        this.mosaic = UnresolvedMosaicBuilder.loadFromBinary(stream);
        this.duration = BlockDurationDto.loadFromBinary(stream);
        this.hashAlgorithm = LockHashAlgorithmDto.loadFromBinary(stream);
        this.recipientAddress = UnresolvedAddressDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param secret Secret.
     * @param mosaic Locked mosaic.
     * @param duration Number of blocks for which a lock should be valid.
     * @param hashAlgorithm Hash algorithm.
     * @param recipientAddress Locked mosaic recipient address.
     */
    protected SecretLockTransactionBodyBuilder(final Hash256Dto secret, final UnresolvedMosaicBuilder mosaic, final BlockDurationDto duration, final LockHashAlgorithmDto hashAlgorithm, final UnresolvedAddressDto recipientAddress) {
        GeneratorUtils.notNull(secret, "secret is null");
        GeneratorUtils.notNull(mosaic, "mosaic is null");
        GeneratorUtils.notNull(duration, "duration is null");
        GeneratorUtils.notNull(hashAlgorithm, "hashAlgorithm is null");
        GeneratorUtils.notNull(recipientAddress, "recipientAddress is null");
        this.secret = secret;
        this.mosaic = mosaic;
        this.duration = duration;
        this.hashAlgorithm = hashAlgorithm;
        this.recipientAddress = recipientAddress;
    }

    /**
     * Creates an instance of SecretLockTransactionBodyBuilder.
     *
     * @param secret Secret.
     * @param mosaic Locked mosaic.
     * @param duration Number of blocks for which a lock should be valid.
     * @param hashAlgorithm Hash algorithm.
     * @param recipientAddress Locked mosaic recipient address.
     * @return Instance of SecretLockTransactionBodyBuilder.
     */
    public static SecretLockTransactionBodyBuilder create(final Hash256Dto secret, final UnresolvedMosaicBuilder mosaic, final BlockDurationDto duration, final LockHashAlgorithmDto hashAlgorithm, final UnresolvedAddressDto recipientAddress) {
        return new SecretLockTransactionBodyBuilder(secret, mosaic, duration, hashAlgorithm, recipientAddress);
    }

    /**
     * Gets secret.
     *
     * @return Secret.
     */
    public Hash256Dto getSecret() {
        return this.secret;
    }

    /**
     * Gets locked mosaic.
     *
     * @return Locked mosaic.
     */
    public UnresolvedMosaicBuilder getMosaic() {
        return this.mosaic;
    }

    /**
     * Gets number of blocks for which a lock should be valid.
     *
     * @return Number of blocks for which a lock should be valid.
     */
    public BlockDurationDto getDuration() {
        return this.duration;
    }

    /**
     * Gets hash algorithm.
     *
     * @return Hash algorithm.
     */
    public LockHashAlgorithmDto getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    /**
     * Gets locked mosaic recipient address.
     *
     * @return Locked mosaic recipient address.
     */
    public UnresolvedAddressDto getRecipientAddress() {
        return this.recipientAddress;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.secret.getSize();
        size += this.mosaic.getSize();
        size += this.duration.getSize();
        size += this.hashAlgorithm.getSize();
        size += this.recipientAddress.getSize();
        return size;
    }

    /**
     * Creates an instance of SecretLockTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of SecretLockTransactionBodyBuilder.
     */
    public static SecretLockTransactionBodyBuilder loadFromBinary(final DataInputStream stream) {
        return new SecretLockTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] secretBytes = this.secret.serialize();
            dataOutputStream.write(secretBytes, 0, secretBytes.length);
            final byte[] mosaicBytes = this.mosaic.serialize();
            dataOutputStream.write(mosaicBytes, 0, mosaicBytes.length);
            final byte[] durationBytes = this.duration.serialize();
            dataOutputStream.write(durationBytes, 0, durationBytes.length);
            final byte[] hashAlgorithmBytes = this.hashAlgorithm.serialize();
            dataOutputStream.write(hashAlgorithmBytes, 0, hashAlgorithmBytes.length);
            final byte[] recipientAddressBytes = this.recipientAddress.serialize();
            dataOutputStream.write(recipientAddressBytes, 0, recipientAddressBytes.length);
        });
    }
}
