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
import java.util.EnumSet;
import java.util.List;

/** Binary layout for an embedded account address restriction transaction. */
public final class EmbeddedAccountAddressRestrictionTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Account address restriction transaction body. */
    private final AccountAddressRestrictionTransactionBodyBuilder accountAddressRestrictionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedAccountAddressRestrictionTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.accountAddressRestrictionTransactionBody = AccountAddressRestrictionTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param restrictionFlags Account restriction flags.
     * @param restrictionAdditions Account restriction additions.
     * @param restrictionDeletions Account restriction deletions.
     */
    protected EmbeddedAccountAddressRestrictionTransactionBuilder(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final EnumSet<AccountRestrictionFlagsDto> restrictionFlags, final List<UnresolvedAddressDto> restrictionAdditions, final List<UnresolvedAddressDto> restrictionDeletions) {
        super(signerPublicKey, version, network, type);
        this.accountAddressRestrictionTransactionBody = AccountAddressRestrictionTransactionBodyBuilder.create(restrictionFlags, restrictionAdditions, restrictionDeletions);
    }

    /**
     * Creates an instance of EmbeddedAccountAddressRestrictionTransactionBuilder.
     *
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param restrictionFlags Account restriction flags.
     * @param restrictionAdditions Account restriction additions.
     * @param restrictionDeletions Account restriction deletions.
     * @return Instance of EmbeddedAccountAddressRestrictionTransactionBuilder.
     */
    public static EmbeddedAccountAddressRestrictionTransactionBuilder create(final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final EnumSet<AccountRestrictionFlagsDto> restrictionFlags, final List<UnresolvedAddressDto> restrictionAdditions, final List<UnresolvedAddressDto> restrictionDeletions) {
        return new EmbeddedAccountAddressRestrictionTransactionBuilder(signerPublicKey, version, network, type, restrictionFlags, restrictionAdditions, restrictionDeletions);
    }

    /**
     * Gets account restriction flags.
     *
     * @return Account restriction flags.
     */
    public EnumSet<AccountRestrictionFlagsDto> getRestrictionFlags() {
        return this.accountAddressRestrictionTransactionBody.getRestrictionFlags();
    }

    /**
     * Gets account restriction additions.
     *
     * @return Account restriction additions.
     */
    public List<UnresolvedAddressDto> getRestrictionAdditions() {
        return this.accountAddressRestrictionTransactionBody.getRestrictionAdditions();
    }

    /**
     * Gets account restriction deletions.
     *
     * @return Account restriction deletions.
     */
    public List<UnresolvedAddressDto> getRestrictionDeletions() {
        return this.accountAddressRestrictionTransactionBody.getRestrictionDeletions();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.accountAddressRestrictionTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedAccountAddressRestrictionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedAccountAddressRestrictionTransactionBuilder.
     */
    public static EmbeddedAccountAddressRestrictionTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new EmbeddedAccountAddressRestrictionTransactionBuilder(stream);
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
            final byte[] accountAddressRestrictionTransactionBodyBytes = this.accountAddressRestrictionTransactionBody.serialize();
            dataOutputStream.write(accountAddressRestrictionTransactionBodyBytes, 0, accountAddressRestrictionTransactionBodyBytes.length);
        });
    }
}
