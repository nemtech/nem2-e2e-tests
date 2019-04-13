package io.nem.sdk.model.account;

import java.math.BigInteger;

public class Importances {
    private BigInteger value;
    private BigInteger height;

    public Importances(BigInteger value, BigInteger height)
    {
        this.value = value;
        this.height = height;
    }

    public BigInteger getHeight() {
        return height;
    }

    public BigInteger getValue() {
        return value;
    }
}
