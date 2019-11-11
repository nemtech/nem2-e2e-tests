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

/** Binary layout for an embedded mosaic global restriction transaction. */
public final class EmbeddedMosaicGlobalRestrictionTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Mosaic global restriction transaction body. */
    private final MosaicGlobalRestrictionTransactionBodyBuilder mosaicGlobalRestrictionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedMosaicGlobalRestrictionTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.mosaicGlobalRestrictionTransactionBody = MosaicGlobalRestrictionTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param mosaicId Identifier of the mosaic being restricted.
     * @param referenceMosaicId Identifier of the mosaic providing the restriction key.
     * @param restrictionKey Restriction key relative to the reference mosaic identifier.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     * @param previousRestrictionType Previous restriction type.
     * @param newRestrictionType New restriction type.
     */
    protected EmbeddedMosaicGlobalRestrictionTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final UnresolvedMosaicIdDto mosaicId, final UnresolvedMosaicIdDto referenceMosaicId, final long restrictionKey, final long previousRestrictionValue, final long newRestrictionValue, final MosaicRestrictionTypeDto previousRestrictionType, final MosaicRestrictionTypeDto newRestrictionType) {
        super(signerPublicKey, version, network, type);
        this.mosaicGlobalRestrictionTransactionBody = MosaicGlobalRestrictionTransactionBodyBuilder.create(mosaicId, referenceMosaicId, restrictionKey, previousRestrictionValue, newRestrictionValue, previousRestrictionType, newRestrictionType);
    }

    /**
     * Creates an instance of EmbeddedMosaicGlobalRestrictionTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param mosaicId Identifier of the mosaic being restricted.
     * @param referenceMosaicId Identifier of the mosaic providing the restriction key.
     * @param restrictionKey Restriction key relative to the reference mosaic identifier.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     * @param previousRestrictionType Previous restriction type.
     * @param newRestrictionType New restriction type.
     * @return Instance of EmbeddedMosaicGlobalRestrictionTransactionBuilder.
     */
    public static EmbeddedMosaicGlobalRestrictionTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final UnresolvedMosaicIdDto mosaicId, final UnresolvedMosaicIdDto referenceMosaicId, final long restrictionKey, final long previousRestrictionValue, final long newRestrictionValue, final MosaicRestrictionTypeDto previousRestrictionType, final MosaicRestrictionTypeDto newRestrictionType) {
        return new EmbeddedMosaicGlobalRestrictionTransactionBuilder(signerPublicKey, version, network, type, mosaicId, referenceMosaicId, restrictionKey, previousRestrictionValue, newRestrictionValue, previousRestrictionType, newRestrictionType);
    }

    /**
     * Gets identifier of the mosaic being restricted.
     *
     * @return Identifier of the mosaic being restricted.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicGlobalRestrictionTransactionBody.getMosaicId();
    }

    /**
     * Gets identifier of the mosaic providing the restriction key.
     *
     * @return Identifier of the mosaic providing the restriction key.
     */
    public UnresolvedMosaicIdDto getReferenceMosaicId() {
        return this.mosaicGlobalRestrictionTransactionBody.getReferenceMosaicId();
    }

    /**
     * Gets restriction key relative to the reference mosaic identifier.
     *
     * @return Restriction key relative to the reference mosaic identifier.
     */
    public long getRestrictionKey() {
        return this.mosaicGlobalRestrictionTransactionBody.getRestrictionKey();
    }

    /**
     * Gets previous restriction value.
     *
     * @return Previous restriction value.
     */
    public long getPreviousRestrictionValue() {
        return this.mosaicGlobalRestrictionTransactionBody.getPreviousRestrictionValue();
    }

    /**
     * Gets new restriction value.
     *
     * @return New restriction value.
     */
    public long getNewRestrictionValue() {
        return this.mosaicGlobalRestrictionTransactionBody.getNewRestrictionValue();
    }

    /**
     * Gets previous restriction type.
     *
     * @return Previous restriction type.
     */
    public MosaicRestrictionTypeDto getPreviousRestrictionType() {
        return this.mosaicGlobalRestrictionTransactionBody.getPreviousRestrictionType();
    }

    /**
     * Gets new restriction type.
     *
     * @return New restriction type.
     */
    public MosaicRestrictionTypeDto getNewRestrictionType() {
        return this.mosaicGlobalRestrictionTransactionBody.getNewRestrictionType();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.mosaicGlobalRestrictionTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedMosaicGlobalRestrictionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedMosaicGlobalRestrictionTransactionBuilder.
     */
    public static EmbeddedMosaicGlobalRestrictionTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedMosaicGlobalRestrictionTransactionBuilder(stream);
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
            final byte[] mosaicGlobalRestrictionTransactionBodyBytes = this.mosaicGlobalRestrictionTransactionBody.serialize();
            dataOutputStream.write(mosaicGlobalRestrictionTransactionBodyBytes, 0, mosaicGlobalRestrictionTransactionBodyBytes.length);
        });
    }
}
