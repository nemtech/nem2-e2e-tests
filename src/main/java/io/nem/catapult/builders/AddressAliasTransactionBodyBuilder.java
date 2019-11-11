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

/** Binary layout for an address alias transaction. */
public final class AddressAliasTransactionBodyBuilder {
    /** Identifier of the namespace that will become an alias. */
    private final NamespaceIdDto namespaceId;
    /** Aliased address. */
    private final AddressDto address;
    /** Alias action. */
    private final AliasActionDto aliasAction;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AddressAliasTransactionBodyBuilder(final DataInputStream stream) {
        this.namespaceId = NamespaceIdDto.loadFromBinary(stream);
        this.address = AddressDto.loadFromBinary(stream);
        this.aliasAction = AliasActionDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param namespaceId Identifier of the namespace that will become an alias.
     * @param address Aliased address.
     * @param aliasAction Alias action.
     */
    protected AddressAliasTransactionBodyBuilder(final NamespaceIdDto namespaceId, final AddressDto address, final AliasActionDto aliasAction) {
        GeneratorUtils.notNull(namespaceId, "namespaceId is null");
        GeneratorUtils.notNull(address, "address is null");
        GeneratorUtils.notNull(aliasAction, "aliasAction is null");
        this.namespaceId = namespaceId;
        this.address = address;
        this.aliasAction = aliasAction;
    }

    /**
     * Creates an instance of AddressAliasTransactionBodyBuilder.
     *
     * @param namespaceId Identifier of the namespace that will become an alias.
     * @param address Aliased address.
     * @param aliasAction Alias action.
     * @return Instance of AddressAliasTransactionBodyBuilder.
     */
    public static AddressAliasTransactionBodyBuilder create(final NamespaceIdDto namespaceId, final AddressDto address, final AliasActionDto aliasAction) {
        return new AddressAliasTransactionBodyBuilder(namespaceId, address, aliasAction);
    }

    /**
     * Gets identifier of the namespace that will become an alias.
     *
     * @return Identifier of the namespace that will become an alias.
     */
    public NamespaceIdDto getNamespaceId() {
        return this.namespaceId;
    }

    /**
     * Gets aliased address.
     *
     * @return Aliased address.
     */
    public AddressDto getAddress() {
        return this.address;
    }

    /**
     * Gets alias action.
     *
     * @return Alias action.
     */
    public AliasActionDto getAliasAction() {
        return this.aliasAction;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.namespaceId.getSize();
        size += this.address.getSize();
        size += this.aliasAction.getSize();
        return size;
    }

    /**
     * Creates an instance of AddressAliasTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AddressAliasTransactionBodyBuilder.
     */
    public static AddressAliasTransactionBodyBuilder loadFromBinary(final DataInputStream stream) {
        return new AddressAliasTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] namespaceIdBytes = this.namespaceId.serialize();
            dataOutputStream.write(namespaceIdBytes, 0, namespaceIdBytes.length);
            final byte[] addressBytes = this.address.serialize();
            dataOutputStream.write(addressBytes, 0, addressBytes.length);
            final byte[] aliasActionBytes = this.aliasAction.serialize();
            dataOutputStream.write(aliasActionBytes, 0, aliasActionBytes.length);
        });
    }
}
