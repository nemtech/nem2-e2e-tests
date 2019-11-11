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

/** Binary layout for a mosaic global restriction transaction. */
public final class MosaicGlobalRestrictionTransactionBodyBuilder {
    /** Identifier of the mosaic being restricted. */
    private final UnresolvedMosaicIdDto mosaicId;
    /** Identifier of the mosaic providing the restriction key. */
    private final UnresolvedMosaicIdDto referenceMosaicId;
    /** Restriction key relative to the reference mosaic identifier. */
    private final long restrictionKey;
    /** Previous restriction value. */
    private final long previousRestrictionValue;
    /** New restriction value. */
    private final long newRestrictionValue;
    /** Previous restriction type. */
    private final MosaicRestrictionTypeDto previousRestrictionType;
    /** New restriction type. */
    private final MosaicRestrictionTypeDto newRestrictionType;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicGlobalRestrictionTransactionBodyBuilder(final DataInputStream stream) {
        try {
            this.mosaicId = UnresolvedMosaicIdDto.loadFromBinary(stream);
            this.referenceMosaicId = UnresolvedMosaicIdDto.loadFromBinary(stream);
            this.restrictionKey = Long.reverseBytes(stream.readLong());
            this.previousRestrictionValue = Long.reverseBytes(stream.readLong());
            this.newRestrictionValue = Long.reverseBytes(stream.readLong());
            this.previousRestrictionType = MosaicRestrictionTypeDto.loadFromBinary(stream);
            this.newRestrictionType = MosaicRestrictionTypeDto.loadFromBinary(stream);
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param mosaicId Identifier of the mosaic being restricted.
     * @param referenceMosaicId Identifier of the mosaic providing the restriction key.
     * @param restrictionKey Restriction key relative to the reference mosaic identifier.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     * @param previousRestrictionType Previous restriction type.
     * @param newRestrictionType New restriction type.
     */
    protected MosaicGlobalRestrictionTransactionBodyBuilder(final UnresolvedMosaicIdDto mosaicId, final UnresolvedMosaicIdDto referenceMosaicId, final long restrictionKey, final long previousRestrictionValue, final long newRestrictionValue, final MosaicRestrictionTypeDto previousRestrictionType, final MosaicRestrictionTypeDto newRestrictionType) {
        GeneratorUtils.notNull(mosaicId, "mosaicId is null");
        GeneratorUtils.notNull(referenceMosaicId, "referenceMosaicId is null");
        GeneratorUtils.notNull(previousRestrictionType, "previousRestrictionType is null");
        GeneratorUtils.notNull(newRestrictionType, "newRestrictionType is null");
        this.mosaicId = mosaicId;
        this.referenceMosaicId = referenceMosaicId;
        this.restrictionKey = restrictionKey;
        this.previousRestrictionValue = previousRestrictionValue;
        this.newRestrictionValue = newRestrictionValue;
        this.previousRestrictionType = previousRestrictionType;
        this.newRestrictionType = newRestrictionType;
    }

    /**
     * Creates an instance of MosaicGlobalRestrictionTransactionBodyBuilder.
     *
     * @param mosaicId Identifier of the mosaic being restricted.
     * @param referenceMosaicId Identifier of the mosaic providing the restriction key.
     * @param restrictionKey Restriction key relative to the reference mosaic identifier.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     * @param previousRestrictionType Previous restriction type.
     * @param newRestrictionType New restriction type.
     * @return Instance of MosaicGlobalRestrictionTransactionBodyBuilder.
     */
    public static MosaicGlobalRestrictionTransactionBodyBuilder create(final UnresolvedMosaicIdDto mosaicId, final UnresolvedMosaicIdDto referenceMosaicId, final long restrictionKey, final long previousRestrictionValue, final long newRestrictionValue, final MosaicRestrictionTypeDto previousRestrictionType, final MosaicRestrictionTypeDto newRestrictionType) {
        return new MosaicGlobalRestrictionTransactionBodyBuilder(mosaicId, referenceMosaicId, restrictionKey, previousRestrictionValue, newRestrictionValue, previousRestrictionType, newRestrictionType);
    }

    /**
     * Gets identifier of the mosaic being restricted.
     *
     * @return Identifier of the mosaic being restricted.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Gets identifier of the mosaic providing the restriction key.
     *
     * @return Identifier of the mosaic providing the restriction key.
     */
    public UnresolvedMosaicIdDto getReferenceMosaicId() {
        return this.referenceMosaicId;
    }

    /**
     * Gets restriction key relative to the reference mosaic identifier.
     *
     * @return Restriction key relative to the reference mosaic identifier.
     */
    public long getRestrictionKey() {
        return this.restrictionKey;
    }

    /**
     * Gets previous restriction value.
     *
     * @return Previous restriction value.
     */
    public long getPreviousRestrictionValue() {
        return this.previousRestrictionValue;
    }

    /**
     * Gets new restriction value.
     *
     * @return New restriction value.
     */
    public long getNewRestrictionValue() {
        return this.newRestrictionValue;
    }

    /**
     * Gets previous restriction type.
     *
     * @return Previous restriction type.
     */
    public MosaicRestrictionTypeDto getPreviousRestrictionType() {
        return this.previousRestrictionType;
    }

    /**
     * Gets new restriction type.
     *
     * @return New restriction type.
     */
    public MosaicRestrictionTypeDto getNewRestrictionType() {
        return this.newRestrictionType;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.mosaicId.getSize();
        size += this.referenceMosaicId.getSize();
        size += 8; // restrictionKey
        size += 8; // previousRestrictionValue
        size += 8; // newRestrictionValue
        size += this.previousRestrictionType.getSize();
        size += this.newRestrictionType.getSize();
        return size;
    }

    /**
     * Creates an instance of MosaicGlobalRestrictionTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicGlobalRestrictionTransactionBodyBuilder.
     */
    public static MosaicGlobalRestrictionTransactionBodyBuilder loadFromBinary(final DataInputStream stream) {
        return new MosaicGlobalRestrictionTransactionBodyBuilder(stream);
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
            final byte[] referenceMosaicIdBytes = this.referenceMosaicId.serialize();
            dataOutputStream.write(referenceMosaicIdBytes, 0, referenceMosaicIdBytes.length);
            dataOutputStream.writeLong(Long.reverseBytes(this.getRestrictionKey()));
            dataOutputStream.writeLong(Long.reverseBytes(this.getPreviousRestrictionValue()));
            dataOutputStream.writeLong(Long.reverseBytes(this.getNewRestrictionValue()));
            final byte[] previousRestrictionTypeBytes = this.previousRestrictionType.serialize();
            dataOutputStream.write(previousRestrictionTypeBytes, 0, previousRestrictionTypeBytes.length);
            final byte[] newRestrictionTypeBytes = this.newRestrictionType.serialize();
            dataOutputStream.write(newRestrictionTypeBytes, 0, newRestrictionTypeBytes.length);
        });
    }
}
