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

public class RegisterNamespaceTransactionBuilder {
    public int getSize()  {
        return this.size;
    }

    public void setSize(int size)  {
        this.size = size;
    }

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

    public long getFee()  {
        return this.fee;
    }

    public void setFee(long fee)  {
        this.fee = fee;
    }

    public long getDeadline()  {
        return this.deadline;
    }

    public void setDeadline(long deadline)  {
        this.deadline = deadline;
    }

    public NamespaceTypeBuilder getNamespacetype()  {
        return this.namespaceType;
    }

    public void setNamespacetype(NamespaceTypeBuilder namespaceType)  {
        this.namespaceType = namespaceType;
    }

    public long getDuration()  {
        if (namespaceType != NamespaceTypeBuilder.ROOT)
            throw new java.lang.IllegalStateException();
        
        return this.duration;
    }

    public void setDuration(long duration)  {
        if (namespaceType != NamespaceTypeBuilder.ROOT)
            throw new java.lang.IllegalStateException();
        
        this.duration = duration;
    }

    public long getParentid()  {
        if (namespaceType != NamespaceTypeBuilder.CHILD)
            throw new java.lang.IllegalStateException();
        
        return this.parentId;
    }

    public void setParentid(long parentId)  {
        if (namespaceType != NamespaceTypeBuilder.CHILD)
            throw new java.lang.IllegalStateException();
        
        this.parentId = parentId;
    }

    public long getNamespaceid()  {
        return this.namespaceId;
    }

    public void setNamespaceid(long namespaceId)  {
        this.namespaceId = namespaceId;
    }

    public ByteBuffer getName()  {
        return this.name;
    }

    public void setName(ByteBuffer name)  {
        if (name == null)
            throw new NullPointerException("name");
        
        
        this.name = name;
    }

    public static RegisterNamespaceTransactionBuilder loadFromBinary(DataInput stream) throws Exception {
        RegisterNamespaceTransactionBuilder obj = new RegisterNamespaceTransactionBuilder();
        obj.setSize(Integer.reverseBytes(stream.readInt()));
        obj.signature = ByteBuffer.allocate(64);
        stream.readFully(obj.signature.array());
        obj.signer = ByteBuffer.allocate(32);
        stream.readFully(obj.signer.array());
        obj.setVersion(Short.reverseBytes(stream.readShort()));
        obj.setType(EntityTypeBuilder.loadFromBinary(stream));
        obj.setFee(Long.reverseBytes(stream.readLong()));
        obj.setDeadline(Long.reverseBytes(stream.readLong()));
        obj.setNamespacetype(NamespaceTypeBuilder.loadFromBinary(stream));
        if (obj.getNamespacetype() == NamespaceTypeBuilder.ROOT)
            obj.setDuration(Long.reverseBytes(stream.readLong()));
        if (obj.getNamespacetype() == NamespaceTypeBuilder.CHILD)
            obj.setParentid(Long.reverseBytes(stream.readLong()));
        obj.setNamespaceid(Long.reverseBytes(stream.readLong()));
        byte namespaceNameSize = stream.readByte();
        obj.name = ByteBuffer.allocate(namespaceNameSize);
        stream.readFully(obj.name.array());
        return obj;
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(bos);
        stream.writeInt(Integer.reverseBytes(this.getSize()));
        stream.write(this.signature.array(), 0, this.signature.array().length);
        stream.write(this.signer.array(), 0, this.signer.array().length);
        stream.writeShort(Short.reverseBytes(this.getVersion()));
        byte[] type = this.getType().serialize();
        stream.write(type, 0, type.length);
        stream.writeLong(Long.reverseBytes(this.getFee()));
        stream.writeLong(Long.reverseBytes(this.getDeadline()));
        byte[] namespaceType = this.getNamespacetype().serialize();
        stream.write(namespaceType, 0, namespaceType.length);
        if (this.getNamespacetype() == NamespaceTypeBuilder.ROOT)
            stream.writeLong(Long.reverseBytes(this.getDuration()));
        if (this.getNamespacetype() == NamespaceTypeBuilder.CHILD)
            stream.writeLong(Long.reverseBytes(this.getParentid()));
        stream.writeLong(Long.reverseBytes(this.getNamespaceid()));
        stream.writeByte((byte)this.name.array().length);
        stream.write(this.name.array(), 0, this.name.array().length);
        stream.close();
        return bos.toByteArray();
    }

    private int size;
    private ByteBuffer signature;
    private ByteBuffer signer;
    private short version;
    private EntityTypeBuilder type;
    private long fee;
    private long deadline;
    private NamespaceTypeBuilder namespaceType;
    private long duration;
    private long parentId;
    private long namespaceId;
    private ByteBuffer name;

}
