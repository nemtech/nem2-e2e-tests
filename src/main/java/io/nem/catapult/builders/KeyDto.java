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

/** Key. */
public final class KeyDto {
    /** Key. */
    private final ByteBuffer key;

    /**
     * Constructor.
     *
     * @param key Key.
     */
    public KeyDto(final ByteBuffer key) {
        GeneratorUtils.notNull(key, "key is null");
        GeneratorUtils.isTrue(key.array().length == 32, "key should be 32 bytes");
        this.key = key;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public KeyDto(final DataInputStream stream) {
        try {
            this.key = ByteBuffer.allocate(32);
            stream.readFully(this.key.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets Key.
     *
     * @return Key.
     */
    public ByteBuffer getKey() {
        return this.key;
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
     * Creates an instance of KeyDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of KeyDto.
     */
    public static KeyDto loadFromBinary(final DataInputStream stream) {
        return new KeyDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.write(this.key.array(), 0, this.key.array().length);
        });
    }
}
