/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.model.transaction;

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MultisigAccountModificationTransaction}
 */
public class MultisigAccountModificationTransactionFactory extends
    TransactionFactory<MultisigAccountModificationTransaction> {

    private final byte minApprovalDelta;
    private final byte minRemovalDelta;
    private final List<PublicAccount> publicAccountsAdditions;
    private final List<PublicAccount> publicAccountsDeletions;

    private MultisigAccountModificationTransactionFactory(
        NetworkType networkType,
        byte minApprovalDelta,
        byte minRemovalDelta,
        List<PublicAccount> publicAccountsAdditions,
        List<PublicAccount> publicAccountsDeletions) {
        super(TransactionType.MODIFY_MULTISIG_ACCOUNT, networkType);
        Validate.notNull(publicAccountsAdditions, "publicKeyAdditions must not be null");
        Validate.notNull(publicAccountsAdditions, "publicKeyDeletions must not be null");
        this.minApprovalDelta = minApprovalDelta;
        this.minRemovalDelta = minRemovalDelta;
        this.publicAccountsAdditions = publicAccountsAdditions;
        this.publicAccountsDeletions = publicAccountsDeletions;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param minApprovalDelta Minimum approval delta.
     * @param minRemovalDelta Minimum removal delta.
     * @param publicAccountsAdditions List of cosigners accounts to add.
     * @param publicAccountsDeletions List of cosigners accounts to remove.
     * @return Multisig account modification transaction.
     */
    public static MultisigAccountModificationTransactionFactory create(
        NetworkType networkType,
        byte minApprovalDelta,
        byte minRemovalDelta,
        List<PublicAccount> publicAccountsAdditions,
        List<PublicAccount> publicAccountsDeletions) {
        return new MultisigAccountModificationTransactionFactory(networkType, minApprovalDelta, minRemovalDelta, publicAccountsAdditions,
                publicAccountsDeletions);
    }

    /**
     * Return number of signatures needed to approve a transaction. If we are modifying and existing
     * multi-signature account this indicates the relative change of the minimum cosignatories.
     *
     * @return byte
     */
    public byte getMinApprovalDelta() {
        return minApprovalDelta;
    }

    /**
     * Return number of signatures needed to remove a cosignatory. If we are modifying and existing
     * multi-signature account this indicates the relative change of the minimum cosignatories.
     *
     * @return byte
     */
    public byte getMinRemovalDelta() {
        return minRemovalDelta;
    }

    /**
     * The List of cosigner accounts to added from the multi-signature account.
     *
     * @return {@link List} of { @ link PublicAccount }
     */
    public List<PublicAccount> getPublicAccountsAdditions() {
        return publicAccountsAdditions;
    }

    /**
     * The List of cosigner accounts to removed from the multi-signature account.
     *
     * @return {@link List} of { @ link PublicAccount }
     */
    public List<PublicAccount> getPublicAccountsDeletions() {
        return publicAccountsDeletions;
    }

    @Override
    public MultisigAccountModificationTransaction build() {
        return new MultisigAccountModificationTransaction(this);
    }
}
