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

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.*;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.sdk.model.transaction.*;
import io.vertx.core.json.JsonObject;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Transaction mapper. */
public class TransactionMapper implements Function<JsonObject, Transaction> {
  /* Transaction json. */
  JsonObject transaction;
  /* Network type. */
  NetworkType networkType;

  /**
   * Gets the common properties for all transactions.
   *
   * @param jsonObject Json object.
   * @return Transaction.
   */
  protected void extractCommonProperties(final JsonObject jsonObject) {
    transaction = jsonObject.getJsonObject("transaction");
    networkType = NetworkType.rawValueOf(transaction.getInteger("network"));
  }

  /**
   * Gets the common properties for all transactions.
   *
   * @param factory Transaction factory.
   * @param jsonObject Json object.
   * @return Transaction.
   */
  protected <T extends Transaction> T appendCommonPropertiesAndBuildTransaction(
      final TransactionFactory<T> factory, final JsonObject jsonObject) {
    final TransactionInfo transactionInfo =
        this.createTransactionInfo(jsonObject.getJsonObject("meta"));
    final Deadline deadline = new Deadline(extractBigInteger(transaction, "deadline"));
    final Integer version = transaction.getInteger("version");
    final BigInteger maxFee = extractBigInteger(transaction, "maxFee");
    final String signature = transaction.getString("signature");
    final PublicAccount signer =
        new PublicAccount(transaction.getString("signerPublicKey"), networkType);
    return factory
        .transactionInfo(transactionInfo)
        .signer(signer)
        .deadline(deadline)
        .version(version)
        .maxFee(maxFee)
        .signature(signature)
        .build();
  }

  /**
   * Converts from Json to a transaction.
   *
   * @param jsonObject Json object.
   * @return Transaction.
   */
  @Override
  public Transaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final TransactionType type =
        TransactionType.rawValueOf(transaction.getInteger("type").shortValue());
    if (type == TransactionType.TRANSFER) {
      return new TransferTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.REGISTER_NAMESPACE) {
      return new RegisterNamespaceTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.MOSAIC_DEFINITION) {
      return new MosaicDefinitionTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.MOSAIC_SUPPLY_CHANGE) {
      return new MosaicSupplyChangeTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.MODIFY_MULTISIG_ACCOUNT) {
      return new MultisigModificationTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.AGGREGATE_COMPLETE
        || type == TransactionType.AGGREGATE_BONDED) {
      return new AggregateTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.LOCK) {
      return new HashLockTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.SECRET_LOCK) {
      return new SecretLockTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.SECRET_PROOF) {
      return new SecretProofTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.MOSAIC_ALIAS) {
      return new MosaicAliasTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.ADDRESS_ALIAS) {
      return new AddressAliasTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.ACCOUNT_MOSAIC_RESTRICTION) {
      return new AccountMosaicRestrictionModificationTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.ACCOUNT_ADDRESS_RESTRICTION) {
      return new AccountAddressRestrictionModificationTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.ACCOUNT_OPERATION_RESTRICTION) {
      return new AccountOperationRestrictionModificationTransactionMapper().apply(jsonObject);
    } else if (type == TransactionType.MOSAIC_GLOBAL_RESTRICTION) {
      return new MosaicGlobalRestrictionTransactionMapper().apply(jsonObject);
    }
    throw new UnsupportedOperationException("Unimplemented Transaction type");
  }

  /**
   * Gets a BigInteger from the json.
   *
   * @param jsonObject Json object.
   * @param name Property name.
   * @return BigInteger value of name.
   */
  protected BigInteger extractBigInteger(final JsonObject jsonObject, final String name) {
    return BigInteger.valueOf(jsonObject.getLong(name));
  }

  /**
   * Creates the transaction info.
   *
   * @param jsonObject Json object.
   * @return Transaction info.
   */
  private TransactionInfo createTransactionInfo(final JsonObject jsonObject) {
    if (jsonObject.containsKey("hash")) {
      return TransactionInfo.create(
          extractBigInteger(jsonObject, "height"),
          jsonObject.getInteger("index"),
          "",
          jsonObject.getString("hash"),
          jsonObject.getString("merkleComponentHash"));
    } else if (jsonObject.containsKey("aggregateHash")) {
      return TransactionInfo.createAggregate(
          extractBigInteger(jsonObject, "height"),
          jsonObject.getInteger("index"),
          "",
          jsonObject.getString("aggregateHash"),
          jsonObject.getJsonObject("aggregateId").getString("$oid"));
    } else {
      return TransactionInfo.create(
          extractBigInteger(jsonObject, "height"),
          jsonObject.getString("hash"),
          jsonObject.getString("merkleComponentHash"));
    }
  }
}

