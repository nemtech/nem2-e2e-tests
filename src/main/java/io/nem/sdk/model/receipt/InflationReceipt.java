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

package io.nem.sdk.model.receipt;

import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;

public class InflationReceipt extends Receipt {

    private final MosaicId mosaicId;
    private final BigInteger amount;

    /**
     * Constructor InflationReceipt
     *
     * @param mosaicId Mosaic Id
     * @param amount Amount
     * @param type Receipt Type
     * @param version Receipt Version
     * @param size Receipt Size
     */
    public InflationReceipt(
        MosaicId mosaicId,
        BigInteger amount,
        ReceiptType type,
        ReceiptVersion version,
        Optional<Integer> size) {
        super(type, version, size);
        this.amount = amount;
        this.mosaicId = mosaicId;
        this.validateReceiptType(type);
    }

    /**
     * Constructor InflationReceipt
     *
     * @param mosaicId Mosaic Id
     * @param amount Amount
     * @param type Receipt Type
     * @param version Receipt Version
     */
    public InflationReceipt(
        MosaicId mosaicId, BigInteger amount, ReceiptType type, ReceiptVersion version) {
        super(type, version, Optional.empty());
        this.amount = amount;
        this.mosaicId = mosaicId;
        this.validateReceiptType(type);
    }

    /**
     * Returns mosaicId
     *
     * @return account
     */
    public MosaicId getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Returns balance change amount
     *
     * @return balance change amount
     */
    public BigInteger getAmount() {
        return this.amount;
    }

    /**
     * Serialize receipt and returns receipt bytes
     *
     * @return receipt bytes
     */
    public byte[] serialize() {
        final ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putShort(Short.reverseBytes((short) getVersion().getValue()));
        buffer.putShort(Short.reverseBytes((short) getType().getValue()));
        buffer.putLong(Long.reverseBytes(getMosaicId().getIdAsLong()));
        buffer.putLong(Long.reverseBytes(getAmount().longValue()));
        return buffer.array();
    }

    /**
     * Validate receipt type
     *
     * @return void
     */
    private void validateReceiptType(ReceiptType type) {
        if (type != ReceiptType.INFLATION) {
            throw new IllegalArgumentException("Receipt type: [" + type.name() + "] is not valid.");
        }
    }
}
