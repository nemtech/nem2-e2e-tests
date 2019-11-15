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

/** Binary layout for a non-embedded secret proof transaction. */
public final class SecretProofTransactionBuilder extends TransactionBuilder {
    /** Secret proof transaction body. */
    private final SecretProofTransactionBodyBuilder secretProofTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected SecretProofTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.secretProofTransactionBody = SecretProofTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param hashAlgorithm Hash algorithm.
     * @param recipientAddress Locked mosaic recipient address.
     * @param proof Proof data.
     */
    protected SecretProofTransactionBuilder(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final Hash256Dto secret, final LockHashAlgorithmDto hashAlgorithm, final UnresolvedAddressDto recipientAddress, final ByteBuffer proof) {
        super(signature, signerPublicKey, version, network, type, fee, deadline);
        this.secretProofTransactionBody = SecretProofTransactionBodyBuilder.create(secret, hashAlgorithm, recipientAddress, proof);
    }

    /**
     * Creates an instance of SecretProofTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param secret Secret.
     * @param hashAlgorithm Hash algorithm.
     * @param recipientAddress Locked mosaic recipient address.
     * @param proof Proof data.
     * @return Instance of SecretProofTransactionBuilder.
     */
    public static SecretProofTransactionBuilder create(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final Hash256Dto secret, final LockHashAlgorithmDto hashAlgorithm, final UnresolvedAddressDto recipientAddress, final ByteBuffer proof) {
        return new SecretProofTransactionBuilder(signature, signerPublicKey, version, network, type, fee, deadline, secret, hashAlgorithm, recipientAddress, proof);
    }

    /**
     * Gets secret.
     *
     * @return Secret.
     */
    public Hash256Dto getSecret() {
        return this.secretProofTransactionBody.getSecret();
    }

    /**
     * Gets hash algorithm.
     *
     * @return Hash algorithm.
     */
    public LockHashAlgorithmDto getHashAlgorithm() {
        return this.secretProofTransactionBody.getHashAlgorithm();
    }

    /**
     * Gets locked mosaic recipient address.
     *
     * @return Locked mosaic recipient address.
     */
    public UnresolvedAddressDto getRecipientAddress() {
        return this.secretProofTransactionBody.getRecipientAddress();
    }

    /**
     * Gets proof data.
     *
     * @return Proof data.
     */
    public ByteBuffer getProof() {
        return this.secretProofTransactionBody.getProof();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.secretProofTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of SecretProofTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of SecretProofTransactionBuilder.
     */
    public static SecretProofTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new SecretProofTransactionBuilder(stream);
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
            final byte[] secretProofTransactionBodyBytes = this.secretProofTransactionBody.serialize();
            dataOutputStream.write(secretProofTransactionBodyBytes, 0, secretProofTransactionBodyBytes.length);
        });
    }
}
