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

/** Binary layout for a mosaic. */
public class MosaicBuilder {
    /** Mosaic identifier. */
    private final MosaicIdDto mosaicId;
    /** Mosaic amount. */
    private final AmountDto amount;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicBuilder(final DataInputStream stream) {
        this.mosaicId = MosaicIdDto.loadFromBinary(stream);
        this.amount = AmountDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param mosaicId Mosaic identifier.
     * @param amount Mosaic amount.
     */
    protected MosaicBuilder(final MosaicIdDto mosaicId, final AmountDto amount) {
        GeneratorUtils.notNull(mosaicId, "mosaicId is null");
        GeneratorUtils.notNull(amount, "amount is null");
        this.mosaicId = mosaicId;
        this.amount = amount;
    }

    /**
     * Creates an instance of MosaicBuilder.
     *
     * @param mosaicId Mosaic identifier.
     * @param amount Mosaic amount.
     * @return Instance of MosaicBuilder.
     */
    public static MosaicBuilder create(final MosaicIdDto mosaicId, final AmountDto amount) {
        return new MosaicBuilder(mosaicId, amount);
    }

    /**
     * Gets mosaic identifier.
     *
     * @return Mosaic identifier.
     */
    public MosaicIdDto getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Gets mosaic amount.
     *
     * @return Mosaic amount.
     */
    public AmountDto getAmount() {
        return this.amount;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.mosaicId.getSize();
        size += this.amount.getSize();
        return size;
    }

    /**
     * Creates an instance of MosaicBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicBuilder.
     */
    public static MosaicBuilder loadFromBinary(final DataInputStream stream) {
        return new MosaicBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] mosaicIdBytes = this.mosaicId.serialize();
            dataOutputStream.write(mosaicIdBytes, 0, mosaicIdBytes.length);
            final byte[] amountBytes = this.amount.serialize();
            dataOutputStream.write(amountBytes, 0, amountBytes.length);
        });
    }
}
