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

package io.nem.sdk.infrastructure;

import io.nem.catapult.builders.*;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.TransactionType;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.Validate;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.stream.Collectors;

/** Utility class used to serialize/deserialize catbuffer values. */
public class SerializationUtils {

  /** Private constructor. */
  private SerializationUtils() {}

  /**
   * It creates a PublicAccount from a {@link KeyDto}.
   *
   * @param keyDto catbuffer {@link KeyDto}.
   * @param networkType the network type
   * @return the model {@link PublicAccount}
   */
  public static PublicAccount toPublicAccount(KeyDto keyDto, NetworkType networkType) {
    return PublicAccount.createFromPublicKey(
        ConvertUtils.toHex(keyDto.getKey().array()), networkType);
  }

  /**
   * It creates a Mosaic from an {@link UnresolvedMosaicBuilder}.
   *
   * @param builder the catbuffer {@link UnresolvedMosaicBuilder}.
   * @return the model {@link Mosaic}
   */
  public static Mosaic toMosaic(UnresolvedMosaicBuilder builder) {
    return new Mosaic(
        new MosaicId(toUnsignedBigInteger(builder.getMosaicId().getUnresolvedMosaicId())),
        toUnsignedBigInteger(builder.getAmount().getAmount()));
  }

  /**
   * It creates a {@link MosaicId} from an {@link UnresolvedMosaicIdDto}.
   *
   * @param dto the catbuffer {@link UnresolvedMosaicIdDto}.
   * @return the model {@link MosaicId}
   */
  public static UnresolvedMosaicId toMosaicId(UnresolvedMosaicIdDto dto) {
    return new MosaicId(toUnsignedBigInteger(dto.getUnresolvedMosaicId()));
  }

  /**
   * It creates a {@link MosaicId} from an {@link MosaicIdDto}.
   *
   * @param dto the catbuffer {@link MosaicIdDto}.
   * @return the model {@link MosaicId}
   */
  public static MosaicId toMosaicId(MosaicIdDto dto) {
    return new MosaicId(toUnsignedBigInteger(dto.getMosaicId()));
  }

  /**
   * It creates a {@link NamespaceId} from an {@link NamespaceIdDto}.
   *
   * @param dto the catbuffer {@link NamespaceIdDto}.
   * @return the model {@link NamespaceId}
   */
  public static NamespaceId toNamespaceId(NamespaceIdDto dto) {
    return NamespaceId.createFromId(toUnsignedBigInteger(dto.getNamespaceId()));
  }

  /**
   * It creates a {@link Address} from an {@link UnresolvedAddressDto}.
   *
   * @param dto the catbuffer {@link UnresolvedAddressDto}.
   * @return the model {@link Address}
   */
  public static UnresolvedAddress toAddress(UnresolvedAddressDto dto) {
    return MapperUtils.toUnresolvedAddress(ConvertUtils.toHex(dto.getUnresolvedAddress().array()));
  }

  /**
   * It serializes a UnresolvedAddress to an hex catbuffer understand.
   *
   * @param unresolvedAddress the {@link Address} or {@link NamespaceId} to be serialized.
   * @param networkType the network type to customize the {@link NamespaceId} serialization
   * @return the serialized {@link UnresolvedAddress} as {@link ByteBuffer}.
   */
  public static ByteBuffer fromUnresolvedAddressToByteBuffer(
      UnresolvedAddress unresolvedAddress, NetworkType networkType) {
    Validate.notNull(unresolvedAddress, "unresolvedAddress must not be null");

    if (unresolvedAddress instanceof NamespaceId) {
      final ByteBuffer namespaceIdAlias = ByteBuffer.allocate(25);
      NamespaceId namespaceId = (NamespaceId) unresolvedAddress;
      final byte firstByte = (byte) (networkType.getValue() | 0x01);
      namespaceIdAlias.order(ByteOrder.LITTLE_ENDIAN);
      namespaceIdAlias.put(firstByte);
      namespaceIdAlias.putLong(namespaceId.getIdAsLong());
      return ByteBuffer.wrap(namespaceIdAlias.array());
    }

    if (unresolvedAddress instanceof Address) {
      return fromAddressToByteBuffer((Address) unresolvedAddress);
    }
    throw new IllegalArgumentException(
        "Unexpected UnresolvedAddress type " + unresolvedAddress.getClass());
  }

