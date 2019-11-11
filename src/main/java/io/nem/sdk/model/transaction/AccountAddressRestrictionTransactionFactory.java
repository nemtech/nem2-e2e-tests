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

import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import org.apache.commons.lang3.Validate;

import java.util.List;

/** Factory of {@link AccountAddressRestrictionTransaction} */
public class AccountAddressRestrictionTransactionFactory
    extends TransactionFactory<AccountAddressRestrictionTransaction> {

  private final AccountRestrictionType restrictionType;

  private final List<UnresolvedAddress> restrictionAdditions;
  private final List<UnresolvedAddress> restrictionDeletions;

  private AccountAddressRestrictionTransactionFactory(
      final NetworkType networkType,
      final AccountRestrictionType restrictionType,
      final List<UnresolvedAddress> restrictionAdditions,
      final List<UnresolvedAddress> restrictionDeletions) {
    super(TransactionType.ACCOUNT_ADDRESS_RESTRICTION, networkType);
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
   * @param restrictionAdditions List of account address restriction to add.
   * @param restrictionDeletions List of account address restriction to delete.
   * @return Account address restriction transaction.
   */
  public static AccountAddressRestrictionTransactionFactory create(
      NetworkType networkType,
      AccountRestrictionType restrictionType,
      final List<UnresolvedAddress> restrictionAdditions,
      final List<UnresolvedAddress> restrictionDeletions) {
    return new AccountAddressRestrictionTransactionFactory(
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
   * Get account address restriction additions list.
   *
   * @return List of {@link UnresolvedAddress}
   */
  public List<UnresolvedAddress> getRestrictionAdditions() {
    return this.restrictionAdditions;
  }

    /**
     * Get account address restriction deletions list.
     *
     * @return List of {@link UnresolvedAddress}
     */
    public List<UnresolvedAddress> getRestrictionDeletions() {
        return this.restrictionDeletions;
    }

  @Override
  public AccountAddressRestrictionTransaction build() {
    return new AccountAddressRestrictionTransaction(this);
  }
}
