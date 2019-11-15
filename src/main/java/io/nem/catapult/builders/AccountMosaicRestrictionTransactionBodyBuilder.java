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

/** Binary layout for an account mosaic restriction transaction. */
public final class AccountMosaicRestrictionTransactionBodyBuilder {
    /** Account restriction flags. */
    private final EnumSet<AccountRestrictionFlagsDto> restrictionFlags;
    /** Reserved padding to align restrictionAdditions on 8-byte boundary. */
    private final int accountRestrictionTransactionBody_Reserved1;
    /** Account restriction additions. */
    private final List<UnresolvedMosaicIdDto> restrictionAdditions;
    /** Account restriction deletions. */
    private final List<UnresolvedMosaicIdDto> restrictionDeletions;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AccountMosaicRestrictionTransactionBodyBuilder(final DataInputStream stream) {
        try {
            this.restrictionFlags = GeneratorUtils.toSet(AccountRestrictionFlagsDto.class, Short.reverseBytes(stream.readShort()));
            final byte restrictionAdditionsCount = stream.readByte();
            final byte restrictionDeletionsCount = stream.readByte();
            this.accountRestrictionTransactionBody_Reserved1 = Integer.reverseBytes(stream.readInt());
            this.restrictionAdditions = new java.util.ArrayList<>(restrictionAdditionsCount);
            for (int i = 0; i < restrictionAdditionsCount; i++) {
                restrictionAdditions.add(UnresolvedMosaicIdDto.loadFromBinary(stream));
            }
            this.restrictionDeletions = new java.util.ArrayList<>(restrictionDeletionsCount);
            for (int i = 0; i < restrictionDeletionsCount; i++) {
                restrictionDeletions.add(UnresolvedMosaicIdDto.loadFromBinary(stream));
            }
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param restrictionFlags Account restriction flags.
     * @param restrictionAdditions Account restriction additions.
     * @param restrictionDeletions Account restriction deletions.
     */
    protected AccountMosaicRestrictionTransactionBodyBuilder(final EnumSet<AccountRestrictionFlagsDto> restrictionFlags, final List<UnresolvedMosaicIdDto> restrictionAdditions, final List<UnresolvedMosaicIdDto> restrictionDeletions) {
        GeneratorUtils.notNull(restrictionFlags, "restrictionFlags is null");
        GeneratorUtils.notNull(restrictionAdditions, "restrictionAdditions is null");
        GeneratorUtils.notNull(restrictionDeletions, "restrictionDeletions is null");
        this.restrictionFlags = restrictionFlags;
        this.accountRestrictionTransactionBody_Reserved1 = 0;
        this.restrictionAdditions = restrictionAdditions;
        this.restrictionDeletions = restrictionDeletions;
    }

    /**
     * Creates an instance of AccountMosaicRestrictionTransactionBodyBuilder.
     *
     * @param restrictionFlags Account restriction flags.
     * @param restrictionAdditions Account restriction additions.
     * @param restrictionDeletions Account restriction deletions.
     * @return Instance of AccountMosaicRestrictionTransactionBodyBuilder.
     */
    public static AccountMosaicRestrictionTransactionBodyBuilder create(final EnumSet<AccountRestrictionFlagsDto> restrictionFlags, final List<UnresolvedMosaicIdDto> restrictionAdditions, final List<UnresolvedMosaicIdDto> restrictionDeletions) {
        return new AccountMosaicRestrictionTransactionBodyBuilder(restrictionFlags, restrictionAdditions, restrictionDeletions);
    }

    /**
     * Gets account restriction flags.
     *
     * @return Account restriction flags.
     */
    public EnumSet<AccountRestrictionFlagsDto> getRestrictionFlags() {
        return this.restrictionFlags;
    }

    /**
     * Gets reserved padding to align restrictionAdditions on 8-byte boundary.
     *
     * @return Reserved padding to align restrictionAdditions on 8-byte boundary.
     */
    private int getAccountRestrictionTransactionBody_Reserved1() {
        return this.accountRestrictionTransactionBody_Reserved1;
    }

    /**
     * Gets account restriction additions.
     *
     * @return Account restriction additions.
     */
    public List<UnresolvedMosaicIdDto> getRestrictionAdditions() {
        return this.restrictionAdditions;
    }

    /**
     * Gets account restriction deletions.
     *
     * @return Account restriction deletions.
     */
    public List<UnresolvedMosaicIdDto> getRestrictionDeletions() {
        return this.restrictionDeletions;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += AccountRestrictionFlagsDto.values()[0].getSize(); // restrictionFlags
        size += 1; // restrictionAdditionsCount
        size += 1; // restrictionDeletionsCount
        size += 4; // accountRestrictionTransactionBody_Reserved1
        size += this.restrictionAdditions.stream().mapToInt(o -> o.getSize()).sum();
        size += this.restrictionDeletions.stream().mapToInt(o -> o.getSize()).sum();
        return size;
    }

    /**
     * Creates an instance of AccountMosaicRestrictionTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountMosaicRestrictionTransactionBodyBuilder.
     */
    public static AccountMosaicRestrictionTransactionBodyBuilder loadFromBinary(final DataInputStream stream) {
        return new AccountMosaicRestrictionTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final short bitMask = (short) GeneratorUtils.toLong(AccountRestrictionFlagsDto.class, this.restrictionFlags);
            dataOutputStream.writeShort(Short.reverseBytes(bitMask));
            dataOutputStream.writeByte((byte) this.restrictionAdditions.size());
            dataOutputStream.writeByte((byte) this.restrictionDeletions.size());
            dataOutputStream.writeInt(Integer.reverseBytes(this.getAccountRestrictionTransactionBody_Reserved1()));
            for (int i = 0; i < this.restrictionAdditions.size(); i++) {
                final byte[] restrictionAdditionsBytes = this.restrictionAdditions.get(i).serialize();
                dataOutputStream.write(restrictionAdditionsBytes, 0, restrictionAdditionsBytes.length);
            }
            for (int i = 0; i < this.restrictionDeletions.size(); i++) {
                final byte[] restrictionDeletionsBytes = this.restrictionDeletions.get(i).serialize();
                dataOutputStream.write(restrictionDeletionsBytes, 0, restrictionDeletionsBytes.length);
            }
        });
    }
}
