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

/** Binary layout for a non-embedded multisig account modification transaction. */
public final class MultisigAccountModificationTransactionBuilder extends TransactionBuilder {
    /** Multisig account modification transaction body. */
    private final MultisigAccountModificationTransactionBodyBuilder multisigAccountModificationTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MultisigAccountModificationTransactionBuilder(final DataInputStream stream) {
        super(stream);
        this.multisigAccountModificationTransactionBody = MultisigAccountModificationTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param minRemovalDelta Relative change of the minimal number of cosignatories required when removing an account.
     * @param minApprovalDelta Relative change of the minimal number of cosignatories required when approving a transaction.
     * @param publicKeyAdditions Cosignatory public key additions.
     * @param publicKeyDeletions Cosignatory public key deletions.
     */
    protected MultisigAccountModificationTransactionBuilder(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final byte minRemovalDelta, final byte minApprovalDelta, final List<KeyDto> publicKeyAdditions, final List<KeyDto> publicKeyDeletions) {
        super(signature, signerPublicKey, version, network, type, fee, deadline);
        this.multisigAccountModificationTransactionBody = MultisigAccountModificationTransactionBodyBuilder.create(minRemovalDelta, minApprovalDelta, publicKeyAdditions, publicKeyDeletions);
    }

    /**
     * Creates an instance of MultisigAccountModificationTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signerPublicKey Entity signer's public key.
     * @param version Entity version.
     * @param network Entity network.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param minRemovalDelta Relative change of the minimal number of cosignatories required when removing an account.
     * @param minApprovalDelta Relative change of the minimal number of cosignatories required when approving a transaction.
     * @param publicKeyAdditions Cosignatory public key additions.
     * @param publicKeyDeletions Cosignatory public key deletions.
     * @return Instance of MultisigAccountModificationTransactionBuilder.
     */
    public static MultisigAccountModificationTransactionBuilder create(final SignatureDto signature, final KeyDto signerPublicKey, final byte version, final NetworkTypeDto network, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final byte minRemovalDelta, final byte minApprovalDelta, final List<KeyDto> publicKeyAdditions, final List<KeyDto> publicKeyDeletions) {
        return new MultisigAccountModificationTransactionBuilder(signature, signerPublicKey, version, network, type, fee, deadline, minRemovalDelta, minApprovalDelta, publicKeyAdditions, publicKeyDeletions);
    }

    /**
     * Gets relative change of the minimal number of cosignatories required when removing an account.
     *
     * @return Relative change of the minimal number of cosignatories required when removing an account.
     */
    public byte getMinRemovalDelta() {
        return this.multisigAccountModificationTransactionBody.getMinRemovalDelta();
    }

    /**
     * Gets relative change of the minimal number of cosignatories required when approving a transaction.
     *
     * @return Relative change of the minimal number of cosignatories required when approving a transaction.
     */
    public byte getMinApprovalDelta() {
        return this.multisigAccountModificationTransactionBody.getMinApprovalDelta();
    }

    /**
     * Gets cosignatory public key additions.
     *
     * @return Cosignatory public key additions.
     */
    public List<KeyDto> getPublicKeyAdditions() {
        return this.multisigAccountModificationTransactionBody.getPublicKeyAdditions();
    }

    /**
     * Gets cosignatory public key deletions.
     *
     * @return Cosignatory public key deletions.
     */
    public List<KeyDto> getPublicKeyDeletions() {
        return this.multisigAccountModificationTransactionBody.getPublicKeyDeletions();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.multisigAccountModificationTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of MultisigAccountModificationTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MultisigAccountModificationTransactionBuilder.
     */
    public static MultisigAccountModificationTransactionBuilder loadFromBinary(final DataInputStream stream) {
        return new MultisigAccountModificationTransactionBuilder(stream);
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
            final byte[] multisigAccountModificationTransactionBodyBytes = this.multisigAccountModificationTransactionBody.serialize();
            dataOutputStream.write(multisigAccountModificationTransactionBodyBytes, 0, multisigAccountModificationTransactionBodyBytes.length);
        });
    }
}