/** Transfer transaction mapper. */
class TransferTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to transfer transaction.
   *
   * @param jsonObject Json object.
   * @return Transfer transaction.
   */
  @Override
  public TransferTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    List<Mosaic> mosaics = new ArrayList<>();
    if (transaction.getJsonArray("mosaics") != null) {
      mosaics =
          transaction.getJsonArray("mosaics").stream()
              .map(item -> (JsonObject) item)
              .map(new MosaicMapper())
              .collect(Collectors.toList());
    }

    Message message = PlainMessage.Empty;
    if (transaction.getJsonObject("message") != null) {
      message =
          new PlainMessage(
              new String(
                  Hex.decode(transaction.getJsonObject("message").getString("payload")),
                  StandardCharsets.UTF_8));
    }
    final String recipient = transaction.getString("recipientAddress");
    UnresolvedAddress unresolvedAddress;
    if (recipient.startsWith("01")) {
      final String namespaceString = recipient.substring(1, 10);
      final byte[] bytes = ConvertUtils.getBytes(namespaceString);
      final BigInteger namespaceId = new BigInteger(bytes);
      unresolvedAddress = NamespaceId.createFromId(namespaceId);
    } else {
      unresolvedAddress = Address.createFromEncoded(recipient);
    }
    final TransferTransactionFactory transferTransactionFactory =
        TransferTransactionFactory.create(networkType, unresolvedAddress, mosaics, message);
    return appendCommonPropertiesAndBuildTransaction(transferTransactionFactory, jsonObject);
  }
}

/** Register namespace transaction mapper. */
class RegisterNamespaceTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to register namespace transaction.
   *
   * @param jsonObject Json object.
   * @return Register namespace transaction.
   */
  @Override
  public NamespaceRegistrationTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final NamespaceRegistrationType namespaceType =
        NamespaceRegistrationType.rawValueOf(transaction.getInteger("registrationType"));
    final String namespaceName =
        new String(Hex.decode(transaction.getString("name")), StandardCharsets.UTF_8);
    final NamespaceRegistrationTransactionFactory namespaceRegistrationTransactionFactory =
        namespaceType == NamespaceRegistrationType.ROOT_NAMESPACE
            ? NamespaceRegistrationTransactionFactory.createRootNamespace(
                networkType, namespaceName, extractBigInteger(transaction, "duration"))
            : NamespaceRegistrationTransactionFactory.createSubNamespace(
                networkType,
                namespaceName,
                NamespaceId.createFromId(extractBigInteger(transaction, "parentId")));
    return appendCommonPropertiesAndBuildTransaction(
        namespaceRegistrationTransactionFactory, jsonObject);
  }
}

/** Mosaic definition transaction mapper. */
class MosaicDefinitionTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to mosaic definition transaction.
   *
   * @param jsonObject Json object.
   * @return Mosaic definition transaction.
   */
  @Override
  public MosaicDefinitionTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final int flags = transaction.getLong("flags").intValue();
    final MosaicFlags mosaicFlags = MosaicFlags.create(flags);
    final int divisibility = transaction.getInteger("divisibility");
    final Long duration = transaction.getLong("duration");

    final MosaicDefinitionTransactionFactory mosaicDefinitionTransactionFactory =
        MosaicDefinitionTransactionFactory.create(
            networkType,
            MosaicNonce.createFromBigInteger(extractBigInteger(transaction, "nonce")),
            MapperUtils.getMosaicIdFromJson(transaction, "id"),
            mosaicFlags,
            divisibility,
            new BlockDuration(duration));
    return appendCommonPropertiesAndBuildTransaction(
        mosaicDefinitionTransactionFactory, jsonObject);
  }
}

