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

public enum MosaicFlagsBuilder {
    NONE((byte)0),
    SUPPLYMUTABLE((byte)1),
    TRANSFERABLE((byte)2),
    LEVYMUTABLE((byte)4);

    private final byte value;

    private  MosaicFlagsBuilder(byte value)  {
        this.value = value;
    }

    public static MosaicFlagsBuilder loadFromBinary(DataInput stream) throws Exception {
        byte val = stream.readByte();
        val = val;
        for (MosaicFlagsBuilder current : MosaicFlagsBuilder.values()) {
            if (val == current.value)
                return current;
        }
        throw new RuntimeException(val + " was not a backing value for MosaicFlagsBuilder.");
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(bos);
        stream.writeByte(this.value);
        stream.close();
        return bos.toByteArray();
    }

}
