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

/** Binary layout for a non-embedded secret lock transaction. */
public final class SecretLockTransactionBuilder extends TransactionBuilder {
    /** Secret lock transaction body. */
    private final SecretLockTransactionBodyBuilder secretLockTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected SecretLockTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.secretLockTransactionBody = SecretLockTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param secret Secret.
     * @param mosaic Locked mosaic.
     * @param duration Number of blocks for which a lock should be valid.
     * @param hashAlgorithm Hash algorithm.
     * @param recipientAddress Locked mosaic recipient address.
     */
    protected SecretLockTransactionBuilder(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final Hash256Dto secret, final UnresolvedMosaicBuilder mosaic, final BlockDurationDto duration, final LockHashAlgorithmDto hashAlgorithm, final UnresolvedAddressDto recipientAddress) {
        super(signature, signerPublicKey, version, network, type, fee, deadline);
        this.secretLockTransactionBody = SecretLockTransactionBodyBuilder.create(secret, mosaic, duration, hashAlgorithm, recipientAddress);
    }

    /**
     * Creates an instance of SecretLockTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param secret Secret.
     * @param mosaic Locked mosaic.
     * @param duration Number of blocks for which a lock should be valid.
     * @param hashAlgorithm Hash algorithm.
     * @param recipientAddress Locked mosaic recipient address.
     * @return Instance of SecretLockTransactionBuilder.
     */
    public static SecretLockTransactionBuilder create(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final Hash256Dto secret, final UnresolvedMosaicBuilder mosaic, final BlockDurationDto duration, final LockHashAlgorithmDto hashAlgorithm, final UnresolvedAddressDto recipientAddress) {
        return new SecretLockTransactionBuilder(signature, signerPublicKey, version, network, type, fee, deadline, secret, mosaic, duration, hashAlgorithm, recipientAddress);
    }

    /**
     * Gets secret.
     *
     * @return Secret.
     */
    public Hash256Dto getSecret() {
        return this.secretLockTransactionBody.getSecret();
    }

    /**
     * Gets locked mosaic.
     *
     * @return Locked mosaic.
     */
    public UnresolvedMosaicBuilder getMosaic() {
        return this.secretLockTransactionBody.getMosaic();
    }

    /**
     * Gets number of blocks for which a lock should be valid.
     *
     * @return Number of blocks for which a lock should be valid.
     */
    public BlockDurationDto getDuration() {
        return this.secretLockTransactionBody.getDuration();
    }

    /**
     * Gets hash algorithm.
     *
     * @return Hash algorithm.
     */
    public LockHashAlgorithmDto getHashAlgorithm() {
        return this.secretLockTransactionBody.getHashAlgorithm();
    }

    /**
     * Gets locked mosaic recipient address.
     *
     * @return Locked mosaic recipient address.
     */
    public UnresolvedAddressDto getRecipientAddress() {
        return this.secretLockTransactionBody.getRecipientAddress();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.secretLockTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of SecretLockTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of SecretLockTransactionBuilder.
     */
    public static SecretLockTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new SecretLockTransactionBuilder(stream);
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
            final byte[] secretLockTransactionBodyBytes = this.secretLockTransactionBody.serialize();
            dataOutputStream.write(secretLockTransactionBodyBytes, 0, secretLockTransactionBodyBytes.length);
        });
    }
}
