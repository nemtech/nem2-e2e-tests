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

import java.net.*;

public class SocketFactory {

    /**
     * Open a socket connection to the given server on the given port.
    */
    public static SocketClient OpenSocket(String server, int port, int timeoutInMilliseconds) throws Exception
    {
        Socket socket;

        InetAddress inetAddress = InetAddress.getByName(server);
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);

        // create a socket
        socket = new Socket();

        // this method will block no more than timeout ms.
        socket.connect(socketAddress, timeoutInMilliseconds);

        return new SocketClient(socket);
    }
}
