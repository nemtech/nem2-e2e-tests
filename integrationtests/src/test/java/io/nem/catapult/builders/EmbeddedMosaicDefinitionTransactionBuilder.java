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

public class EmbeddedMosaicDefinitionTransactionBuilder {
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

    public int getMosaicnonce()  {
        return this.mosaicNonce;
    }

    public void setMosaicnonce(int mosaicNonce)  {
        this.mosaicNonce = mosaicNonce;
    }

    public long getMosaicid()  {
        return this.mosaicId;
    }

    public void setMosaicid(long mosaicId)  {
        this.mosaicId = mosaicId;
    }

    public MosaicFlagsBuilder getFlags()  {
        return this.flags;
    }

    public void setFlags(MosaicFlagsBuilder flags)  {
        this.flags = flags;
    }

    public byte getDivisibility()  {
        return this.divisibility;
    }

    public void setDivisibility(byte divisibility)  {
        this.divisibility = divisibility;
    }

    public java.util.ArrayList<MosaicPropertyBuilder> getProperties()  {
        return (java.util.ArrayList<MosaicPropertyBuilder>)this.properties;
    }

    public void setProperties(java.util.ArrayList<MosaicPropertyBuilder> properties)  {
        this.properties = properties;
    }

    public static EmbeddedMosaicDefinitionTransactionBuilder loadFromBinary(DataInput stream) throws Exception {
        EmbeddedMosaicDefinitionTransactionBuilder obj = new EmbeddedMosaicDefinitionTransactionBuilder();
        obj.setSize(Integer.reverseBytes(stream.readInt()));
        obj.signer = ByteBuffer.allocate(32);
        stream.readFully(obj.signer.array());
        obj.setVersion(Short.reverseBytes(stream.readShort()));
        obj.setType(EntityTypeBuilder.loadFromBinary(stream));
        obj.setMosaicnonce(Integer.reverseBytes(stream.readInt()));
        obj.setMosaicid(Long.reverseBytes(stream.readLong()));
        byte propertiesCount = stream.readByte();
        obj.setFlags(MosaicFlagsBuilder.loadFromBinary(stream));
        obj.setDivisibility(stream.readByte());
        java.util.ArrayList<MosaicPropertyBuilder> properties = new java.util.ArrayList<MosaicPropertyBuilder>(propertiesCount);
        for (int i = 0; i < propertiesCount; i++) {
            properties.add(MosaicPropertyBuilder.loadFromBinary(stream));
        }
        obj.setProperties(properties);
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
        stream.writeInt(Integer.reverseBytes(this.getMosaicnonce()));
        stream.writeLong(Long.reverseBytes(this.getMosaicid()));
        stream.writeByte((byte)this.properties.size());
        byte[] flags = this.getFlags().serialize();
        stream.write(flags, 0, flags.length);
        stream.writeByte(this.getDivisibility());
        for (int i = 0; i < this.properties.size(); i++) {
            byte[] ser = this.properties.get(i).serialize();
            stream.write(ser, 0, ser.length);
        }
        stream.close();
        return bos.toByteArray();
    }

    private int size;
    private ByteBuffer signer;
    private short version;
    private EntityTypeBuilder type;
    private int mosaicNonce;
    private long mosaicId;
    private MosaicFlagsBuilder flags;
    private byte divisibility;
    private java.util.ArrayList<MosaicPropertyBuilder> properties;

}
