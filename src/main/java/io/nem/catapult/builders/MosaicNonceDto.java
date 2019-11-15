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

/** Mosaic nonce. */
public final class MosaicNonceDto {
    /** Mosaic nonce. */
    private final int mosaicNonce;

    /**
     * Constructor.
     *
     * @param mosaicNonce Mosaic nonce.
     */
    public MosaicNonceDto(final int mosaicNonce) {
        this.mosaicNonce = mosaicNonce;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public MosaicNonceDto(final DataInputStream stream) {
        try {
            this.mosaicNonce = Integer.reverseBytes(stream.readInt());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets Mosaic nonce.
     *
     * @return Mosaic nonce.
     */
    public int getMosaicNonce() {
        return this.mosaicNonce;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return 4;
    }

    /**
     * Creates an instance of MosaicNonceDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicNonceDto.
     */
    public static MosaicNonceDto loadFromBinary(final DataInputStream stream) {
        return new MosaicNonceDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeInt(Integer.reverseBytes(this.getMosaicNonce()));
        });
    }
}
