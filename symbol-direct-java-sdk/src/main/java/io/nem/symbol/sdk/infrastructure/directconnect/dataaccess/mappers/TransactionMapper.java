/*
 * Copyright (c) 2016-present,
 * Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 *
 * This file is part of Catapult.
 *
 * Catapult is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catapult is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Catapult.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.message.Message;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.*;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.*;
import io.vertx.core.json.JsonObject;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transaction mapper.
 */
public class TransactionMapper implements Function<JsonObject, Transaction> {

    private TransactionMapperBase resolveTransactionFactory(final JsonObject jsonObject) {
        JsonObject transaction = jsonObject.getJsonObject("transaction");
        final TransactionType type =
                TransactionType.rawValueOf(transaction.getInteger("type").shortValue());
        if (type == TransactionType.TRANSFER) {
            return new TransferTransactionMapper();
        } else if (type == TransactionType.NAMESPACE_REGISTRATION) {
            return new RegisterNamespaceTransactionMapper();
        } else if (type == TransactionType.MOSAIC_DEFINITION) {
            return new MosaicDefinitionTransactionMapper();
        } else if (type == TransactionType.MOSAIC_SUPPLY_CHANGE) {
            return new MosaicSupplyChangeTransactionMapper();
        } else if (type == TransactionType.MULTISIG_ACCOUNT_MODIFICATION) {
            return new MultisigModificationTransactionMapper();
        } else if (type == TransactionType.AGGREGATE_COMPLETE
                || type == TransactionType.AGGREGATE_BONDED) {
            return new AggregateTransactionMapper();
        } else if (type == TransactionType.HASH_LOCK) {
            return new HashLockTransactionMapper();
        } else if (type == TransactionType.SECRET_LOCK) {
            return new SecretLockTransactionMapper();
        } else if (type == TransactionType.SECRET_PROOF) {
            return new SecretProofTransactionMapper();
        } else if (type == TransactionType.MOSAIC_ALIAS) {
            return new MosaicAliasTransactionMapper();
        } else if (type == TransactionType.ADDRESS_ALIAS) {
            return new AddressAliasTransactionMapper();
        } else if (type == TransactionType.ACCOUNT_MOSAIC_RESTRICTION) {
            return new AccountMosaicRestrictionModificationTransactionMapper();
        } else if (type == TransactionType.ACCOUNT_ADDRESS_RESTRICTION) {
            return new AccountAddressRestrictionModificationTransactionMapper();
        } else if (type == TransactionType.ACCOUNT_OPERATION_RESTRICTION) {
            return new AccountOperationRestrictionModificationTransactionMapper();
        } else if (type == TransactionType.MOSAIC_GLOBAL_RESTRICTION) {
            return new MosaicGlobalRestrictionTransactionMapper();
        } else if (type == TransactionType.MOSAIC_ADDRESS_RESTRICTION) {
            return new MosaicAddressRestrictionTransactionMapper();
        } else if (type == TransactionType.ACCOUNT_KEY_LINK) {
            return new AccountKeyLinkTransactionMapper();
        } else if (type == TransactionType.ACCOUNT_METADATA) {
            return new AccountMetadataTransactionMapper();
        } else if (type == TransactionType.MOSAIC_METADATA) {
            return new AccountOperationRestrictionModificationTransactionMapper();
        } else if (type == TransactionType.NAMESPACE_METADATA) {
            return new AccountOperationRestrictionModificationTransactionMapper();
        } else if (type == TransactionType.VRF_KEY_LINK) {
            return new VrfLinkTransactionMapper();
        } else if (type == TransactionType.NODE_KEY_LINK) {
            return new NodeKeyLinkTransactionMapper();
        } else if (type == TransactionType.VOTING_KEY_LINK) {
            return new VotingKeyLinkTransactionMapper();
        }
        throw new UnsupportedOperationException("Unimplemented Transaction type: " + type.toString());
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
                    MapperUtils.toBigInteger(jsonObject, "height"),
                    jsonObject.getInteger("index"),
                    "",
                    jsonObject.getString("hash"),
                    jsonObject.getString("merkleComponentHash"));
        } else if (jsonObject.containsKey("aggregateHash")) {
            return TransactionInfo.createAggregate(
                    MapperUtils.toBigInteger(jsonObject, "height"),
                    jsonObject.getInteger("index"),
                    "",
                    jsonObject.getString("aggregateHash"),
                    jsonObject.getJsonObject("aggregateId").getString("$oid"));
        } else {
            return TransactionInfo.create(
                    MapperUtils.toBigInteger(jsonObject, "height"),
                    jsonObject.getString("hash"),
                    jsonObject.getString("merkleComponentHash"));
        }
    }

    /**
     * Gets the common properties for all transactions.
     *
     * @param factory    Transaction factory.
     * @param jsonObject Json object.
     * @return Transaction.
     */
    protected <T extends Transaction> T appendCommonPropertiesAndBuildTransaction(
            final TransactionFactory<T> factory, final JsonObject jsonObject) {
        final JsonObject transaction = jsonObject.getJsonObject("transaction");
        final NetworkType networkType = NetworkType.rawValueOf(transaction.getInteger("network"));
        final TransactionInfo transactionInfo =
                this.createTransactionInfo(jsonObject.getJsonObject("meta"));
        final Deadline deadline = new Deadline(MapperUtils.toBigInteger(transaction, "deadline"));
        final Integer version = transaction.getInteger("version");
        final BigInteger maxFee = MapperUtils.toBigInteger(transaction, "maxFee");
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

    private <T extends Transaction> Transaction createTransaction(final JsonObject jsonObject) {
        final TransactionFactory<T> transactionFactory =
                resolveTransactionFactory(jsonObject).create(jsonObject);
        return appendCommonPropertiesAndBuildTransaction(transactionFactory, jsonObject);
    }

    /**
     * Converts from Json to a transaction.
     *
     * @param jsonObject Json object.
     * @return Transaction.
     */
    @Override
    public Transaction apply(final JsonObject jsonObject) {
        return createTransaction(jsonObject);
    }
}

