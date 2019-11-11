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

/** Binary layout for an embedded mosaic address restriction transaction. */
public final class EmbeddedMosaicAddressRestrictionTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Mosaic address restriction transaction body. */
    private final MosaicAddressRestrictionTransactionBodyBuilder mosaicAddressRestrictionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedMosaicAddressRestrictionTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.mosaicAddressRestrictionTransactionBody = MosaicAddressRestrictionTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param mosaicId Identifier of the mosaic to which the restriction applies.
     * @param restrictionKey Restriction key.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     * @param targetAddress Address being restricted.
     */
    protected EmbeddedMosaicAddressRestrictionTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final UnresolvedMosaicIdDto mosaicId, final long restrictionKey, final long previousRestrictionValue, final long newRestrictionValue, final UnresolvedAddressDto targetAddress) {
        super(signerPublicKey, version, network, type);
        this.mosaicAddressRestrictionTransactionBody = MosaicAddressRestrictionTransactionBodyBuilder.create(mosaicId, restrictionKey, previousRestrictionValue, newRestrictionValue, targetAddress);
    }

    /**
     * Creates an instance of EmbeddedMosaicAddressRestrictionTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param mosaicId Identifier of the mosaic to which the restriction applies.
     * @param restrictionKey Restriction key.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     * @param targetAddress Address being restricted.
     * @return Instance of EmbeddedMosaicAddressRestrictionTransactionBuilder.
     */
    public static EmbeddedMosaicAddressRestrictionTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final UnresolvedMosaicIdDto mosaicId, final long restrictionKey, final long previousRestrictionValue, final long newRestrictionValue, final UnresolvedAddressDto targetAddress) {
        return new EmbeddedMosaicAddressRestrictionTransactionBuilder(signerPublicKey, version, network, type, mosaicId, restrictionKey, previousRestrictionValue, newRestrictionValue, targetAddress);
    }

    /**
     * Gets identifier of the mosaic to which the restriction applies.
     *
     * @return Identifier of the mosaic to which the restriction applies.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicAddressRestrictionTransactionBody.getMosaicId();
    }

    /**
     * Gets restriction key.
     *
     * @return Restriction key.
     */
    public long getRestrictionKey() {
        return this.mosaicAddressRestrictionTransactionBody.getRestrictionKey();
    }

    /**
     * Gets previous restriction value.
     *
     * @return Previous restriction value.
     */
    public long getPreviousRestrictionValue() {
        return this.mosaicAddressRestrictionTransactionBody.getPreviousRestrictionValue();
    }

    /**
     * Gets new restriction value.
     *
     * @return New restriction value.
     */
    public long getNewRestrictionValue() {
        return this.mosaicAddressRestrictionTransactionBody.getNewRestrictionValue();
    }

    /**
     * Gets address being restricted.
     *
     * @return Address being restricted.
     */
    public UnresolvedAddressDto getTargetAddress() {
        return this.mosaicAddressRestrictionTransactionBody.getTargetAddress();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.mosaicAddressRestrictionTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedMosaicAddressRestrictionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedMosaicAddressRestrictionTransactionBuilder.
     */
    public static EmbeddedMosaicAddressRestrictionTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedMosaicAddressRestrictionTransactionBuilder(stream);
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
            final byte[] mosaicAddressRestrictionTransactionBodyBytes = this.mosaicAddressRestrictionTransactionBody.serialize();
            dataOutputStream.write(mosaicAddressRestrictionTransactionBodyBytes, 0, mosaicAddressRestrictionTransactionBodyBytes.length);
        });
    }
}
