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

/** Hash256. */
public final class Hash256Dto {
    /** Hash256. */
    private final ByteBuffer hash256;

    /**
     * Constructor.
     *
     * @param hash256 Hash256.
     */
    public Hash256Dto(final ByteBuffer hash256) {
        GeneratorUtils.notNull(hash256, "hash256 is null");
        GeneratorUtils.isTrue(hash256.array().length == 32, "hash256 should be 32 bytes");
        this.hash256 = hash256;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public Hash256Dto(final DataInputStream stream) {
        try {
            this.hash256 = ByteBuffer.allocate(32);
            stream.readFully(this.hash256.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets Hash256.
     *
     * @return Hash256.
     */
    public ByteBuffer getHash256() {
        return this.hash256;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return 32;
    }

    /**
     * Creates an instance of Hash256Dto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of Hash256Dto.
     */
    public static Hash256Dto loadFromBinary(final DataInputStream stream) {
        return new Hash256Dto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.write(this.hash256.array(), 0, this.hash256.array().length);
        });
    }
}
