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

/** Binary layout for an embedded account link transaction. */
public final class EmbeddedAccountLinkTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Account link transaction body. */
    private final AccountLinkTransactionBodyBuilder accountLinkTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedAccountLinkTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.accountLinkTransactionBody = AccountLinkTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param remotePublicKey Remote public key.
     * @param linkAction Account link action.
     */
    protected EmbeddedAccountLinkTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final KeyDto remotePublicKey, final AccountLinkActionDto linkAction) {
        super(signerPublicKey, version, network, type);
        this.accountLinkTransactionBody = AccountLinkTransactionBodyBuilder.create(remotePublicKey, linkAction);
    }

    /**
     * Creates an instance of EmbeddedAccountLinkTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param remotePublicKey Remote public key.
     * @param linkAction Account link action.
     * @return Instance of EmbeddedAccountLinkTransactionBuilder.
     */
    public static EmbeddedAccountLinkTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final KeyDto remotePublicKey, final AccountLinkActionDto linkAction) {
        return new EmbeddedAccountLinkTransactionBuilder(signerPublicKey, version, network, type, remotePublicKey, linkAction);
    }

    /**
     * Gets remote public key.
     *
     * @return Remote public key.
     */
    public KeyDto getRemotePublicKey() {
        return this.accountLinkTransactionBody.getRemotePublicKey();
    }

    /**
     * Gets account link action.
     *
     * @return Account link action.
     */
    public AccountLinkActionDto getLinkAction() {
        return this.accountLinkTransactionBody.getLinkAction();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.accountLinkTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedAccountLinkTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedAccountLinkTransactionBuilder.
     */
    public static EmbeddedAccountLinkTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedAccountLinkTransactionBuilder(stream);
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
            final byte[] accountLinkTransactionBodyBytes = this.accountLinkTransactionBody.serialize();
            dataOutputStream.write(accountLinkTransactionBodyBytes, 0, accountLinkTransactionBodyBytes.length);
        });
    }
}
