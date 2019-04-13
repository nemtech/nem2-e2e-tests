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

public class EmbeddedAccountLinkTransactionBuilder {
    public int getSize()  {
        return this.size;
    }

    public void setSize(int size)  {
        this.size = size;
    }

    public ByteBuffer getSigner()  {
        return this.signer;
    }

    public void setSigner(ByteBuffer signer)  {
        if (signer == null)
            throw new NullPointerException("signer");
        
        if (signer.array().length != 32)
            throw new IllegalArgumentException("signer should be 32 bytes");
        
        this.signer = signer;
    }

    public short getVersion()  {
        return this.version;
    }

    public void setVersion(short version)  {
        this.version = version;
    }

    public EntityTypeBuilder getType()  {
        return this.type;
    }

    public void setType(EntityTypeBuilder type)  {
        this.type = type;
    }

    public ByteBuffer getRemoteaccountkey()  {
        return this.remoteAccountKey;
    }

    public void setRemoteaccountkey(ByteBuffer remoteAccountKey)  {
        if (remoteAccountKey == null)
            throw new NullPointerException("remoteAccountKey");
        
        if (remoteAccountKey.array().length != 32)
            throw new IllegalArgumentException("remoteAccountKey should be 32 bytes");
        
        this.remoteAccountKey = remoteAccountKey;
    }

    public AccountLinkActionBuilder getLinkaction()  {
        return this.linkAction;
    }

    public void setLinkaction(AccountLinkActionBuilder linkAction)  {
        this.linkAction = linkAction;
    }

    public static EmbeddedAccountLinkTransactionBuilder loadFromBinary(DataInput stream) throws Exception {
        EmbeddedAccountLinkTransactionBuilder obj = new EmbeddedAccountLinkTransactionBuilder();
        obj.setSize(Integer.reverseBytes(stream.readInt()));
        obj.signer = ByteBuffer.allocate(32);
        stream.readFully(obj.signer.array());
        obj.setVersion(Short.reverseBytes(stream.readShort()));
        obj.setType(EntityTypeBuilder.loadFromBinary(stream));
        obj.remoteAccountKey = ByteBuffer.allocate(32);
        stream.readFully(obj.remoteAccountKey.array());
        obj.setLinkaction(AccountLinkActionBuilder.loadFromBinary(stream));
        return obj;
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(bos);
        stream.writeInt(Integer.reverseBytes(this.getSize()));
        stream.write(this.signer.array(), 0, this.signer.array().length);
        stream.writeShort(Short.reverseBytes(this.getVersion()));
        byte[] type = this.getType().serialize();
        stream.write(type, 0, type.length);
        stream.write(this.remoteAccountKey.array(), 0, this.remoteAccountKey.array().length);
        byte[] linkAction = this.getLinkaction().serialize();
        stream.write(linkAction, 0, linkAction.length);
        stream.close();
        return bos.toByteArray();
    }

    private int size;
    private ByteBuffer signer;
    private short version;
    private EntityTypeBuilder type;
    private ByteBuffer remoteAccountKey;
    private AccountLinkActionBuilder linkAction;

}
