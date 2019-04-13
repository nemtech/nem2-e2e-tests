/**
*** Copyright (c) 2016-present,
*** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
***
*** This file is part of Catapult.
***
*** Catapult is free software: you can redistribute it and/or modify
*** it under the terms of the GNU Lesser General Public License as published by
*** the Free Software Foundation, either version 3 of the License, or
*** (at your option) any later version.
***
*** Catapult is distributed in the hope that it will be useful,
*** but WITHOUT ANY WARRANTY; without even the implied warranty of
*** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*** GNU Lesser General Public License for more details.
***
*** You should have received a copy of the GNU Lesser General Public License
*** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
**/


package io.nem.catapult.builders;

import java.lang.*;
import java.io.*;
import java.nio.*;
import io.nem.catapult.builders.*;

public enum EntityTypeBuilder {
    RESERVED((short)0),
    TRANSFER_TRANSACTION_BUILDER((short)16724);

    private final short value;

    private  EntityTypeBuilder(short value)  {
        this.value = value;
    }

    public static EntityTypeBuilder loadFromBinary(DataInput stream) throws Exception {
        short val = stream.readShort();
        val = Short.reverseBytes(val);
        for (EntityTypeBuilder current : EntityTypeBuilder.values()) {
            if (val == current.value)
                return current;
        }
        throw new RuntimeException(val + " was not a backing value for EntityTypeBuilder.");
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(bos);
        stream.writeShort(Short.reverseBytes(this.value));
        stream.close();
        return bos.toByteArray();
    }

}