/** Mosaic supply change transaction mapper. */
class MosaicSupplyChangeTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to mosaic supply change transaction.
   *
   * @param jsonObject Json object.
   * @return Mosaic supply change transaction.
   */
  @Override
  public MosaicSupplyChangeTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final MosaicSupplyChangeTransactionFactory mosaicSupplyChangeTransactionFactory =
        MosaicSupplyChangeTransactionFactory.create(
            networkType,
            MapperUtils.getMosaicIdFromJson(transaction, "mosaicId"),
            MosaicSupplyChangeActionType.rawValueOf(transaction.getInteger("action")),
            extractBigInteger(transaction, "delta"));
    return appendCommonPropertiesAndBuildTransaction(
        mosaicSupplyChangeTransactionFactory, jsonObject);
  }
}

/** Multisig modification transaction mapper. */
class MultisigModificationTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to multisig modification transaction.
   *
   * @param jsonObject Json object.
   * @return Multisig modification transaction.
   */
  @Override
  public MultisigAccountModificationTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final List<PublicAccount> publicKeyAdditions =
        getPublicKeyList(transaction, "publicKeyAdditions");
    final List<PublicAccount> publicKeyDeletions =
        getPublicKeyList(transaction, "publicKeyDeletions");
    final MultisigAccountModificationTransactionFactory mosaicSupplyChangeTransactionFactory =
        MultisigAccountModificationTransactionFactory.create(
            networkType,
            transaction.getInteger("minApprovalDelta").byteValue(),
            transaction.getInteger("minRemovalDelta").byteValue(),
            publicKeyAdditions,
            publicKeyDeletions);
    return appendCommonPropertiesAndBuildTransaction(
        mosaicSupplyChangeTransactionFactory, jsonObject);
  }

  private List<PublicAccount> getPublicKeyList(final JsonObject jsonObject, final String name) {
    return jsonObject.getJsonArray(name).stream()
        .map(item -> PublicAccount.createFromPublicKey((String) item, networkType))
        .collect(Collectors.toList());
  }
}

/** Aggregate transaction mapper. */
class AggregateTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to aggregate transaction.
   *
   * @param jsonObject Json object.
   * @return Aggregate transaction.
   */
  @Override
  public AggregateTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
    if (transaction.getJsonArray("cosignatures") != null) {
      cosignatures =
          transaction.getJsonArray("cosignatures").stream()
              .map(item -> (JsonObject) item)
              .map(
                  aggregateCosignature ->
                      new AggregateTransactionCosignature(
                          aggregateCosignature.getString("signature"),
                          new PublicAccount(
                              aggregateCosignature.getString("signerPublicKey"), networkType)))
              .collect(Collectors.toList());
    }
    final TransactionType type =
        TransactionType.rawValueOf(transaction.getInteger("type").shortValue());
    final AggregateTransactionFactory aggregateTransactionFactory =
        AggregateTransactionFactory.create(
            TransactionType.rawValueOf(transaction.getInteger("type").shortValue()),
            networkType,
            new ArrayList<>(),
            cosignatures);
    return appendCommonPropertiesAndBuildTransaction(aggregateTransactionFactory, jsonObject);
  }
}

/** Lock funds transaction mapper. */
class HashLockTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to lock funds transaction.
   *
   * @param jsonObject Json object.
   * @return Lock funds transaction.
   */
  @Override
  public HashLockTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final MosaicId mosaicId = MapperUtils.getMosaicIdFromJson(transaction, "mosaicId");
    final BigInteger amount = BigInteger.valueOf(transaction.getInteger("amount"));
    final HashLockTransactionFactory hashLockTransactionFactory =
        HashLockTransactionFactory.create(
            networkType,
            new Mosaic(mosaicId, amount),
            extractBigInteger(transaction, "duration"),
            new SignedTransaction(
                "", transaction.getString("hash"), TransactionType.AGGREGATE_BONDED));
    return appendCommonPropertiesAndBuildTransaction(hashLockTransactionFactory, jsonObject);
  }
}

