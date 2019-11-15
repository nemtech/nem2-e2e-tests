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
import java.nio.ByteBuffer;

/** Binary layout for a non-embedded namespace registration transaction. */
public final class NamespaceRegistrationTransactionBuilder extends TransactionBuilder {
    /** Namespace registration transaction body. */
    private final NamespaceRegistrationTransactionBodyBuilder namespaceRegistrationTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected NamespaceRegistrationTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.namespaceRegistrationTransactionBody = NamespaceRegistrationTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param duration Namespace duration.
     * @param id Namespace identifier.
     * @param name Namespace name.
     */
    protected NamespaceRegistrationTransactionBuilder(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final BlockDurationDto duration, final NamespaceIdDto id, final ByteBuffer name) {
        super(signature, signerPublicKey, version, network, type, fee, deadline);
        this.namespaceRegistrationTransactionBody = NamespaceRegistrationTransactionBodyBuilder.create(duration, id, name);
    }

    /**
     * Constructor.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param parentId Parent namespace identifier.
     * @param id Namespace identifier.
     * @param name Namespace name.
     */
    protected NamespaceRegistrationTransactionBuilder(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final NamespaceIdDto parentId, final NamespaceIdDto id, final ByteBuffer name) {
        super(signature, signerPublicKey, version, network, type, fee, deadline);
        this.namespaceRegistrationTransactionBody = NamespaceRegistrationTransactionBodyBuilder.create(parentId, id, name);
    }

    /**
     * Creates an instance of NamespaceRegistrationTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param duration Namespace duration.
     * @param id Namespace identifier.
     * @param name Namespace name.
     * @return Instance of NamespaceRegistrationTransactionBuilder.
     */
    public static NamespaceRegistrationTransactionBuilder create(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final BlockDurationDto duration, final NamespaceIdDto id, final ByteBuffer name) {
        return new NamespaceRegistrationTransactionBuilder(signature, signerPublicKey, version, network, type, fee, deadline, duration, id, name);
    }

    /**
     * Creates an instance of NamespaceRegistrationTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param parentId Parent namespace identifier.
     * @param id Namespace identifier.
     * @param name Namespace name.
     * @return Instance of NamespaceRegistrationTransactionBuilder.
     */
    public static NamespaceRegistrationTransactionBuilder create(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final NamespaceIdDto parentId, final NamespaceIdDto id, final ByteBuffer name) {
        return new NamespaceRegistrationTransactionBuilder(signature, signerPublicKey, version, network, type, fee, deadline, parentId, id, name);
    }

    /**
     * Gets namespace duration.
     *
     * @return Namespace duration.
     */
    public BlockDurationDto getDuration() {
        return this.namespaceRegistrationTransactionBody.getDuration();
    }

    /**
     * Gets parent namespace identifier.
     *
     * @return Parent namespace identifier.
     */
    public NamespaceIdDto getParentId() {
        return this.namespaceRegistrationTransactionBody.getParentId();
    }

    /**
     * Gets namespace identifier.
     *
     * @return Namespace identifier.
     */
    public NamespaceIdDto getId() {
        return this.namespaceRegistrationTransactionBody.getId();
    }

    /**
     * Gets namespace registration type.
     *
     * @return Namespace registration type.
     */
    public NamespaceRegistrationTypeDto getRegistrationType() {
        return this.namespaceRegistrationTransactionBody.getRegistrationType();
    }

    /**
     * Gets namespace name.
     *
     * @return Namespace name.
     */
    public ByteBuffer getName() {
        return this.namespaceRegistrationTransactionBody.getName();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.namespaceRegistrationTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of NamespaceRegistrationTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of NamespaceRegistrationTransactionBuilder.
     */
    public static NamespaceRegistrationTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new NamespaceRegistrationTransactionBuilder(stream);
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
            final byte[] namespaceRegistrationTransactionBodyBytes = this.namespaceRegistrationTransactionBody.serialize();
            dataOutputStream.write(namespaceRegistrationTransactionBodyBytes, 0, namespaceRegistrationTransactionBodyBytes.length);
        });
    }
}
