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

/** Namespace id. */
public final class NamespaceIdDto {
    /** Namespace id. */
    private final long namespaceId;

    /**
     * Constructor.
     *
     * @param namespaceId Namespace id.
     */
    public NamespaceIdDto(final long namespaceId) {
        this.namespaceId = namespaceId;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public NamespaceIdDto(final DataInputStream stream) {
        try {
            this.namespaceId = Long.reverseBytes(stream.readLong());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets Namespace id.
     *
     * @return Namespace id.
     */
    public long getNamespaceId() {
        return this.namespaceId;
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
     * Creates an instance of NamespaceIdDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of NamespaceIdDto.
     */
    public static NamespaceIdDto loadFromBinary(final DataInputStream stream) {
        return new NamespaceIdDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeLong(Long.reverseBytes(this.getNamespaceId()));
        });
    }
}
