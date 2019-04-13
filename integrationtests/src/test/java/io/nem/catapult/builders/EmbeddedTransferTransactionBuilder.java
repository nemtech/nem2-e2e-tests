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

public class EmbeddedTransferTransactionBuilder {
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

    public ByteBuffer getRecipient()  {
        return this.recipient;
    }

    public void setRecipient(ByteBuffer recipient)  {
        if (recipient == null)
            throw new NullPointerException("recipient");
        
        if (recipient.array().length != 25)
            throw new IllegalArgumentException("recipient should be 25 bytes");
        
        this.recipient = recipient;
    }

    public ByteBuffer getMessage()  {
        return this.message;
    }

    public void setMessage(ByteBuffer message)  {
        if (message == null)
            throw new NullPointerException("message");
        
        
        this.message = message;
    }

    public java.util.ArrayList<UnresolvedMosaicBuilder> getMosaics()  {
        return (java.util.ArrayList<UnresolvedMosaicBuilder>)this.mosaics;
    }

    public void setMosaics(java.util.ArrayList<UnresolvedMosaicBuilder> mosaics)  {
        this.mosaics = mosaics;
    }

    public static EmbeddedTransferTransactionBuilder loadFromBinary(DataInput stream) throws Exception {
        EmbeddedTransferTransactionBuilder obj = new EmbeddedTransferTransactionBuilder();
        obj.setSize(Integer.reverseBytes(stream.readInt()));
        obj.signer = ByteBuffer.allocate(32);
        stream.readFully(obj.signer.array());
        obj.setVersion(Short.reverseBytes(stream.readShort()));
        obj.setType(EntityTypeBuilder.loadFromBinary(stream));
        obj.recipient = ByteBuffer.allocate(25);
        stream.readFully(obj.recipient.array());
        short messageSize = Short.reverseBytes(stream.readShort());
        byte mosaicsCount = stream.readByte();
        obj.message = ByteBuffer.allocate(messageSize);
        stream.readFully(obj.message.array());
        java.util.ArrayList<UnresolvedMosaicBuilder> mosaics = new java.util.ArrayList<UnresolvedMosaicBuilder>(mosaicsCount);
        for (int i = 0; i < mosaicsCount; i++) {
            mosaics.add(UnresolvedMosaicBuilder.loadFromBinary(stream));
        }
        obj.setMosaics(mosaics);
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
        stream.write(this.recipient.array(), 0, this.recipient.array().length);
        stream.writeShort(Short.reverseBytes((short)this.message.array().length));
        stream.writeByte((byte)this.mosaics.size());
        stream.write(this.message.array(), 0, this.message.array().length);
        for (int i = 0; i < this.mosaics.size(); i++) {
            byte[] ser = this.mosaics.get(i).serialize();
            stream.write(ser, 0, ser.length);
        }
        stream.close();
        return bos.toByteArray();
    }

    private int size;
    private ByteBuffer signer;
    private short version;
    private EntityTypeBuilder type;
    private ByteBuffer recipient;
    private ByteBuffer message;
    private java.util.ArrayList<UnresolvedMosaicBuilder> mosaics;

}