abstract class TransactionMapperBase<T extends Transaction> {
    /* Transaction json. */
    JsonObject transaction;
    /* Network type. */
    NetworkType networkType;

    public abstract TransactionFactory<T> create(final JsonObject jsonObject);

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
}

/**
 * Transfer transaction mapper.
 */
class TransferTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to transfer transaction.
     *
     * @param jsonObject Json object.
     * @return Transfer transaction factory.
     */
    @Override
    public TransferTransactionFactory create(final JsonObject jsonObject) {
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
        final UnresolvedAddress unresolvedAddress =
                MapperUtils.toUnresolvedAddress(transaction, "recipientAddress");
        return TransferTransactionFactory.create(networkType, unresolvedAddress, mosaics, message);
    }
}

/**
 * Register namespace transaction mapper.
 */
class RegisterNamespaceTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to register namespace transaction.
     *
     * @param jsonObject Json object.
     * @return Register namespace transaction.
     */
    @Override
    public NamespaceRegistrationTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final NamespaceRegistrationType namespaceType =
                NamespaceRegistrationType.rawValueOf(transaction.getInteger("registrationType"));
        final String namespaceName =
                new String(Hex.decode(transaction.getString("name")), StandardCharsets.UTF_8);
        return namespaceType == NamespaceRegistrationType.ROOT_NAMESPACE
                ? NamespaceRegistrationTransactionFactory.createRootNamespace(
                networkType, namespaceName, MapperUtils.toBigInteger(transaction, "duration"))
                : NamespaceRegistrationTransactionFactory.createSubNamespace(
                networkType,
                namespaceName,
                NamespaceId.createFromId(MapperUtils.toBigInteger(transaction, "parentId")));
    }
}

/**
 * Mosaic definition transaction mapper.
 */
class MosaicDefinitionTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to mosaic definition transaction.
     *
     * @param jsonObject Json object.
     * @return Mosaic definition transaction.
     */
    @Override
    public MosaicDefinitionTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final int flags = transaction.getLong("flags").intValue();
        final MosaicFlags mosaicFlags = MosaicFlags.create(flags);
        final int divisibility = transaction.getInteger("divisibility");
        final Long duration = transaction.getLong("duration");

        return MosaicDefinitionTransactionFactory.create(
                networkType,
                MosaicNonce.createFromBigInteger(MapperUtils.toBigInteger(transaction, "nonce")),
                MapperUtils.toMosaicId(transaction, "id"),
                mosaicFlags,
                divisibility,
                new BlockDuration(duration));
    }
}

/**
 * Mosaic supply change transaction mapper.
 */
class MosaicSupplyChangeTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to mosaic supply change transaction.
     *
     * @param jsonObject Json object.
     * @return Mosaic supply change transaction.
     */
    @Override
    public MosaicSupplyChangeTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        return MosaicSupplyChangeTransactionFactory.create(
                networkType,
                MapperUtils.toMosaicId(transaction, "mosaicId"),
                MosaicSupplyChangeActionType.rawValueOf(transaction.getInteger("action")),
                MapperUtils.toBigInteger(transaction, "delta"));
    }
}

