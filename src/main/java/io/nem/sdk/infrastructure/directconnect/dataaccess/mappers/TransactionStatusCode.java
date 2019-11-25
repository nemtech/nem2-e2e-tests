 /**
 * ** Copyright (c) 2016-present,
 * ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 * **
 * ** This file is part of Catapult.
 * **
 * ** Catapult is free software: you can redistribute it and/or modify
 * ** it under the terms of the GNU Lesser General Public License as published by
 * ** the Free Software Foundation, either version 3 of the License, or
 * ** (at your option) any later version.
 * **
 * ** Catapult is distributed in the hope that it will be useful,
 * ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 * ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * ** GNU Lesser General Public License for more details.
 * **
 * ** You should have received a copy of the GNU Lesser General Public License
 * ** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.sdk.infrastructure.directconnect.dataaccess.mappers;

/** Transaction status codes. */
public enum TransactionStatusCode {
  SUCCESS(0x00000000),
  NEUTRAL(0x40000000),
  FAILURE(0x80000000),
  FAILURE_CORE_PAST_DEADLINE(0x80430001),
  FAILURE_CORE_FUTURE_DEADLINE(0x80430002),
  FAILURE_CORE_INSUFFICIENT_BALANCE(0x80430003),
  FAILURE_CORE_TOO_MANY_TRANSACTIONS(0x80430004),
  FAILURE_CORE_NEMESIS_ACCOUNT_SIGNED_AFTER_NEMESIS_BLOCK(0x80430005),
  FAILURE_CORE_WRONG_NETWORK(0x80430006),
  FAILURE_CORE_INVALID_ADDRESS(0x80430007),
  FAILURE_CORE_INVALID_VERSION(0x80430008),
  FAILURE_CORE_INVALID_TRANSACTION_FEE(0x80430009),
  FAILURE_CORE_BLOCK_HARVESTER_INELIGIBLE(0x8043000A),
  FAILURE_CORE_ZERO_ADDRESS(0x8043000B),
  FAILURE_CORE_ZERO_PUBLIC_KEY(0x8043000C),
  FAILURE_CORE_NONZERO_INTERNAL_PADDING(0x8043000D),
  FAILURE_HASH_ALREADY_EXISTS(0x81490001),
  FAILURE_SIGNATURE_NOT_VERIFIABLE(0x80530001),
  FAILURE_ACCOUNTLINK_INVALID_ACTION(0x804C0001),
  FAILURE_ACCOUNTLINK_LINK_ALREADY_EXISTS(0x804C0002),
  FAILURE_ACCOUNTLINK_UNKNOWN_LINK(0x804C0003),
  FAILURE_ACCOUNTLINK_INCONSISTENT_UNLINK_DATA(0x804C0004),
  FAILURE_ACCOUNTLINK_REMOTE_ACCOUNT_INELIGIBLE(0x804C0005),
  FAILURE_ACCOUNTLINK_REMOTE_ACCOUNT_SIGNER_PROHIBITED(0x804C0006),
  FAILURE_ACCOUNTLINK_REMOTE_ACCOUNT_PARTICIPANT_PROHIBITED(0x804C0007),
  FAILURE_AGGREGATE_TOO_MANY_TRANSACTIONS(0x80410001),
  FAILURE_AGGREGATE_NO_TRANSACTIONS(0x80410002),
  FAILURE_AGGREGATE_TOO_MANY_COSIGNATURES(0x80410003),
  FAILURE_AGGREGATE_REDUNDANT_COSIGNATURES(0x80410004),
  FAILURE_AGGREGATE_INELIGIBLE_COSIGNATORIES(0x80410005),
  FAILURE_AGGREGATE_MISSING_COSIGNATURES(0x80410006),
  FAILURE_AGGREGATE_TRANSACTIONS_HASH_MISMATCH(0x80410007),
  FAILURE_LOCKHASH_INVALID_MOSAIC_ID(0x80480001),
  FAILURE_LOCKHASH_INVALID_MOSAIC_AMOUNT(0x80480002),
  FAILURE_LOCKHASH_HASH_ALREADY_EXISTS(0x80480003),
  FAILURE_LOCKHASH_UNKNOWN_HASH(0x80480004),
  FAILURE_LOCKHASH_INACTIVE_HASH(0x80480005),
  FAILURE_LOCKHASH_INVALID_DURATION(0x80480006),
  FAILURE_LOCKSECRET_INVALID_HASH_ALGORITHM(0x80520001),
  FAILURE_LOCKSECRET_HASH_ALREADY_EXISTS(0x80520002),
  FAILURE_LOCKSECRET_PROOF_SIZE_OUT_OF_BOUNDS(0x80520003),
  FAILURE_LOCKSECRET_SECRET_MISMATCH(0x80520004),
  FAILURE_LOCKSECRET_UNKNOWN_COMPOSITE_KEY(0x80520005),
  FAILURE_LOCKSECRET_INACTIVE_SECRET(0x80520006),
  FAILURE_LOCKSECRET_HASH_ALGORITHM_MISMATCH(0x80520007),
  FAILURE_LOCKSECRET_INVALID_DURATION(0x80520008),
  FAILURE_METADATA_VALUE_TOO_SMALL(0x80440001),
  FAILURE_METADATA_VALUE_TOO_LARGE(0x80440002),
  FAILURE_METADATA_VALUE_SIZE_DELTA_TOO_LARGE(0x80440003),
  FAILURE_METADATA_VALUE_SIZE_DELTA_MISMATCH(0x80440004),
  FAILURE_METADATA_VALUE_CHANGE_IRREVERSIBLE(0x80440005),
  FAILURE_MOSAIC_INVALID_DURATION(0x804D0001),
  FAILURE_MOSAIC_INVALID_NAME(0x804D0002),
  FAILURE_MOSAIC_NAME_ID_MISMATCH(0x804D0003),
  FAILURE_MOSAIC_EXPIRED(0x804D0004),
  FAILURE_MOSAIC_OWNER_CONFLICT(0x804D0005),
  FAILURE_MOSAIC_ID_MISMATCH(0x804D0006),
  FAILURE_MOSAIC_PARENT_ID_CONFLICT(0x804D0064),
  FAILURE_MOSAIC_INVALID_PROPERTY(0x804D0065),
  FAILURE_MOSAIC_INVALID_FLAGS(0x804D0066),
  FAILURE_MOSAIC_INVALID_DIVISIBILITY(0x804D0067),
  FAILURE_MOSAIC_INVALID_SUPPLY_CHANGE_ACTION(0x804D0068),
  FAILURE_MOSAIC_INVALID_SUPPLY_CHANGE_AMOUNT(0x804D0069),
  FAILURE_MOSAIC_INVALID_ID(0x804D006A),
  FAILURE_MOSAIC_MODIFICATION_DISALLOWED(0x804D006B),
  FAILURE_MOSAIC_MODIFICATION_NO_CHANGES(0x804D006C),
  FAILURE_MOSAIC_SUPPLY_IMMUTABLE(0x804D006D),
  FAILURE_MOSAIC_SUPPLY_NEGATIVE(0x804D006E),
  FAILURE_MOSAIC_SUPPLY_EXCEEDED(0x804D006F),
  FAILURE_MOSAIC_NON_TRANSFERABLE(0x804D0070),
  FAILURE_MOSAIC_MAX_MOSAICS_EXCEEDED(0x804D0071),
  FAILURE_MOSAIC_REQUIRED_PROPERTY_FLAG_UNSET(0x804D0072),
  FAILURE_MULTISIG_ACCOUNT_IN_BOTH_SETS(0x80550001),
  FAILURE_MULTISIG_MULTIPLE_DELETES(0x80550002),
  FAILURE_MULTISIG_REDUNDANT_MODIFICATION(0x80550003),
  FAILURE_MULTISIG_UNKNOWN_MULTISIG_ACCOUNT(0x80550004),
  FAILURE_MULTISIG_NOT_A_COSIGNATORY(0x80550005),
  FAILURE_MULTISIG_ALREADY_A_COSIGNATORY(0x80550006),
  FAILURE_MULTISIG_MIN_SETTING_OUT_OF_RANGE(0x80550007),
  FAILURE_MULTISIG_MIN_SETTING_LARGER_THAN_NUM_COSIGNATORIES(0x80550008),
  FAILURE_MULTISIG_INVALID_MODIFICATION_ACTION(0x80550009),
  FAILURE_MULTISIG_MAX_COSIGNED_ACCOUNTS(0x8055000A),
  FAILURE_MULTISIG_MAX_COSIGNATORIES(0x8055000B),
  FAILURE_MULTISIG_LOOP(0x8055000C),
  FAILURE_MULTISIG_MAX_MULTISIG_DEPTH(0x8055000D),
  FAILURE_MULTISIG_OPERATION_PROHIBITED_BY_ACCOUNT(0x8055000E),
  FAILURE_NAMESPACE_INVALID_DURATION(0x804E0001),
  FAILURE_NAMESPACE_INVALID_NAME(0x804E0002),
  FAILURE_NAMESPACE_NAME_ID_MISMATCH(0x804E0003),
  FAILURE_NAMESPACE_EXPIRED(0x804E0004),
  FAILURE_NAMESPACE_OWNER_CONFLICT(0x804E0005),
  FAILURE_NAMESPACE_ID_MISMATCH(0x804E0006),
  FAILURE_NAMESPACE_INVALID_REGISTRATION_TYPE(0x804E0064),
  FAILURE_NAMESPACE_ROOT_NAME_RESERVED(0x804E0065),
  FAILURE_NAMESPACE_TOO_DEEP(0x804E0066),
  FAILURE_NAMESPACE_UNKNOWN_PARENT(0x804E0067),
  FAILURE_NAMESPACE_ALREADY_EXISTS(0x804E0068),
  FAILURE_NAMESPACE_ALREADY_ACTIVE(0x804E0069),
  FAILURE_NAMESPACE_ETERNAL_AFTER_NEMESIS_BLOCK(0x804E006A),
  FAILURE_NAMESPACE_MAX_CHILDREN_EXCEEDED(0x804E006B),
  FAILURE_NAMESPACE_ALIAS_INVALID_ACTION(0x804E006C),
  FAILURE_NAMESPACE_UNKNOWN(0x804E006D),
  FAILURE_NAMESPACE_ALIAS_ALREADY_EXISTS(0x804E006E),
  FAILURE_NAMESPACE_UNKNOWN_ALIAS(0x804E006F),
  FAILURE_NAMESPACE_ALIAS_INCONSISTENT_UNLINK_TYPE(0x804E0070),
  FAILURE_NAMESPACE_ALIAS_INCONSISTENT_UNLINK_DATA(0x804E0071),
  FAILURE_NAMESPACE_ALIAS_INVALID_ADDRESS(0x804E0072),
  FAILURE_RESTRICTIONACCOUNT_INVALID_RESTRICTION_FLAGS(0x80500001),
  FAILURE_RESTRICTIONACCOUNT_INVALID_MODIFICATION_ACTION(0x80500002),
  FAILURE_RESTRICTIONACCOUNT_INVALID_MODIFICATION_ADDRESS(0x80500003),
  FAILURE_RESTRICTIONACCOUNT_MODIFICATION_OPERATION_TYPE_INCOMPATIBLE(0x80500004),
  FAILURE_RESTRICTIONACCOUNT_REDUNDANT_MODIFICATION(0x80500005),
  FAILURE_RESTRICTIONACCOUNT_INVALID_MODIFICATION(0x80500006),
  FAILURE_RESTRICTIONACCOUNT_MODIFICATION_COUNT_EXCEEDED(0x80500007),
  FAILURE_RESTRICTIONACCOUNT_NO_MODIFICATIONS(0x80500008),
  FAILURE_RESTRICTIONACCOUNT_VALUES_COUNT_EXCEEDED(0x80500009),
  FAILURE_RESTRICTIONACCOUNT_INVALID_VALUE(0x8050000A),
  FAILURE_RESTRICTIONACCOUNT_ADDRESS_INTERACTION_PROHIBITED(0x8050000B),
  FAILURE_RESTRICTIONACCOUNT_MOSAIC_TRANSFER_PROHIBITED(0x8050000C),
  FAILURE_RESTRICTIONACCOUNT_OPERATION_TYPE_PROHIBITED(0x8050000D),
  FAILURE_RESTRICTIONMOSAIC_INVALID_RESTRICTION_TYPE(0x80510001),
  FAILURE_RESTRICTIONMOSAIC_PREVIOUS_VALUE_MISMATCH(0x80510002),
  FAILURE_RESTRICTIONMOSAIC_PREVIOUS_VALUE_MUST_BE_ZERO(0x80510003),
  FAILURE_RESTRICTIONMOSAIC_MAX_RESTRICTIONS_EXCEEDED(0x80510004),
  FAILURE_RESTRICTIONMOSAIC_CANNOT_DELETE_NONEXISTENT_RESTRICTION(0x80510005),
  FAILURE_RESTRICTIONMOSAIC_UNKNOWN_GLOBAL_RESTRICTION(0x80510006),
  FAILURE_RESTRICTIONMOSAIC_INVALID_GLOBAL_RESTRICTION(0x80510007),
  FAILURE_RESTRICTIONMOSAIC_ACCOUNT_UNAUTHORIZED(0x80510008),
  FAILURE_TRANSFER_MESSAGE_TOO_LARGE(0x80540001),
  FAILURE_TRANSFER_OUT_OF_ORDER_MOSAICS(0x80540002),
  FAILURE_CHAIN_UNLINKED(0x80FF0001),
  FAILURE_CHAIN_BLOCK_NOT_HIT(0x80FF0002),
  FAILURE_CHAIN_BLOCK_INCONSISTENT_STATE_HASH(0x80FF0003),
  FAILURE_CHAIN_BLOCK_INCONSISTENT_RECEIPTS_HASH(0x80FF0004),
  FAILURE_CHAIN_UNCONFIRMED_CACHE_TOO_FULL(0x80FF0005),
  FAILURE_CONSUMER_EMPTY_INPUT(0x80FE0001),
  FAILURE_CONSUMER_BLOCK_TRANSACTIONS_HASH_MISMATCH(0x80FE0002),
  NEUTRAL_CONSUMER_HASH_IN_RECENCY_CACHE(0x41FE0003),
  FAILURE_CONSUMER_REMOTE_CHAIN_TOO_MANY_BLOCKS(0x80FE0004),
  FAILURE_CONSUMER_REMOTE_CHAIN_IMPROPER_LINK(0x80FE0005),
  FAILURE_CONSUMER_REMOTE_CHAIN_DUPLICATE_TRANSACTIONS(0x80FE0006),
  FAILURE_CONSUMER_REMOTE_CHAIN_UNLINKED(0x80FE0007),
  FAILURE_CONSUMER_REMOTE_CHAIN_DIFFICULTIES_MISMATCH(0x80FE0008),
  FAILURE_CONSUMER_REMOTE_CHAIN_SCORE_NOT_BETTER(0x80FE0009),
  FAILURE_CONSUMER_REMOTE_CHAIN_TOO_FAR_BEHIND(0x80FE000A),
  FAILURE_CONSUMER_REMOTE_CHAIN_TOO_FAR_IN_FUTURE(0x80FE000B),
  FAILURE_CONSUMER_BATCH_SIGNATURE_NOT_VERIFIABLE(0x80FE000C),
  FAILURE_EXTENSION_PARTIAL_TRANSACTION_CACHE_PRUNE(0x80450001),
  FAILURE_EXTENSION_PARTIAL_TRANSACTION_DEPENDENCY_REMOVED(0x80450002),
  FAILURE_EXTENSION_READ_RATE_LIMIT_EXCEEDED(0x80450003);

  private final long value;

  /**
   * Constructor.
   *
   * @param value Enum value.
   */
  TransactionStatusCode(final long value) {
    this.value = value;
  }

  /**
   * Gets enum value.
   *
   * @param value Raw value.
   * @return Enum value.
   */
  public static TransactionStatusCode rawValueOf(final int value) {
    for (TransactionStatusCode current : TransactionStatusCode.values()) {
      if (value == current.getValue()) {
        return current;
      }
    }
    throw new IllegalArgumentException(
        value + " was not a backing value for TransactionStatusCode.");
  }

  /**
   * Returns enum value.
   *
   * @return enum value
   */
  public long getValue() {
    return this.value;
  }
}
