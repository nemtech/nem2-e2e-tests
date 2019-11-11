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

/** Mosaic id. */
public final class MosaicIdDto {
    /** Mosaic id. */
    private final long mosaicId;

    /**
     * Constructor.
     *
     * @param mosaicId Mosaic id.
     */
    public MosaicIdDto(final long mosaicId) {
        this.mosaicId = mosaicId;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public MosaicIdDto(final DataInputStream stream) {
        try {
            this.mosaicId = Long.reverseBytes(stream.readLong());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets Mosaic id.
     *
     * @return Mosaic id.
     */
    public long getMosaicId() {
        return this.mosaicId;
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
     * Creates an instance of MosaicIdDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicIdDto.
     */
    public static MosaicIdDto loadFromBinary(final DataInputStream stream) {
        return new MosaicIdDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeLong(Long.reverseBytes(this.getMosaicId()));
        });
    }
}