/** Secret lock transaction mapper. */
class SecretLockTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to secret lock transaction.
   *
   * @param jsonObject Json object.
   * @return Secret lock transaction.
   */
  @Override
  public SecretLockTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final SecretLockTransactionFactory secretLockTransactionFactory =
        SecretLockTransactionFactory.create(
            networkType,
            new MosaicMapper("mosaicId").apply(transaction),
            extractBigInteger(transaction, "duration"),
            LockHashAlgorithmType.rawValueOf(transaction.getInteger("hashAlgorithm")),
            transaction.getString("secret"),
            Address.createFromEncoded(transaction.getString("recipientAddress")));
    return appendCommonPropertiesAndBuildTransaction(secretLockTransactionFactory, jsonObject);
  }
}

/** Secret proof transaction mapper. */
class SecretProofTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to secret proof transaction.
   *
   * @param jsonObject Json object.
   * @return Secret proof transaction.
   */
  @Override
  public SecretProofTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final SecretProofTransactionFactory secretProofTransactionFactory =
        SecretProofTransactionFactory.create(
            networkType,
            LockHashAlgorithmType.rawValueOf(transaction.getInteger("hashAlgorithm")),
            Address.createFromEncoded(transaction.getString("recipientAddress")),
            transaction.getString("secret"),
            transaction.getString("proof"));
    return appendCommonPropertiesAndBuildTransaction(secretProofTransactionFactory, jsonObject);
  }
}

/** Mosaic alias transaction mapper. */
class MosaicAliasTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to mosaic alias transaction.
   *
   * @param jsonObject Json object.
   * @return Mosaic alias transaction.
   */
  @Override
  public MosaicAliasTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final MosaicAliasTransactionFactory mosaicAliasTransactionFactory =
        MosaicAliasTransactionFactory.create(
            networkType,
            AliasAction.rawValueOf(transaction.getInteger("aliasAction").byteValue()),
            NamespaceId.createFromId(extractBigInteger(transaction, "namespaceId")),
            MapperUtils.getMosaicIdFromJson(transaction, "mosaicId"));
    return appendCommonPropertiesAndBuildTransaction(mosaicAliasTransactionFactory, jsonObject);
  }
}

/** Address alias transaction mapper. */
class AddressAliasTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to Address alias transaction.
   *
   * @param jsonObject Json object.
   * @return Address alias transaction.
   */
  @Override
  public AddressAliasTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final AddressAliasTransactionFactory addressAliasTransactionFactory =
        AddressAliasTransactionFactory.create(
            networkType,
            AliasAction.rawValueOf(transaction.getInteger("aliasAction").byteValue()),
            NamespaceId.createFromId(extractBigInteger(transaction, "namespaceId")),
            Address.createFromEncoded(transaction.getString("address")));
    return appendCommonPropertiesAndBuildTransaction(addressAliasTransactionFactory, jsonObject);
  }
}

/** Account mosaic restriction modification alias transaction mapper. */
class AccountMosaicRestrictionModificationTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to Account mosaic restriction modification alias transaction.
   *
   * @param jsonObject Json object.
   * @return Address alias transaction.
   */
  @Override
  public AccountMosaicRestrictionTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final List<UnresolvedMosaicId> restrictionAdditions =
        getMosaicIdList(transaction, "restrictionAdditions");
    final List<UnresolvedMosaicId> restrictionDeletions =
        getMosaicIdList(transaction, "restrictionDeletions");
    final AccountMosaicRestrictionTransactionFactory accountMosaicRestrictionTransactionFactory =
        AccountMosaicRestrictionTransactionFactory.create(
            networkType,
            AccountRestrictionType.rawValueOf(transaction.getInteger("restrictionType")),
            restrictionAdditions,
            restrictionDeletions);
    return appendCommonPropertiesAndBuildTransaction(
        accountMosaicRestrictionTransactionFactory, jsonObject);
  }

  private List<UnresolvedMosaicId> getMosaicIdList(final JsonObject jsonObject, final String name) {
    return jsonObject.getJsonArray(name).stream()
        .map(item -> new MosaicId(BigInteger.valueOf((long) item)))
        .collect(Collectors.toList());
  }
}