  public static ByteBuffer fromAddressToByteBuffer(final Address address) {
    return ByteBuffer.wrap(new Base32().decode((address.plain())));
  }

  /**
   * It creates a {@link Address} from an {@link AddressDto}.
   *
   * @param dto the catbuffer {@link AddressDto}.
   * @return the model {@link Address}
   */
  public static Address toAddress(AddressDto dto) {
    return Address.createFromEncoded(ConvertUtils.toHex(dto.getAddress().array()));
  }

  /**
   * It converts a signed byte to a positive integer.
   *
   * @param value the byte, it can be a overflowed negative byte.
   * @return the positive integer.
   */
  public static int byteToUnsignedInt(byte value) {
    return value & 0xFF;
  }

  /**
   * It converts a signed short to a positive integer.
   *
   * @param value the short, it can be a overflowed negative short.
   * @return the positive integer.
   */
  public static int shortToUnsignedInt(short value) {
    return value & 0xFFFF;
  }

  /**
   * It creates a {@link DataInput} from a binary payload.
   *
   * @param payload the payload
   * @return the {@link DataInput} catbuffer uses.
   */
  public static DataInputStream toDataInputStream(byte[] payload) {
    return new DataInputStream(new ByteArrayInputStream(payload));
  }

  /**
   * It converts an AmountDto into a positive {@link BigInteger}.
   *
   * @param amountDto the catbuffer {@link AmountDto}
   * @return the positive {@link BigInteger}.
   */
  public static BigInteger toUnsignedBigInteger(AmountDto amountDto) {
    return toUnsignedBigInteger(amountDto.getAmount());
  }

  /**
   * It converts a signed long to a positive integer.
   *
   * @param value the short, it can be a overflowed negative long.
   * @return the positive integer.
   */
  public static BigInteger toUnsignedBigInteger(long value) {
    return ConvertUtils.toUnsignedBigInteger(value);
  }

  /**
   * It extracts the hex string from the {@link Hash256Dto}
   *
   * @param dto the {@link Hash256Dto}
   * @return the hex string.
   */
  public static String toHexString(Hash256Dto dto) {
    return ConvertUtils.toHex(dto.getHash256().array());
  }

  /**
   * It extracts the hex string from the {@link SignatureDto}
   *
   * @param dto the {@link SignatureDto}
   * @return the hex string.
   */
  public static String toHexString(SignatureDto dto) {
    return ConvertUtils.toHex(dto.getSignature().array());
  }

  /**
   * It extracts an UTF-8 string from the {@link ByteBuffer}
   *
   * @param buffer the {@link ByteBuffer}
   * @return the UTF-8 string.
   */
  public static String toString(ByteBuffer buffer) {
    return StringEncoder.getString(buffer.array());
  }

  /**
   * It concats the 2 byte arrays patching the int size at the beginning of the first byte array
   * setting up the sum of both lengths.
   *
   * @param commonBytes the common transaction byte array
   * @param transactionBytes the specific transaction byte array.
   * @return the concated byte array.
   */
  public static byte[] concat(byte[] commonBytes, byte[] transactionBytes) {
    return GeneratorUtils.serialize(
        dataOutputStream -> {
          dataOutputStream.writeInt(
              Integer.reverseBytes(commonBytes.length + transactionBytes.length));
          dataOutputStream.write(commonBytes, 4, commonBytes.length - 4);
          dataOutputStream.write(transactionBytes);
        });
  }

  /**
   * It serializes the public key of a public account.
   *
   * @return the public account
   */
  public static ByteBuffer toByteBuffer(PublicAccount publicAccount) {
    final byte[] bytes = publicAccount.getPublicKey().getBytes();
    return ByteBuffer.wrap(bytes);
  }

  /**
   * It serializes the string signature into a SignatureDto catbuffer understands
   *
   * @param signature the signature string
   * @return SignatureDto.
   */
  public static SignatureDto toSignatureDto(String signature) {
    return new SignatureDto(ByteBuffer.wrap(ConvertUtils.getBytes(signature)));
  }

