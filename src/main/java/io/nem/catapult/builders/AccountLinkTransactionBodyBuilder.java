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

/** Binary layout for an account link transaction. */
public final class AccountLinkTransactionBodyBuilder {
    /** Remote public key. */
    private final KeyDto remotePublicKey;
    /** Account link action. */
    private final AccountLinkActionDto linkAction;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AccountLinkTransactionBodyBuilder(final DataInputStream stream) {
        this.remotePublicKey = KeyDto.loadFromBinary(stream);
        this.linkAction = AccountLinkActionDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param remotePublicKey Remote public key.
     * @param linkAction Account link action.
     */
    protected AccountLinkTransactionBodyBuilder(final KeyDto remotePublicKey, final AccountLinkActionDto linkAction) {
        GeneratorUtils.notNull(remotePublicKey, "remotePublicKey is null");
        GeneratorUtils.notNull(linkAction, "linkAction is null");
        this.remotePublicKey = remotePublicKey;
        this.linkAction = linkAction;
    }

    /**
     * Creates an instance of AccountLinkTransactionBodyBuilder.
     *
     * @param remotePublicKey Remote public key.
     * @param linkAction Account link action.
     * @return Instance of AccountLinkTransactionBodyBuilder.
     */
    public static AccountLinkTransactionBodyBuilder create(final KeyDto remotePublicKey, final AccountLinkActionDto linkAction) {
        return new AccountLinkTransactionBodyBuilder(remotePublicKey, linkAction);
    }

    /**
     * Gets remote public key.
     *
     * @return Remote public key.
     */
    public KeyDto getRemotePublicKey() {
        return this.remotePublicKey;
    }

    /**
     * Gets account link action.
     *
     * @return Account link action.
     */
    public AccountLinkActionDto getLinkAction() {
        return this.linkAction;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.remotePublicKey.getSize();
        size += this.linkAction.getSize();
        return size;
    }

    /**
     * Creates an instance of AccountLinkTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountLinkTransactionBodyBuilder.
     */
    public static AccountLinkTransactionBodyBuilder loadFromBinary(final DataInputStream stream) {
        return new AccountLinkTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] remotePublicKeyBytes = this.remotePublicKey.serialize();
            dataOutputStream.write(remotePublicKeyBytes, 0, remotePublicKeyBytes.length);
            final byte[] linkActionBytes = this.linkAction.serialize();
            dataOutputStream.write(linkActionBytes, 0, linkActionBytes.length);
        });
    }
}
