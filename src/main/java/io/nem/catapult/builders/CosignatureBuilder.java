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

/** Cosignature attached to an aggregate transaction. */
public class CosignatureBuilder {
    /** Cosigner public key. */
    private final KeyDto signerPublicKey;
    /** Cosigner signature. */
    private final SignatureDto signature;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected CosignatureBuilder(final DataInputStream stream) {
        this.signerPublicKey = KeyDto.loadFromBinary(stream);
        this.signature = SignatureDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Cosigner public key.
     * @param signature Cosigner signature.
     */
    protected CosignatureBuilder(final KeyDto signerPublicKey, final SignatureDto signature) {
        GeneratorUtils.notNull(signerPublicKey, "signerPublicKey is null");
        GeneratorUtils.notNull(signature, "signature is null");
        this.signerPublicKey = signerPublicKey;
        this.signature = signature;
    }

    /**
     * Creates an instance of CosignatureBuilder.
     *
     * @param signerPublicKey Cosigner public key.
     * @param signature Cosigner signature.
     * @return Instance of CosignatureBuilder.
     */
    public static CosignatureBuilder create(final KeyDto signerPublicKey, final SignatureDto signature) {
        return new CosignatureBuilder(signerPublicKey, signature);
    }

    /**
     * Gets cosigner public key.
     *
     * @return Cosigner public key.
     */
    public KeyDto getSignerPublicKey() {
        return this.signerPublicKey;
    }

    /**
     * Gets cosigner signature.
     *
     * @return Cosigner signature.
     */
    public SignatureDto getSignature() {
        return this.signature;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.signerPublicKey.getSize();
        size += this.signature.getSize();
        return size;
    }

    /**
     * Creates an instance of CosignatureBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of CosignatureBuilder.
     */
    public static CosignatureBuilder loadFromBinary(final DataInputStream stream) {
        return new CosignatureBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] signerPublicKeyBytes = this.signerPublicKey.serialize();
            dataOutputStream.write(signerPublicKeyBytes, 0, signerPublicKeyBytes.length);
            final byte[] signatureBytes = this.signature.serialize();
            dataOutputStream.write(signatureBytes, 0, signatureBytes.length);
        });
    }
}