/**
 * Multisig modification transaction mapper.
 */
class MultisigModificationTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to multisig modification transaction.
     *
     * @param jsonObject Json object.
     * @return Multisig modification transaction.
     */
    @Override
    public MultisigAccountModificationTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final List<UnresolvedAddress> publicKeyAdditions =
                getAddressList(transaction, "addressAdditions");
        final List<UnresolvedAddress> publicKeyDeletions =
                getAddressList(transaction, "addressDeletions");
        return MultisigAccountModificationTransactionFactory.create(
                networkType,
                transaction.getInteger("minApprovalDelta").byteValue(),
                transaction.getInteger("minRemovalDelta").byteValue(),
                publicKeyAdditions,
                publicKeyDeletions);
    }

    private List<UnresolvedAddress> getAddressList(final JsonObject jsonObject, final String name) {
        return jsonObject.getJsonArray(name).stream()
                .map(item -> Address.createFromEncoded((String) item))
                .collect(Collectors.toList());
    }
}

/**
 * Aggregate transaction mapper.
 */
class AggregateTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to aggregate transaction.
     *
     * @param jsonObject Json object.
     * @return Aggregate transaction.
     */
    @Override
    public AggregateTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
        if (transaction.getJsonArray("cosignatures") != null) {
            cosignatures =
                    transaction.getJsonArray("cosignatures").stream()
                            .map(item -> (JsonObject) item)
                            .map(
                                    aggregateCosignature ->
                                            new AggregateTransactionCosignature(
                                                    BigInteger.ZERO,
                                                    aggregateCosignature.getString("signature"),
                                                    new PublicAccount(
                                                            aggregateCosignature.getString("signerPublicKey"), networkType)))
                            .collect(Collectors.toList());
        }
        final TransactionType type =
                TransactionType.rawValueOf(transaction.getInteger("type").shortValue());
        return AggregateTransactionFactory.create(
                TransactionType.rawValueOf(transaction.getInteger("type").shortValue()),
                networkType,
                new ArrayList<>(),
                cosignatures);
    }
}

/**
 * Lock funds transaction mapper.
 */
class HashLockTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to lock funds transaction.
     *
     * @param jsonObject Json object.
     * @return Lock funds transaction.
     */
    @Override
    public HashLockTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final MosaicId mosaicId = MapperUtils.toMosaicId(transaction, "mosaicId");
        final BigInteger amount = BigInteger.valueOf(transaction.getInteger("amount"));
        return HashLockTransactionFactory.create(
                networkType,
                new Mosaic(mosaicId, amount),
                MapperUtils.toBigInteger(transaction, "duration"),
                transaction.getString("hash"));
    }
}

/**
 * Secret lock transaction mapper.
 */
class SecretLockTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to secret lock transaction.
     *
     * @param jsonObject Json object.
     * @return Secret lock transaction.
     */
    @Override
    public SecretLockTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        return SecretLockTransactionFactory.create(
                networkType,
                new MosaicMapper("mosaicId").apply(transaction),
                MapperUtils.toBigInteger(transaction, "duration"),
                LockHashAlgorithmType.rawValueOf(transaction.getInteger("hashAlgorithm")),
                transaction.getString("secret"),
                Address.createFromEncoded(transaction.getString("recipientAddress")));
    }
}

/**
 * Secret proof transaction mapper.
 */
class SecretProofTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to secret proof transaction.
     *
     * @param jsonObject Json object.
     * @return Secret proof transaction.
     */
    @Override
    public SecretProofTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        return SecretProofTransactionFactory.create(
                networkType,
                LockHashAlgorithmType.rawValueOf(transaction.getInteger("hashAlgorithm")),
                Address.createFromEncoded(transaction.getString("recipientAddress")),
                transaction.getString("secret"),
                transaction.getString("proof"));
    }
}

/**
 * Mosaic alias transaction mapper.
 */
class MosaicAliasTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to mosaic alias transaction.
     *
     * @param jsonObject Json object.
     * @return Mosaic alias transaction.
     */
    @Override
    public MosaicAliasTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        return MosaicAliasTransactionFactory.create(
                networkType,
                AliasAction.rawValueOf(transaction.getInteger("aliasAction").byteValue()),
                NamespaceId.createFromId(MapperUtils.toBigInteger(transaction, "namespaceId")),
                MapperUtils.toMosaicId(transaction, "mosaicId"));
    }
}

