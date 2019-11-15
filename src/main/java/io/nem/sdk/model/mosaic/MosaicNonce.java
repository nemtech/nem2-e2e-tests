package io.nem.sdk.model.mosaic;

import io.nem.core.utils.ByteUtils;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Mosaic nonce class
 */
public class MosaicNonce {

    /**
     * Mosaic nonce
     */
    private final byte[] nonce;

    /**
     * Create MosaicNonce from byte array
     */
    public MosaicNonce(byte[] nonce) {
        this.nonce = nonce;
    }

    /**
     * Create a random MosaicNonce
     *
     * @return MosaicNonce nonce
     */
    public static MosaicNonce createRandom() {
        final byte NO_OF_RANDOM_BYTES = 4;
        byte[] bytes = new byte[NO_OF_RANDOM_BYTES]; // the array to be filled in with random bytes
        try {
            new SecureRandom().nextBytes(bytes);
        } catch (Exception e) {
            throw new IllegalIdentifierException(ExceptionUtils.getMessage(e), e);
        }
        return new MosaicNonce(bytes);
    }

    /**
     * Create a MosaicNonce from hexadecimal notation.
     *
     * @return MosaicNonce
     */
    public static MosaicNonce createFromHex(String hex) {
        try {
            final byte[] bytes = Hex.decodeHex(hex);
            if (bytes.length != 4) {
                throw new IllegalIdentifierException(
                    "Expected 4 bytes for Nonce but got " + bytes.length + " instead.");
            }
            return new MosaicNonce(bytes);
        } catch (DecoderException e) {
            throw new IllegalIdentifierException(ExceptionUtils.getMessage(e), e);
        }
    }

    /**
     * Create a MosaicNonce from a BigInteger.
     *
     * @return MosaicNonce
     */
    public static MosaicNonce createFromBigInteger(BigInteger number) {
        return new MosaicNonce(ByteUtils.bigIntToBytesOfSize(number, 4));
    }

    /**
     * Create a MosaicNonce from a BigInteger.
     *
     * @return MosaicNonce
     */
    public static MosaicNonce createFromBigInteger(Long number) {
        return new MosaicNonce(ByteUtils.bigIntToBytesOfSize(BigInteger.valueOf(number), 4));
    }

    /**
     * @return nonce
     */
    public byte[] getNonce() {
        return nonce;
    }

    /**
     * @return nonce int
     */
    public int getNonceAsInt() {
        return ByteUtils.bytesToInt(this.nonce);
    }
}