/** Account address restriction modification alias transaction mapper. */
class AccountAddressRestrictionModificationTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to Account address restriction modification alias transaction.
   *
   * @param jsonObject Json object.
   * @return Address alias transaction.
   */
  @Override
  public AccountAddressRestrictionTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final List<UnresolvedAddress> restrictionAdditions =
        getAddressList(transaction, "restrictionAdditions");
    final List<UnresolvedAddress> restrictionDeletions =
        getAddressList(transaction, "restrictionDeletions");
    final AccountAddressRestrictionTransactionFactory accountAddressRestrictionTransactionFactory =
        AccountAddressRestrictionTransactionFactory.create(
            networkType,
            AccountRestrictionType.rawValueOf(transaction.getInteger("restrictionType")),
            restrictionAdditions,
            restrictionDeletions);
    return appendCommonPropertiesAndBuildTransaction(
        accountAddressRestrictionTransactionFactory, jsonObject);
  }

  private List<UnresolvedAddress> getAddressList(final JsonObject jsonObject, final String name) {
    return jsonObject.getJsonArray(name).stream()
        .map(item -> new Address((String) item, networkType))
        .collect(Collectors.toList());
  }
}

/** Account transaction type restriction modification alias transaction mapper. */
class AccountOperationRestrictionModificationTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to Account transaction type restriction modification alias transaction.
   *
   * @param jsonObject Json object.
   * @return Address alias transaction.
   */
  @Override
  public AccountOperationRestrictionTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final List<TransactionType> restrictionAdditions =
        getTransactionTypeList(transaction, "restrictionAdditions");
    final List<TransactionType> restrictionDeletions =
        getTransactionTypeList(transaction, "restrictionDeletions");
    final AccountOperationRestrictionTransactionFactory
        accountOperationRestrictionTransactionFactory =
            AccountOperationRestrictionTransactionFactory.create(
                networkType,
                AccountRestrictionType.rawValueOf(transaction.getInteger("restrictionType")),
                restrictionAdditions,
                restrictionDeletions);
    return appendCommonPropertiesAndBuildTransaction(
        accountOperationRestrictionTransactionFactory, jsonObject);
  }

  private List<TransactionType> getTransactionTypeList(
      final JsonObject jsonObject, final String name) {
    return jsonObject.getJsonArray(name).stream()
        .map(item -> TransactionType.rawValueOf((short) item))
        .collect(Collectors.toList());
  }
}

/** Mosaic global restriction transaction mapper. */
class MosaicGlobalRestrictionTransactionMapper extends TransactionMapper {
  /**
   * Converts from json to mosaic global restriction transaction.
   *
   * @param jsonObject Json object.
   * @return Mosaic global restriction transaction.
   */
  @Override
  public MosaicGlobalRestrictionTransaction apply(final JsonObject jsonObject) {
    extractCommonProperties(jsonObject);
    final MosaicGlobalRestrictionTransactionFactory mosaicGlobalRestrictionTransactionFactory =
        MosaicGlobalRestrictionTransactionFactory.create(
                networkType,
                MapperUtils.getMosaicIdFromJson(transaction, "mosaicId"),
                extractBigInteger(transaction, "restrictionKey"),
                extractBigInteger(transaction, "newRestrictionValue"),
                MosaicRestrictionType.rawValueOf(
                    transaction.getInteger("newRestrictionType").byteValue()))
            .referenceMosaicId(MapperUtils.getMosaicIdFromJson(transaction, "mosaicId"))
            .previousRestrictionType(
                MosaicRestrictionType.rawValueOf(
                    transaction.getInteger("previousRestrictionType").byteValue()))
            .previousRestrictionValue(extractBigInteger(transaction, "previousRestrictionValue"));
    return appendCommonPropertiesAndBuildTransaction(
        mosaicGlobalRestrictionTransactionFactory, jsonObject);
  }
}
