/*
 * Copyright (c) 2016-present,
 * Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 *
 * This file is part of Catapult.
 *
 * Catapult is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catapult is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Catapult.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.nem.automationHelpers.auth;

/**
 * Server challenge connection security mode.
 */
public enum ConnectionSecurityMode {
    /**
     * No security mode.
     */
    NONE((byte) 1),
    /**
     * Signed security mode.
     */
    SIGNED((byte) 2);

    private final byte mode;

    /**
     * Constructor.
     *
     * @param mode The security mode.
     */
    ConnectionSecurityMode(final byte mode) {
        this.mode = mode;
    }

    /**
     * Gets the security mode.
     *
     * @return The security mode.
     */
    public byte toByte() {
        return this.mode;
    }
}
