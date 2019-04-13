/*
 * Copyright (c) 2016-present,
 * Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 *
 * This file is part of Catapult.
 *
 * Catapult is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catapult is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Catapult.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.nem.automationHelpers.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SocketClient {

    protected Socket socket;

    public SocketClient(Socket socket) {
        this.socket = socket;
    }

    public ByteBuffer Read(int size) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] buffer = byteBuffer.array();

        int index = 0;
        int readSize = 0;
        do {
            readSize = socket.getInputStream().read(buffer, index, buffer.length - index);
            index += readSize;
        } while ((readSize != -1) && (index < size));
        byteBuffer.rewind();
        return byteBuffer;
    }

    public void Write(ByteBuffer byteBuffer) throws IOException {
        OutputStream outStream = socket.getOutputStream();

        outStream.write(byteBuffer.array());
    }
}
