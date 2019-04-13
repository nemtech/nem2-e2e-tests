package io.nem.automationHelpers.auth;

public enum ConnectionSecurityMode {
    NONE((byte)1),
    SIGNED((byte)2);

    private byte mode;

    ConnectionSecurityMode(byte mode) {
        this.mode = mode;
    }

    byte toByte() {
        return this.mode;
    }
}
