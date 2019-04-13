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

public class SecretLockTransactionBuilder {
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

    public UnresolvedMosaicBuilder getMosaic()  {
        return this.mosaic;
    }

    public void setMosaic(UnresolvedMosaicBuilder mosaic)  {
        this.mosaic = mosaic;
    }

    public long getDuration()  {
        return this.duration;
    }

    public void setDuration(long duration)  {
        this.duration = duration;
    }

    public LockHashAlgorithmBuilder getHashalgorithm()  {
        return this.hashAlgorithm;
    }

    public void setHashalgorithm(LockHashAlgorithmBuilder hashAlgorithm)  {
        this.hashAlgorithm = hashAlgorithm;
    }

    public ByteBuffer getSecret()  {
        return this.secret;
    }

    public void setSecret(ByteBuffer secret)  {
        if (secret == null)
            throw new NullPointerException("secret");
        
        if (secret.array().length != 32)
            throw new IllegalArgumentException("secret should be 32 bytes");
        
        this.secret = secret;
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

    public static SecretLockTransactionBuilder loadFromBinary(DataInput stream) throws Exception {
        SecretLockTransactionBuilder obj = new SecretLockTransactionBuilder();
        obj.setSize(Integer.reverseBytes(stream.readInt()));
        obj.signature = ByteBuffer.allocate(64);
        stream.readFully(obj.signature.array());
        obj.signer = ByteBuffer.allocate(32);
        stream.readFully(obj.signer.array());
        obj.setVersion(Short.reverseBytes(stream.readShort()));
        obj.setType(EntityTypeBuilder.loadFromBinary(stream));
        obj.setFee(Long.reverseBytes(stream.readLong()));
        obj.setDeadline(Long.reverseBytes(stream.readLong()));
        obj.setMosaic(UnresolvedMosaicBuilder.loadFromBinary(stream));
        obj.setDuration(Long.reverseBytes(stream.readLong()));
        obj.setHashalgorithm(LockHashAlgorithmBuilder.loadFromBinary(stream));
        obj.secret = ByteBuffer.allocate(32);
        stream.readFully(obj.secret.array());
        obj.recipient = ByteBuffer.allocate(25);
        stream.readFully(obj.recipient.array());
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
        byte[] mosaic = this.getMosaic().serialize();
        stream.write(mosaic, 0, mosaic.length);
        stream.writeLong(Long.reverseBytes(this.getDuration()));
        byte[] hashAlgorithm = this.getHashalgorithm().serialize();
        stream.write(hashAlgorithm, 0, hashAlgorithm.length);
        stream.write(this.secret.array(), 0, this.secret.array().length);
        stream.write(this.recipient.array(), 0, this.recipient.array().length);
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
    private UnresolvedMosaicBuilder mosaic;
    private long duration;
    private LockHashAlgorithmBuilder hashAlgorithm;
    private ByteBuffer secret;
    private ByteBuffer recipient;

}
