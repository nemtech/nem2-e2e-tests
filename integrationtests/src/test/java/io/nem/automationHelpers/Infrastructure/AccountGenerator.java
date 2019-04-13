package io.nem.automationHelpers.Infrastructure;

import io.nem.core.crypto.KeyPair;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;

public class AccountGenerator {
    private AccountGenerator() {}

    public static Account Create(NetworkType networkType) {
        return new Account(new KeyPair(), networkType);
    }
}
