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

/** Cosignature detached from an aggregate transaction. */
public final class DetachedCosignatureBuilder extends CosignatureBuilder {
    /** Hash of the aggregate transaction that is signed by this cosignature. */
    private final Hash256Dto parentHash;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected DetachedCosignatureBuilder(final DataInputStream stream) {
        super(stream);
        this.parentHash = Hash256Dto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Cosigner public key.
     * @param signature Cosigner signature.
     * @param parentHash Hash of the aggregate transaction that is signed by this cosignature.
     */
    protected DetachedCosignatureBuilder(final KeyDto signerPublicKey, final SignatureDto signature, final Hash256Dto parentHash) {
        super(signerPublicKey, signature);
        GeneratorUtils.notNull(parentHash, "parentHash is null");
        this.parentHash = parentHash;
    }

    /**
     * Creates an instance of DetachedCosignatureBuilder.
     *
     * @param signerPublicKey Cosigner public key.
     * @param signature Cosigner signature.
     * @param parentHash Hash of the aggregate transaction that is signed by this cosignature.
     * @return Instance of DetachedCosignatureBuilder.
     */
    public static DetachedCosignatureBuilder create(final KeyDto signerPublicKey, final SignatureDto signature, final Hash256Dto parentHash) {
        return new DetachedCosignatureBuilder(signerPublicKey, signature, parentHash);
    }

    /**
     * Gets hash of the aggregate transaction that is signed by this cosignature.
     *
     * @return Hash of the aggregate transaction that is signed by this cosignature.
     */
    public Hash256Dto getParentHash() {
        return this.parentHash;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.parentHash.getSize();
        return size;
    }

    /**
     * Creates an instance of DetachedCosignatureBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of DetachedCosignatureBuilder.
     */
    public static DetachedCosignatureBuilder loadFromBinary(final DataInputStream stream) {
        return new DetachedCosignatureBuilder(stream);
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
            final byte[] parentHashBytes = this.parentHash.serialize();
            dataOutputStream.write(parentHashBytes, 0, parentHashBytes.length);
        });
    }
}
