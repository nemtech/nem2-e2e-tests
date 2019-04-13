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
 * Possible results of a verification handshake with a peer.
 */
public enum VerifyResult  {
	/** The peer was verified. */
	SUCCESS(0),

	/** An i/o error was encountered during verification. */
	IO_ERROR(1),

	/** The peer sent malformed data. */
	MALFORMED_DATA(2),

	/** The peer failed the challenge. */
	FAILED_CHALLENGE(3);

	private final int value;

	VerifyResult(int value)  {
		this.value = value;
	}
}
