
package io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.mappers;

import io.nem.symbol.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.ChainStatisticInfo;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.function.Function;

/** Chain info mapper. */
public class ChainStatisticInfoMapper implements Function<JsonObject, ChainStatisticInfo> {
  /**
   * Converts a json object to block info.
   *
   * @param jsonObject Json object.
   * @return Chain info.
   */
  public ChainStatisticInfo apply(final JsonObject jsonObject) {
    final JsonObject chainStatisticJsonObject = jsonObject.getJsonObject("current");
    final BigInteger height = MapperUtils.toBigInteger(chainStatisticJsonObject, "height");
    final BigInteger scoreHigh =
        MapperUtils.toBigInteger(chainStatisticJsonObject, "scoreHigh");
    final BigInteger scoreLow = MapperUtils.toBigInteger(chainStatisticJsonObject, "scoreLow");
    return ChainStatisticInfo.create(height, scoreHigh, scoreLow);
  }
}
