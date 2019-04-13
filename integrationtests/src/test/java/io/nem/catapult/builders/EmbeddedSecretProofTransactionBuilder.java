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

public class EmbeddedSecretProofTransactionBuilder {
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

    public ByteBuffer getProof()  {
        return this.proof;
    }

    public void setProof(ByteBuffer proof)  {
        if (proof == null)
            throw new NullPointerException("proof");
        
        
        this.proof = proof;
    }

    public static EmbeddedSecretProofTransactionBuilder loadFromBinary(DataInput stream) throws Exception {
        EmbeddedSecretProofTransactionBuilder obj = new EmbeddedSecretProofTransactionBuilder();
        obj.setSize(Integer.reverseBytes(stream.readInt()));
        obj.signer = ByteBuffer.allocate(32);
        stream.readFully(obj.signer.array());
        obj.setVersion(Short.reverseBytes(stream.readShort()));
        obj.setType(EntityTypeBuilder.loadFromBinary(stream));
        obj.setHashalgorithm(LockHashAlgorithmBuilder.loadFromBinary(stream));
        obj.secret = ByteBuffer.allocate(32);
        stream.readFully(obj.secret.array());
        short proofSize = Short.reverseBytes(stream.readShort());
        obj.proof = ByteBuffer.allocate(proofSize);
        stream.readFully(obj.proof.array());
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
        byte[] hashAlgorithm = this.getHashalgorithm().serialize();
        stream.write(hashAlgorithm, 0, hashAlgorithm.length);
        stream.write(this.secret.array(), 0, this.secret.array().length);
        stream.writeShort(Short.reverseBytes((short)this.proof.array().length));
        stream.write(this.proof.array(), 0, this.proof.array().length);
        stream.close();
        return bos.toByteArray();
    }

    private int size;
    private ByteBuffer signer;
    private short version;
    private EntityTypeBuilder type;
    private LockHashAlgorithmBuilder hashAlgorithm;
    private ByteBuffer secret;
    private ByteBuffer proof;

}
