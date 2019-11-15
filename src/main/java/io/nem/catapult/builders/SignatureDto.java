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

/** Signature. */
public final class SignatureDto {
    /** Signature. */
    private final ByteBuffer signature;

    /**
     * Constructor.
     *
     * @param signature Signature.
     */
    public SignatureDto(final ByteBuffer signature) {
        GeneratorUtils.notNull(signature, "signature is null");
        GeneratorUtils.isTrue(signature.array().length == 64, "signature should be 64 bytes");
        this.signature = signature;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public SignatureDto(final DataInputStream stream) {
        try {
            this.signature = ByteBuffer.allocate(64);
            stream.readFully(this.signature.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets Signature.
     *
     * @return Signature.
     */
    public ByteBuffer getSignature() {
        return this.signature;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return 64;
    }

    /**
     * Creates an instance of SignatureDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of SignatureDto.
     */
    public static SignatureDto loadFromBinary(final DataInputStream stream) {
        return new SignatureDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.write(this.signature.array(), 0, this.signature.array().length);
        });
    }
}
