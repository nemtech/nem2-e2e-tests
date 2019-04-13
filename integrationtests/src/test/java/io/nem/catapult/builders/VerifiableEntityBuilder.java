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


package catapult.builders;

import java.lang.*;
import java.io.*;
import java.nio.*;
import catapult.builders.*;

public class VerifiableEntityBuilder {
    public ByteBuffer getSignature()  {
        return this.signature;
    }

    public void setSignature(ByteBuffer signature)  {
        if (signature == null)
            throw new NullPointerException("signature");
        
        if (signature.array().length != 64)
            throw new IllegalArgumentException("signature should be 64 bytes");
        
        this.signature = signature;
    }

    public static VerifiableEntityBuilder loadFromBinary(DataInput stream) throws Exception {
        VerifiableEntityBuilder obj = new VerifiableEntityBuilder();
        obj.signature = ByteBuffer.allocate(64);
        stream.readFully(obj.signature.array());
        return obj;
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(bos);
        stream.write(this.signature.array(), 0, this.signature.array().length);
        stream.close();
        return bos.toByteArray();
    }

    private ByteBuffer signature;

}
