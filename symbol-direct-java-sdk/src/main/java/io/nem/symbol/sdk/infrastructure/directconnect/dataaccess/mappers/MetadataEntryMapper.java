package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers;

import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataEntry;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.vertx.core.json.JsonObject;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

public class MetadataEntryMapper implements Function<JsonObject, Metadata> {
  /**
   * Converts a json object to block info.
   *
   * @param jsonObject Json object.
   * @return Chain info.
   */
  public Metadata apply(final JsonObject jsonObject) {
    final JsonObject metadataEntryJsonObject = jsonObject.getJsonObject("metadataEntry");
    final String compositeHash = metadataEntryJsonObject.getString("compositeHash");
    final Address sourceAddress = MapperUtils.toAddress(metadataEntryJsonObject, "sourceAddress");
    final Address targetAddress = MapperUtils.toAddress(metadataEntryJsonObject, "targetAddress");
    final String value =
        new String(Hex.decode(metadataEntryJsonObject.getString("value")), StandardCharsets.UTF_8);
    final BigInteger scopedMetadataKey =
        MapperUtils.toBigInteger(metadataEntryJsonObject, "scopedMetadataKey");
    final String targetIdHex =
        ConvertUtils.toHex(ByteUtils.longToBytes(metadataEntryJsonObject.getLong("targetId")));
    final MetadataType metadataType =
        MetadataType.rawValueOf(metadataEntryJsonObject.getInteger("metadataType"));
    final int valueSize = metadataEntryJsonObject.getInteger("valueSize");
    final MetadataEntry metadataEntry =
        new MetadataEntry(
            compositeHash,
            sourceAddress,
            targetAddress,
            scopedMetadataKey,
            metadataType,
            value,
            Optional.of(targetIdHex));
    return new Metadata("", metadataEntry);
  }
}
