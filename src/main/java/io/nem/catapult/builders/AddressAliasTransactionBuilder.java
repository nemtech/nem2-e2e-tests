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

/** Binary layout for a non-embedded address alias transaction. */
public final class AddressAliasTransactionBuilder extends TransactionBuilder {
    /** Address alias transaction body. */
    private final AddressAliasTransactionBodyBuilder addressAliasTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AddressAliasTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.addressAliasTransactionBody = AddressAliasTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param namespaceId Identifier of the namespace that will become an alias.
     * @param address Aliased address.
     * @param aliasAction Alias action.
     */
    protected AddressAliasTransactionBuilder(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final NamespaceIdDto namespaceId, final AddressDto address, final AliasActionDto aliasAction) {
        super(signature, signerPublicKey, version, network, type, fee, deadline);
        this.addressAliasTransactionBody = AddressAliasTransactionBodyBuilder.create(namespaceId, address, aliasAction);
    }

    /**
     * Creates an instance of AddressAliasTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param namespaceId Identifier of the namespace that will become an alias.
     * @param address Aliased address.
     * @param aliasAction Alias action.
     * @return Instance of AddressAliasTransactionBuilder.
     */
    public static AddressAliasTransactionBuilder create(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final NamespaceIdDto namespaceId, final AddressDto address, final AliasActionDto aliasAction) {
        return new AddressAliasTransactionBuilder(signature, signerPublicKey, version, network, type, fee, deadline, namespaceId, address, aliasAction);
    }

    /**
     * Gets identifier of the namespace that will become an alias.
     *
     * @return Identifier of the namespace that will become an alias.
     */
    public NamespaceIdDto getNamespaceId() {
        return this.addressAliasTransactionBody.getNamespaceId();
    }

    /**
     * Gets aliased address.
     *
     * @return Aliased address.
     */
    public AddressDto getAddress() {
        return this.addressAliasTransactionBody.getAddress();
    }

    /**
     * Gets alias action.
     *
     * @return Alias action.
     */
    public AliasActionDto getAliasAction() {
        return this.addressAliasTransactionBody.getAliasAction();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.addressAliasTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of AddressAliasTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AddressAliasTransactionBuilder.
     */
    public static AddressAliasTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new AddressAliasTransactionBuilder(stream);
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
            final byte[] addressAliasTransactionBodyBytes = this.addressAliasTransactionBody.serialize();
            dataOutputStream.write(addressAliasTransactionBodyBytes, 0, addressAliasTransactionBodyBytes.length);
        });
    }
}
