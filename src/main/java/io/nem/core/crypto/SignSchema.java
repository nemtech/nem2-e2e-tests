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

package io.nem.core.crypto;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * This enum defines the strategies that can be used when signing and generating public addresses.
 */
public enum SignSchema {

    /**
     * SHA3 hash algorithm without key reversal
     */
    SHA3,
    /**
     * Keccak hash algorithm without key reversal
     */
    KECCAK;

    /**
     * It 32 bytes hashes the input according to the schema.
     *
     * @param signSchema the hashed input.
     * @param inputs the inputs
     * @return the hash
     */
    public static byte[] toHash32Bytes(SignSchema signSchema, byte[]... inputs) {
        return getHasher(signSchema, HashSize.HASH_SIZE_32_BYTES).hash(inputs);
    }

    /**
     * It 64 bytes hashes the input according to the schema.
     *
     * @param signSchema the hashed input.
     * @param inputs the schema
     * @return the hash
     */
    public static byte[] toHash64Bytes(SignSchema signSchema, byte[]... inputs) {
        return getHasher(signSchema, HashSize.HASH_SIZE_64_BYTES).hash(inputs);
    }

    /**
     * It 64 bytes hashes the private key reversing it if necessary.
     *
     * @param privateKey the privateKey
     * @param signSchema the hashed input.
     * @return the hash
     */
    public static byte[] toHash(PrivateKey privateKey, SignSchema signSchema) {
        byte[] secret = privateKey.getBytes();
        //Here is where you reverse the secret if necessary.
        return getHasher(signSchema, HashSize.HASH_SIZE_64_BYTES).hash(secret);
    }

    /**
     * Reversed conversion of an byte array;
     *
     * @param input the byte array.
     * @return the reverse of the input.
     */
    public static byte[] reverse(byte[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i += 1) {
            output[output.length - 1 - i] = input[i];
        }
        return output;
    }

    /**
     * Reversed conversion of hex hash;
     *
     * @param input the hex encoded hash.
     * @return the reverse of the input.
     */
    public static String reverse(String input) {
        try {
            return Hex.encodeHexString(reverse(Hex.decodeHex(input)));
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Decoder Exception " + ExceptionUtils.getMessage(e),
                e);
        }
    }


    /**
     * It resolved the righ hasher for the configuration.
     *
     * @param signSchema the {@link SignSchema}
     * @param hashSize the size of the hasher.
     * @return the hasher
     */
    public static Hasher getHasher(SignSchema signSchema, HashSize hashSize) {
        if (signSchema == SignSchema.SHA3 && hashSize == HashSize.HASH_SIZE_32_BYTES) {
            return Hashes::sha3_256;
        }
        if (signSchema == SignSchema.SHA3 && hashSize == HashSize.HASH_SIZE_64_BYTES) {
            return Hashes::sha3_512;
        }
        if (signSchema == SignSchema.KECCAK && hashSize == HashSize.HASH_SIZE_32_BYTES) {
            return Hashes::keccak256;
        }
        if (signSchema == SignSchema.KECCAK && hashSize == HashSize.HASH_SIZE_64_BYTES) {
            return Hashes::keccak512;
        }
        throw new IllegalStateException(
            "Unknown SignSchema " + signSchema + " and size " + hashSize);
    }

    /**
     * The allowed sizes when hashing byte arrays.
     */
    public enum HashSize {

        /**
         * 32 bytes / 256 bits hashes.
         */
        HASH_SIZE_32_BYTES,

        /**
         * 64 bytes / 512 bits hashes.
         */
        HASH_SIZE_64_BYTES;

    }

    /**
     * Function that hashes inputs according to the preconfigured schema.
     */
    @FunctionalInterface
    public interface Hasher {

        byte[] hash(byte[]... inputs);
    }

}
