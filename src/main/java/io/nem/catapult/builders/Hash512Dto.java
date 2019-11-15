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

/** Hash512. */
public final class Hash512Dto {
    /** Hash512. */
    private final ByteBuffer hash512;

    /**
     * Constructor.
     *
     * @param hash512 Hash512.
     */
    public Hash512Dto(final ByteBuffer hash512) {
        GeneratorUtils.notNull(hash512, "hash512 is null");
        GeneratorUtils.isTrue(hash512.array().length == 64, "hash512 should be 64 bytes");
        this.hash512 = hash512;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public Hash512Dto(final DataInputStream stream) {
        try {
            this.hash512 = ByteBuffer.allocate(64);
            stream.readFully(this.hash512.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets Hash512.
     *
     * @return Hash512.
     */
    public ByteBuffer getHash512() {
        return this.hash512;
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
     * Creates an instance of Hash512Dto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of Hash512Dto.
     */
    public static Hash512Dto loadFromBinary(final DataInputStream stream) {
        return new Hash512Dto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.write(this.hash512.array(), 0, this.hash512.array().length);
        });
    }
}