/**
 * Address alias transaction mapper.
 */
class AddressAliasTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to Address alias transaction.
     *
     * @param jsonObject Json object.
     * @return Address alias transaction.
     */
    @Override
    public AddressAliasTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        return AddressAliasTransactionFactory.create(
                networkType,
                AliasAction.rawValueOf(transaction.getInteger("aliasAction").byteValue()),
                NamespaceId.createFromId(MapperUtils.toBigInteger(transaction, "namespaceId")),
                Address.createFromEncoded(transaction.getString("address")));
    }
}

/**
 * Account mosaic restriction modification alias transaction mapper.
 */
class AccountMosaicRestrictionModificationTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to Account mosaic restriction modification alias transaction.
     *
     * @param jsonObject Json object.
     * @return Address alias transaction.
     */
    @Override
    public AccountMosaicRestrictionTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final List<UnresolvedMosaicId> restrictionAdditions =
                getMosaicIdList(transaction, "restrictionAdditions");
        final List<UnresolvedMosaicId> restrictionDeletions =
                getMosaicIdList(transaction, "restrictionDeletions");
        return AccountMosaicRestrictionTransactionFactory.create(
                networkType,
                AccountMosaicRestrictionFlags.rawValueOf(transaction.getInteger("restrictionFlags")),
                restrictionAdditions,
                restrictionDeletions);
    }

    private List<UnresolvedMosaicId> getMosaicIdList(final JsonObject jsonObject, final String name) {
        return jsonObject.getJsonArray(name).stream()
                .map(item -> io.nem.symbol.core.utils.MapperUtils.toUnresolvedMosaicId((String) item))
                .collect(Collectors.toList());
    }
}

/**
 * Account address restriction modification alias transaction mapper.
 */
class AccountAddressRestrictionModificationTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to Account address restriction modification alias transaction.
     *
     * @param jsonObject Json object.
     * @return Address alias transaction.
     */
    @Override
    public AccountAddressRestrictionTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final List<UnresolvedAddress> restrictionAdditions =
                getAddressList(transaction, "restrictionAdditions");
        final List<UnresolvedAddress> restrictionDeletions =
                getAddressList(transaction, "restrictionDeletions");
        return AccountAddressRestrictionTransactionFactory.create(
                networkType,
                AccountAddressRestrictionFlags.rawValueOf(transaction.getInteger("restrictionFlags")),
                restrictionAdditions,
                restrictionDeletions);
    }

    private List<UnresolvedAddress> getAddressList(final JsonObject jsonObject, final String name) {
        return jsonObject.getJsonArray(name).stream()
                .map(item -> io.nem.symbol.core.utils.MapperUtils.toUnresolvedAddress((String) item))
                .collect(Collectors.toList());
    }
}

/**
 * Account transaction type restriction modification alias transaction mapper.
 */
class AccountOperationRestrictionModificationTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to Account transaction type restriction modification alias transaction.
     *
     * @param jsonObject Json object.
     * @return Address alias transaction.
     */
    @Override
    public AccountOperationRestrictionTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final List<TransactionType> restrictionAdditions =
                getTransactionTypeList(transaction, "restrictionAdditions");
        final List<TransactionType> restrictionDeletions =
                getTransactionTypeList(transaction, "restrictionDeletions");
        return AccountOperationRestrictionTransactionFactory.create(
                networkType,
                AccountOperationRestrictionFlags.rawValueOf(transaction.getInteger("restrictionFlags")),
                restrictionAdditions,
                restrictionDeletions);
    }

    private List<TransactionType> getTransactionTypeList(
            final JsonObject jsonObject, final String name) {
        return jsonObject.getJsonArray(name).stream()
                .map(
                        item -> {
                            final Short typeValue = Short.parseShort((String) item, 16);
                            return TransactionType.rawValueOf(Short.reverseBytes(typeValue));
                        })
                .collect(Collectors.toList());
    }
}

/**
 * Mosaic global restriction transaction mapper.
 */
class MosaicGlobalRestrictionTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to mosaic global restriction transaction.
     *
     * @param jsonObject Json object.
     * @return Mosaic global restriction transaction.
     */
    @Override
    public MosaicGlobalRestrictionTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        return MosaicGlobalRestrictionTransactionFactory.create(
                networkType,
                MapperUtils.toMosaicId(transaction, "mosaicId"),
                MapperUtils.toBigInteger(transaction, "restrictionKey"),
                MapperUtils.toBigInteger(transaction, "newRestrictionValue"),
                MosaicRestrictionType.rawValueOf(
                        transaction.getInteger("newRestrictionType").byteValue()))
                .referenceMosaicId(MapperUtils.toMosaicId(transaction, "referenceMosaicId"))
                .previousRestrictionType(
                        MosaicRestrictionType.rawValueOf(
                                transaction.getInteger("previousRestrictionType").byteValue()))
                .previousRestrictionValue(
                        MapperUtils.toBigInteger(transaction, "previousRestrictionValue"));
    }
}

