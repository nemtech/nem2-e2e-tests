/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.model.transaction;

import io.nem.sdk.model.account.PublicAccount;

import java.util.List;

/**
 * Modify multisig account transactions are part of the NEM's multisig account system. A modify
 * multisig account transaction holds an array of multisig cosignatory modifications, min number of
 * signatures to approve a transaction and a min number of signatures to remove a cosignatory.
 *
 * @since 1.0
 */
public class MultisigAccountModificationTransaction extends Transaction {

  private final byte minApprovalDelta;
  private final byte minRemovalDelta;
  private final List<PublicAccount> publicAccountsAdditions;
  private final List<PublicAccount> publicAccountsDeletions;

  public MultisigAccountModificationTransaction(
      MultisigAccountModificationTransactionFactory factory) {
    super(factory);
    this.minApprovalDelta = factory.getMinApprovalDelta();
    this.minRemovalDelta = factory.getMinRemovalDelta();
    this.publicAccountsAdditions = factory.getPublicAccountsAdditions();
    this.publicAccountsDeletions = factory.getPublicAccountsDeletions();
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
}
