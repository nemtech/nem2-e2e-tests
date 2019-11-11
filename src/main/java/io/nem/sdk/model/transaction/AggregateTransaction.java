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

import io.nem.core.crypto.*;
import io.nem.sdk.infrastructure.BinarySerializationImpl;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import java.math.BigInteger;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;

/**
 * The aggregate innerTransactions contain multiple innerTransactions that can be initiated by
 * different accounts.
 *
 * @since 1.0
 */
public class AggregateTransaction extends Transaction {

    private final List<Transaction> innerTransactions;
    private final List<AggregateTransactionCosignature> cosignatures;
    private final String transactionsHash;

    /**
     * AggregateTransaction constructor using factory.
     */
    AggregateTransaction(AggregateTransactionFactory factory) {
        super(factory);
        this.innerTransactions = factory.getInnerTransactions();
        this.cosignatures = factory.getCosignatures();
        this.transactionsHash = calculateTransactionsHash(this.innerTransactions);
    }

    /**
     * Returns list of innerTransactions included in the aggregate transaction.
     *
     * @return List of innerTransactions included in the aggregate transaction.
     */
    public List<Transaction> getInnerTransactions() {
        return innerTransactions;
    }

    /**
     * Returns list of transaction cosigners signatures.
     *
     * @return List of transaction cosigners signatures.
     */
    public List<AggregateTransactionCosignature> getCosignatures() {
        return cosignatures;
    }

    /**
     * Get the bytes required for signing.
     *
     * @param payloadBytes Payload bytes.
     * @param generationHashBytes Generation hash bytes.
     * @return Bytes to sign.
     */
    @Override
    protected byte[] getSignBytes(final byte[] payloadBytes, final byte[] generationHashBytes) {
        final short headerSize = 4 + 32 + 64 + 8;
        // Aggregate tx only require to sign the body.
        final short signingBytesSize = 52;
        final byte[] signingBytes = new byte[signingBytesSize + generationHashBytes.length];
        System.arraycopy(generationHashBytes, 0, signingBytes, 0, generationHashBytes.length);
        System.arraycopy(payloadBytes, headerSize, signingBytes, generationHashBytes.length, signingBytesSize);
        return signingBytes;
    }

    /**
     * Sign transaction with cosignatories creating a new SignedTransaction.
     *
     * @param initiatorAccount Initiator account
     * @param cosignatories The list of accounts that will cosign the transaction
     * @return {@link SignedTransaction}
     */
    public SignedTransaction signTransactionWithCosigners(
        final Account initiatorAccount,
        final List<Account> cosignatories,
        final String generationHash) {
        SignedTransaction signedTransaction = this.signWith(initiatorAccount, generationHash);
        StringBuilder payload = new StringBuilder(signedTransaction.getPayload());

        for (Account cosignatory : cosignatories) {
            final DsaSigner signer = CryptoEngines.defaultEngine()
                .createDsaSigner(cosignatory.getKeyPair(),
                    cosignatory.getNetworkType().resolveSignSchema());
            byte[] bytes = Hex.decode(signedTransaction.getHash());
            byte[] signatureBytes = signer.sign(bytes).getBytes();
            payload.append(cosignatory.getPublicKey()).append(Hex.toHexString(signatureBytes));
        }

        byte[] payloadBytes = Hex.decode(payload.toString());

        byte[] size = BigInteger.valueOf(payloadBytes.length).toByteArray();
        ArrayUtils.reverse(size);

        System.arraycopy(size, 0, payloadBytes, 0, size.length);

        return new SignedTransaction(
            Hex.toHexString(payloadBytes), signedTransaction.getHash(), getType());
    }

    /**
     * Check if account has signed transaction.
     *
     * @param publicAccount - Signer public account
     * @return boolean
     */
    public boolean signedByAccount(PublicAccount publicAccount) {
        return this.getSigner().filter(a -> a.equals(publicAccount)).isPresent()
            || this.getCosignatures().stream().anyMatch(o -> o.getSigner().equals(publicAccount));
    }

    /**
     * Gets the hash of the inner transaction.
     *
     * @return Hash of the inner transaction.
     */
    public String getTransactionsHash() {
        return transactionsHash;
    }

    private String calculateTransactionsHash(final List<Transaction> transactions) {
        final SignSchema.Hasher hasher = SignSchema.getHasher(SignSchema.SHA3, SignSchema.HashSize.HASH_SIZE_32_BYTES);
        final MerkleHashBuilder transactionsHashBuilder = new MerkleHashBuilder(hasher, transactions.size());
        final BinarySerializationImpl transactionSerialization = new BinarySerializationImpl();

        for (final Transaction transaction : transactions) {
            final byte[] bytes = transactionSerialization.serializeEmbedded(transaction);
            byte[] transactionHash = Hashes.sha3_256(bytes);
            transactionsHashBuilder.update(transactionHash);
        }

        final byte[] hash = transactionsHashBuilder.getRootHash();
        return Hex.toHexString(hash).toUpperCase();
    }
}
