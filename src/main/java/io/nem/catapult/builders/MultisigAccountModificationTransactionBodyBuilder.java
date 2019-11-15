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

/** Binary layout for a multisig account modification transaction. */
public final class MultisigAccountModificationTransactionBodyBuilder {
    /** Relative change of the minimal number of cosignatories required when removing an account. */
    private final byte minRemovalDelta;
    /** Relative change of the minimal number of cosignatories required when approving a transaction. */
    private final byte minApprovalDelta;
    /** Reserved padding to align publicKeyAdditions on 8-byte boundary. */
    private final int multisigAccountModificationTransactionBody_Reserved1;
    /** Cosignatory public key additions. */
    private final List<KeyDto> publicKeyAdditions;
    /** Cosignatory public key deletions. */
    private final List<KeyDto> publicKeyDeletions;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MultisigAccountModificationTransactionBodyBuilder(final DataInputStream stream) {
        try {
            this.minRemovalDelta = stream.readByte();
            this.minApprovalDelta = stream.readByte();
            final byte publicKeyAdditionsCount = stream.readByte();
            final byte publicKeyDeletionsCount = stream.readByte();
            this.multisigAccountModificationTransactionBody_Reserved1 = Integer.reverseBytes(stream.readInt());
            this.publicKeyAdditions = new java.util.ArrayList<>(publicKeyAdditionsCount);
            for (int i = 0; i < publicKeyAdditionsCount; i++) {
                publicKeyAdditions.add(KeyDto.loadFromBinary(stream));
            }
            this.publicKeyDeletions = new java.util.ArrayList<>(publicKeyDeletionsCount);
            for (int i = 0; i < publicKeyDeletionsCount; i++) {
                publicKeyDeletions.add(KeyDto.loadFromBinary(stream));
            }
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param minRemovalDelta Relative change of the minimal number of cosignatories required when removing an account.
     * @param minApprovalDelta Relative change of the minimal number of cosignatories required when approving a transaction.
     * @param publicKeyAdditions Cosignatory public key additions.
     * @param publicKeyDeletions Cosignatory public key deletions.
     */
    protected MultisigAccountModificationTransactionBodyBuilder(final byte minRemovalDelta, final byte minApprovalDelta, final List<KeyDto> publicKeyAdditions, final List<KeyDto> publicKeyDeletions) {
        GeneratorUtils.notNull(publicKeyAdditions, "publicKeyAdditions is null");
        GeneratorUtils.notNull(publicKeyDeletions, "publicKeyDeletions is null");
        this.minRemovalDelta = minRemovalDelta;
        this.minApprovalDelta = minApprovalDelta;
        this.multisigAccountModificationTransactionBody_Reserved1 = 0;
        this.publicKeyAdditions = publicKeyAdditions;
        this.publicKeyDeletions = publicKeyDeletions;
    }

    /**
     * Creates an instance of MultisigAccountModificationTransactionBodyBuilder.
     *
     * @param minRemovalDelta Relative change of the minimal number of cosignatories required when removing an account.
     * @param minApprovalDelta Relative change of the minimal number of cosignatories required when approving a transaction.
     * @param publicKeyAdditions Cosignatory public key additions.
     * @param publicKeyDeletions Cosignatory public key deletions.
     * @return Instance of MultisigAccountModificationTransactionBodyBuilder.
     */
    public static MultisigAccountModificationTransactionBodyBuilder create(final byte minRemovalDelta, final byte minApprovalDelta, final List<KeyDto> publicKeyAdditions, final List<KeyDto> publicKeyDeletions) {
        return new MultisigAccountModificationTransactionBodyBuilder(minRemovalDelta, minApprovalDelta, publicKeyAdditions, publicKeyDeletions);
    }

    /**
     * Gets relative change of the minimal number of cosignatories required when removing an account.
     *
     * @return Relative change of the minimal number of cosignatories required when removing an account.
     */
    public byte getMinRemovalDelta() {
        return this.minRemovalDelta;
    }

    /**
     * Gets relative change of the minimal number of cosignatories required when approving a transaction.
     *
     * @return Relative change of the minimal number of cosignatories required when approving a transaction.
     */
    public byte getMinApprovalDelta() {
        return this.minApprovalDelta;
    }

    /**
     * Gets reserved padding to align publicKeyAdditions on 8-byte boundary.
     *
     * @return Reserved padding to align publicKeyAdditions on 8-byte boundary.
     */
    private int getMultisigAccountModificationTransactionBody_Reserved1() {
        return this.multisigAccountModificationTransactionBody_Reserved1;
    }

    /**
     * Gets cosignatory public key additions.
     *
     * @return Cosignatory public key additions.
     */
    public List<KeyDto> getPublicKeyAdditions() {
        return this.publicKeyAdditions;
    }

    /**
     * Gets cosignatory public key deletions.
     *
     * @return Cosignatory public key deletions.
     */
    public List<KeyDto> getPublicKeyDeletions() {
        return this.publicKeyDeletions;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += 1; // minRemovalDelta
        size += 1; // minApprovalDelta
        size += 1; // publicKeyAdditionsCount
        size += 1; // publicKeyDeletionsCount
        size += 4; // multisigAccountModificationTransactionBody_Reserved1
        size += this.publicKeyAdditions.stream().mapToInt(o -> o.getSize()).sum();
        size += this.publicKeyDeletions.stream().mapToInt(o -> o.getSize()).sum();
        return size;
    }

    /**
     * Creates an instance of MultisigAccountModificationTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MultisigAccountModificationTransactionBodyBuilder.
     */
    public static MultisigAccountModificationTransactionBodyBuilder loadFromBinary(final DataInputStream stream) {
        return new MultisigAccountModificationTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeByte(this.getMinRemovalDelta());
            dataOutputStream.writeByte(this.getMinApprovalDelta());
            dataOutputStream.writeByte((byte) this.publicKeyAdditions.size());
            dataOutputStream.writeByte((byte) this.publicKeyDeletions.size());
            dataOutputStream.writeInt(Integer.reverseBytes(this.getMultisigAccountModificationTransactionBody_Reserved1()));
            for (int i = 0; i < this.publicKeyAdditions.size(); i++) {
                final byte[] publicKeyAdditionsBytes = this.publicKeyAdditions.get(i).serialize();
                dataOutputStream.write(publicKeyAdditionsBytes, 0, publicKeyAdditionsBytes.length);
            }
            for (int i = 0; i < this.publicKeyDeletions.size(); i++) {
                final byte[] publicKeyDeletionsBytes = this.publicKeyDeletions.get(i).serialize();
                dataOutputStream.write(publicKeyDeletionsBytes, 0, publicKeyDeletionsBytes.length);
            }
        });
    }
}
