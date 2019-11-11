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
import java.util.List;
import java.nio.ByteBuffer;

/** Binary layout for a transfer transaction. */
public final class TransferTransactionBodyBuilder {
    /** Recipient address. */
    private final UnresolvedAddressDto recipientAddress;
    /** Reserved padding to align mosaics on 8-byte boundary. */
    private final int transferTransactionBody_Reserved1;
    /** Attached mosaics. */
    private final List<UnresolvedMosaicBuilder> mosaics;
    /** Attached message. */
    private final ByteBuffer message;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected TransferTransactionBodyBuilder(final DataInputStream stream) {
        try {
            this.recipientAddress = UnresolvedAddressDto.loadFromBinary(stream);
            final byte mosaicsCount = stream.readByte();
            final short messageSize = Short.reverseBytes(stream.readShort());
            this.transferTransactionBody_Reserved1 = Integer.reverseBytes(stream.readInt());
            this.mosaics = new java.util.ArrayList<>(mosaicsCount);
            for (int i = 0; i < mosaicsCount; i++) {
                mosaics.add(UnresolvedMosaicBuilder.loadFromBinary(stream));
            }
            this.message = ByteBuffer.allocate(messageSize);
            stream.readFully(this.message.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param recipientAddress Recipient address.
     * @param mosaics Attached mosaics.
     * @param message Attached message.
     */
    protected TransferTransactionBodyBuilder(final UnresolvedAddressDto recipientAddress, final List<UnresolvedMosaicBuilder> mosaics, final ByteBuffer message) {
        GeneratorUtils.notNull(recipientAddress, "recipientAddress is null");
        GeneratorUtils.notNull(mosaics, "mosaics is null");
        GeneratorUtils.notNull(message, "message is null");
        this.recipientAddress = recipientAddress;
        this.transferTransactionBody_Reserved1 = 0;
        this.mosaics = mosaics;
        this.message = message;
    }

    /**
     * Creates an instance of TransferTransactionBodyBuilder.
     *
     * @param recipientAddress Recipient address.
     * @param mosaics Attached mosaics.
     * @param message Attached message.
     * @return Instance of TransferTransactionBodyBuilder.
     */
    public static TransferTransactionBodyBuilder create(final UnresolvedAddressDto recipientAddress, final List<UnresolvedMosaicBuilder> mosaics, final ByteBuffer message) {
        return new TransferTransactionBodyBuilder(recipientAddress, mosaics, message);
    }

    /**
     * Gets recipient address.
     *
     * @return Recipient address.
     */
    public UnresolvedAddressDto getRecipientAddress() {
        return this.recipientAddress;
    }

    /**
     * Gets reserved padding to align mosaics on 8-byte boundary.
     *
     * @return Reserved padding to align mosaics on 8-byte boundary.
     */
    private int getTransferTransactionBody_Reserved1() {
        return this.transferTransactionBody_Reserved1;
    }

    /**
     * Gets attached mosaics.
     *
     * @return Attached mosaics.
     */
    public List<UnresolvedMosaicBuilder> getMosaics() {
        return this.mosaics;
    }

    /**
     * Gets attached message.
     *
     * @return Attached message.
     */
    public ByteBuffer getMessage() {
        return this.message;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.recipientAddress.getSize();
        size += 1; // mosaicsCount
        size += 2; // messageSize
        size += 4; // transferTransactionBody_Reserved1
        size += this.mosaics.stream().mapToInt(o -> o.getSize()).sum();
        size += this.message.array().length;
        return size;
    }

    /**
     * Creates an instance of TransferTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of TransferTransactionBodyBuilder.
     */
    public static TransferTransactionBodyBuilder loadFromBinary(final DataInputStream stream) {
        return new TransferTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] recipientAddressBytes = this.recipientAddress.serialize();
            dataOutputStream.write(recipientAddressBytes, 0, recipientAddressBytes.length);
            dataOutputStream.writeByte((byte) this.mosaics.size());
            dataOutputStream.writeShort(Short.reverseBytes((short) this.message.array().length));
            dataOutputStream.writeInt(Integer.reverseBytes(this.getTransferTransactionBody_Reserved1()));
            for (int i = 0; i < this.mosaics.size(); i++) {
                final byte[] mosaicsBytes = this.mosaics.get(i).serialize();
                dataOutputStream.write(mosaicsBytes, 0, mosaicsBytes.length);
            }
            dataOutputStream.write(this.message.array(), 0, this.message.array().length);
        });
    }
}
