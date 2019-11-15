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

/** Binary layout for an embedded transaction. */
public class EmbeddedTransactionBuilder {
    /** Entity size. */
    private int size;
    /** Reserved padding to align end of EmbeddedTransactionHeader on 8-byte boundary. */
    private final int embeddedTransactionHeader_Reserved1;
    /** Entity signer's public key. */
    private final KeyDto signerPublicKey;
    /** Reserved padding to align end of EntityBody on 8-byte boundary. */
    private final int entityBody_Reserved1;
    /** Entity version. */
    private final byte version;
    /** Entity network. */
    private final NetworkTypeDto network;
    /** Entity type. */
    private final EntityTypeDto type;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedTransactionBuilder(final DataInputStream stream) {
        try {
            this.size = Integer.reverseBytes(stream.readInt());
            this.embeddedTransactionHeader_Reserved1 = Integer.reverseBytes(stream.readInt());
            this.signerPublicKey = KeyDto.loadFromBinary(stream);
            this.entityBody_Reserved1 = Integer.reverseBytes(stream.readInt());
            this.version = stream.readByte();
            this.network = NetworkTypeDto.loadFromBinary(stream);
            this.type = EntityTypeDto.loadFromBinary(stream);
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     */
    protected EmbeddedTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type) {
        GeneratorUtils.notNull(signerPublicKey, "signerPublicKey is null");
        GeneratorUtils.notNull(network, "network is null");
        GeneratorUtils.notNull(type, "type is null");
        this.embeddedTransactionHeader_Reserved1 = 0;
        this.signerPublicKey = signerPublicKey;
        this.entityBody_Reserved1 = 0;
        this.version = version;
        this.network = network;
        this.type = type;
    }

    /**
     * Creates an instance of EmbeddedTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @return Instance of EmbeddedTransactionBuilder.
     */
    public static EmbeddedTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type) {
        return new EmbeddedTransactionBuilder(signerPublicKey, version, network, type);
    }

    /**
     * Gets the size if created from a stream otherwise zero.
     *
     * @return Object size from stream.
     */
    protected int getStreamSize() {
        return this.size;
    }

    /**
     * Gets reserved padding to align end of EmbeddedTransactionHeader on 8-byte boundary.
     *
     * @return Reserved padding to align end of EmbeddedTransactionHeader on 8-byte boundary.
     */
    private int getEmbeddedTransactionHeader_Reserved1() {
        return this.embeddedTransactionHeader_Reserved1;
    }

    /**
     * Gets entity signer's public key.
     *
     * @return Entity signer's public key.
     */
    public KeyDto getSignerPublicKey() {
        return this.signerPublicKey;
    }

    /**
     * Gets reserved padding to align end of EntityBody on 8-byte boundary.
     *
     * @return Reserved padding to align end of EntityBody on 8-byte boundary.
     */
    private int getEntityBody_Reserved1() {
        return this.entityBody_Reserved1;
    }

    /**
     * Gets entity version.
     *
     * @return Entity version.
     */
    public byte getVersion() {
        return this.version;
    }

    /**
     * Gets entity network.
     *
     * @return Entity network.
     */
    public NetworkTypeDto getNetwork() {
        return this.network;
    }

    /**
     * Gets entity type.
     *
     * @return Entity type.
     */
    public EntityTypeDto getType() {
        return this.type;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += 4; // size
        size += 4; // embeddedTransactionHeader_Reserved1
        size += this.signerPublicKey.getSize();
        size += 4; // entityBody_Reserved1
        size += 1; // version
        size += this.network.getSize();
        size += this.type.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedTransactionBuilder.
     */
    public static EmbeddedTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedTransactionBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeInt(Integer.reverseBytes(this.getSize()));
            dataOutputStream.writeInt(Integer.reverseBytes(this.getEmbeddedTransactionHeader_Reserved1()));
            final byte[] signerPublicKeyBytes = this.signerPublicKey.serialize();
            dataOutputStream.write(signerPublicKeyBytes, 0, signerPublicKeyBytes.length);
            dataOutputStream.writeInt(Integer.reverseBytes(this.getEntityBody_Reserved1()));
            dataOutputStream.writeByte(this.getVersion());
            final byte[] networkBytes = this.network.serialize();
            dataOutputStream.write(networkBytes, 0, networkBytes.length);
            final byte[] typeBytes = this.type.serialize();
            dataOutputStream.write(typeBytes, 0, typeBytes.length);
        });
    }
}
