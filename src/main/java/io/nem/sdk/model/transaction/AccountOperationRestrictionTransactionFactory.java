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

import io.nem.sdk.model.blockchain.NetworkType;
import org.apache.commons.lang3.Validate;

import java.util.List;

/** Factory of {@link AccountOperationRestrictionTransaction} */
public class AccountOperationRestrictionTransactionFactory
    extends TransactionFactory<AccountOperationRestrictionTransaction> {

  private final AccountRestrictionType restrictionType;

  private final List<TransactionType> restrictionAdditions;
  private final List<TransactionType> restrictionDeletions;

  private AccountOperationRestrictionTransactionFactory(
      final NetworkType networkType,
      final AccountRestrictionType restrictionType,
      final List<TransactionType> restrictionAdditions,
      final List<TransactionType> restrictionDeletions) {
    super(TransactionType.ACCOUNT_OPERATION_RESTRICTION, networkType);
    Validate.notNull(restrictionType, "RestrictionType must not be null");
    Validate.notNull(restrictionAdditions, "restrictionAdditions must not be null");
    Validate.notNull(restrictionDeletions, "restrictionDeletions must not be null");
    this.restrictionType = restrictionType;
    this.restrictionAdditions = restrictionAdditions;
    this.restrictionDeletions = restrictionDeletions;
  }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param restrictionType Restriction type.
   * @param restrictionAdditions List of account operation restriction modifications.
   * @param restrictionDeletions  List of accounts operation to delete.
   * @return Account operation restriction transaction.
   */
  public static AccountOperationRestrictionTransactionFactory create(
      NetworkType networkType,
      AccountRestrictionType restrictionType,
      final List<TransactionType> restrictionAdditions,
      final List<TransactionType> restrictionDeletions) {
    return new AccountOperationRestrictionTransactionFactory(
        networkType, restrictionType, restrictionAdditions, restrictionDeletions);
  }

  /**
   * Get account restriction type
   *
   * @return {@link AccountRestrictionType}
   */
  public AccountRestrictionType getRestrictionType() {
    return this.restrictionType;
  }

  /**
   * Get account operation restriction modifications
   *
   * @return list of {@link TransactionType}
   */
  public List<TransactionType> getRestrictionAdditions() {
    return this.restrictionAdditions;
  }

    /**
     * Get account operation restriction modifications
     *
     * @return list of {@link TransactionType}
     */
    public List<TransactionType> getRestrictionDeletions() {
        return this.restrictionDeletions;
    }

    @Override
  public AccountOperationRestrictionTransaction build() {
    return new AccountOperationRestrictionTransaction(this);
  }
}
