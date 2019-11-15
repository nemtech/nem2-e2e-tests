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

/** Block duration. */
public final class BlockDurationDto {
    /** Block duration. */
    private final long blockDuration;

    /**
     * Constructor.
     *
     * @param blockDuration Block duration.
     */
    public BlockDurationDto(final long blockDuration) {
        this.blockDuration = blockDuration;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public BlockDurationDto(final DataInputStream stream) {
        try {
            this.blockDuration = Long.reverseBytes(stream.readLong());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets Block duration.
     *
     * @return Block duration.
     */
    public long getBlockDuration() {
        return this.blockDuration;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return 8;
    }

    /**
     * Creates an instance of BlockDurationDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of BlockDurationDto.
     */
    public static BlockDurationDto loadFromBinary(final DataInputStream stream) {
        return new BlockDurationDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeLong(Long.reverseBytes(this.getBlockDuration()));
        });
    }
}
