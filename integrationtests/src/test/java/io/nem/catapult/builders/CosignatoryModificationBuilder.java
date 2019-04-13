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

public class CosignatoryModificationBuilder {
    public CosignatoryModificationTypeBuilder getModificationtype()  {
        return this.modificationType;
    }

    public void setModificationtype(CosignatoryModificationTypeBuilder modificationType)  {
        this.modificationType = modificationType;
    }

    public ByteBuffer getCosignatorypublickey()  {
        return this.cosignatoryPublicKey;
    }

    public void setCosignatorypublickey(ByteBuffer cosignatoryPublicKey)  {
        if (cosignatoryPublicKey == null)
            throw new NullPointerException("cosignatoryPublicKey");
        
        if (cosignatoryPublicKey.array().length != 32)
            throw new IllegalArgumentException("cosignatoryPublicKey should be 32 bytes");
        
        this.cosignatoryPublicKey = cosignatoryPublicKey;
    }

    public static CosignatoryModificationBuilder loadFromBinary(DataInput stream) throws Exception {
        CosignatoryModificationBuilder obj = new CosignatoryModificationBuilder();
        obj.setModificationtype(CosignatoryModificationTypeBuilder.loadFromBinary(stream));
        obj.cosignatoryPublicKey = ByteBuffer.allocate(32);
        stream.readFully(obj.cosignatoryPublicKey.array());
        return obj;
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(bos);
        byte[] modificationType = this.getModificationtype().serialize();
        stream.write(modificationType, 0, modificationType.length);
        stream.write(this.cosignatoryPublicKey.array(), 0, this.cosignatoryPublicKey.array().length);
        stream.close();
        return bos.toByteArray();
    }

    private CosignatoryModificationTypeBuilder modificationType;
    private ByteBuffer cosignatoryPublicKey;

}