  /**
   * Convert a list of public accounts to list of KeyDto.
   *
   * @param publicAccounts List of public accounts.
   * @return List of KeyDto.
   */
  public static List<KeyDto> toKeyDtoList(final List<PublicAccount> publicAccounts) {
    return publicAccounts.stream()
        .map(p -> new KeyDto(toByteBuffer(p)))
        .collect(Collectors.toList());
  }

  /**
   * Convert list of KeyDto to public account.
   *
   * @param keyDtoList List of KeyDto.
   * @return List of KeyDto.
   */
  public static List<PublicAccount> toPublicAccountList(
      final List<KeyDto> keyDtoList, final NetworkType networkType) {
    return keyDtoList.stream()
        .map(keyDto -> toPublicAccount(keyDto, networkType))
        .collect(Collectors.toList());
  }

  /**
   * Convert list of UnresolvedAddressDto to UnresolvedAddress.
   *
   * @param unresolvedAddressDtoList List of UnresolvedAddressDto.
   * @return List of unresolvedAddress.
   */
  public static List<UnresolvedAddress> toUnresolvedAddressList(
      final List<UnresolvedAddressDto> unresolvedAddressDtoList) {
    return unresolvedAddressDtoList.stream()
        .map(SerializationUtils::toAddress)
        .collect(Collectors.toList());
  }

  /**
   * Convert list of UnresolvedAddress to UnresolvedAddressDto.
   *
   * @param unresolvedAddressList List of UnresolvedAddress.
   * @return List of unresolvedAddressDto.
   */
  public static List<UnresolvedAddressDto> toUnresolvedAddressDtoList(
      final List<UnresolvedAddress> unresolvedAddressList, final NetworkType networkType) {
    return unresolvedAddressList.stream()
        .map(
            unresolvedAddress ->
                new UnresolvedAddressDto(
                    SerializationUtils.fromUnresolvedAddressToByteBuffer(
                        unresolvedAddress, networkType)))
        .collect(Collectors.toList());
  }

  /**
   * Convert list of KeyDto to public account.
   *
   * @param UnresolvedMosaicIdDtoList List of UnresolvedMosaicIdDto.
   * @return List of UnresolvedMosaicId.
   */
  public static List<UnresolvedMosaicId> toUnresolvedMosaicIdList(
      final List<UnresolvedMosaicIdDto> UnresolvedMosaicIdDtoList) {
    return UnresolvedMosaicIdDtoList.stream()
        .map(SerializationUtils::toMosaicId)
        .collect(Collectors.toList());
  }

  /**
   * Convert list of UnresolvedAddress to UnresolvedAddressDto.
   *
   * @param unresolvedMosaicIdList List of UnresolvedAddress.
   * @return List of unresolvedAddressDto.
   */
  public static List<UnresolvedMosaicIdDto> toUnresolvedMosaicIdDtoList(
      final List<UnresolvedMosaicId> unresolvedMosaicIdList) {
    return unresolvedMosaicIdList.stream()
        .map(unresolvedMosaicId -> new UnresolvedMosaicIdDto(unresolvedMosaicId.getIdAsLong()))
        .collect(Collectors.toList());
  }

    /**
     * Convert list of KeyDto to public account.
     *
     * @param entityTypeDtoList List of UnresolvedMosaicIdDto.
     * @return List of UnresolvedMosaicId.
     */
    public static List<TransactionType> toTransactionTypeList(
            final List<EntityTypeDto> entityTypeDtoList) {
        return entityTypeDtoList.stream()
                .map(entityTypeDto -> TransactionType.rawValueOf(entityTypeDto.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Convert list of UnresolvedAddress to UnresolvedAddressDto.
     *
     * @param transactionTypeList List of transaction type.
     * @return List of unresolvedAddressDto.
     */
    public static List<EntityTypeDto> toEntityTypeDtoList(
            final List<TransactionType> transactionTypeList) {
        return transactionTypeList.stream()
                .map(transactionType -> EntityTypeDto.rawValueOf((short)transactionType.getValue()))
                .collect(Collectors.toList());
    }
}
