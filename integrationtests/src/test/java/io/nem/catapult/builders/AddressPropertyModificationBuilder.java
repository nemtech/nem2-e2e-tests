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

public class AddressPropertyModificationBuilder {
    public PropertyModificationTypeBuilder getModificationtype()  {
        return this.modificationType;
    }

    public void setModificationtype(PropertyModificationTypeBuilder modificationType)  {
        this.modificationType = modificationType;
    }

    public ByteBuffer getValue()  {
        return this.value;
    }

    public void setValue(ByteBuffer value)  {
        if (value == null)
            throw new NullPointerException("value");
        
        if (value.array().length != 25)
            throw new IllegalArgumentException("value should be 25 bytes");
        
        this.value = value;
    }

    public static AddressPropertyModificationBuilder loadFromBinary(DataInput stream) throws Exception {
        AddressPropertyModificationBuilder obj = new AddressPropertyModificationBuilder();
        obj.setModificationtype(PropertyModificationTypeBuilder.loadFromBinary(stream));
        obj.value = ByteBuffer.allocate(25);
        stream.readFully(obj.value.array());
        return obj;
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(bos);
        byte[] modificationType = this.getModificationtype().serialize();
        stream.write(modificationType, 0, modificationType.length);
        stream.write(this.value.array(), 0, this.value.array().length);
        stream.close();
        return bos.toByteArray();
    }

    private PropertyModificationTypeBuilder modificationType;
    private ByteBuffer value;

}