/**
 * Mosaic global restriction transaction mapper.
 */
class MosaicAddressRestrictionTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to mosaic global restriction transaction.
     *
     * @param jsonObject Json object.
     * @return Mosaic global restriction transaction.
     */
    @Override
    public MosaicAddressRestrictionTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        return MosaicAddressRestrictionTransactionFactory.create(
                networkType,
                MapperUtils.toMosaicId(transaction, "mosaicId"),
                MapperUtils.toBigInteger(transaction, "restrictionKey"),
                MapperUtils.toUnresolvedAddress(transaction, "targetAddress"),
                MapperUtils.toBigInteger(transaction, "newRestrictionValue"))
                .previousRestrictionValue(
                        MapperUtils.toBigInteger(transaction, "previousRestrictionValue"));
    }
}

/**
 * Account metadata transaction mapper.
 */
class AccountMetadataTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to Account metadata transaction.
     *
     * @param jsonObject Json object.ƒ
     * @return Account metadata transaction.
     */
    @Override
    public MetadataTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final String value =
                new String(Hex.decode(transaction.getString("value")), StandardCharsets.UTF_8);
        return AccountMetadataTransactionFactory.create(
                networkType,
                MapperUtils.toUnresolvedAddress(transaction, "targetAddress"),
                MapperUtils.toBigInteger(transaction, "scopedMetadataKey"),
                value)
                .valueSizeDelta(transaction.getInteger("valueSizeDelta"))
                .valueSize(transaction.getInteger("valueSize"));
    }
}

/**
 * Account link transaction mapper.
 */
class AccountKeyLinkTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to account link transaction.
     *
     * @param jsonObject Json object.
     * @return Account link transaction.
     */
    @Override
    public AccountKeyLinkTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final PublicKey linkedPublicKey =
                PublicKey.fromHexString(transaction.getString("linkedPublicKey"));
        return AccountKeyLinkTransactionFactory.create(
                networkType,
                linkedPublicKey,
                LinkAction.rawValueOf(transaction.getInteger("linkAction").byteValue()));
    }
}

/**
 * Node Key link transaction mapper.
 */
class NodeKeyLinkTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to Node key link transaction.
     *
     * @param jsonObject Json object.
     * @return Node key link transaction.
     */
    @Override
    public NodeKeyLinkTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);

        final PublicKey linkedPublicKey =
                PublicKey.fromHexString(transaction.getString("linkedPublicKey"));
        return NodeKeyLinkTransactionFactory.create(
                networkType,
                linkedPublicKey,
                LinkAction.rawValueOf(transaction.getInteger("linkAction").byteValue()));
    }
}

/**
 * Vrf link transaction mapper.
 */
class VrfLinkTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to Vrf link transaction.
     *
     * @param jsonObject Json object.
     * @return Vrf link transaction.
     */
    @Override
    public VrfKeyLinkTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final PublicKey linkedPublicKey =
                PublicKey.fromHexString(transaction.getString("linkedPublicKey"));
        return VrfKeyLinkTransactionFactory.create(
                networkType,
                linkedPublicKey,
                LinkAction.rawValueOf(transaction.getInteger("linkAction").byteValue()));
    }
}

/**
 * Voting Key link transaction mapper.
 */
class VotingKeyLinkTransactionMapper extends TransactionMapperBase {
    /**
     * Converts from json to Voting key link transaction.
     *
     * @param jsonObject Json object.
     * @return Voting key link transaction.
     */
    @Override
    public VotingKeyLinkTransactionFactory create(final JsonObject jsonObject) {
        extractCommonProperties(jsonObject);
        final VotingKey votingKey = new VotingKey(transaction.getString("linkedPublicKey"));
        final BigInteger startPoint = MapperUtils.toBigInteger(transaction, "startPoint");
        final BigInteger endPoint = MapperUtils.toBigInteger(transaction, "endPoint");
        return VotingKeyLinkTransactionFactory.create(
                networkType,
                votingKey,
                startPoint,
                endPoint,
                LinkAction.rawValueOf(transaction.getInteger("linkAction").byteValue()));
    }
}
