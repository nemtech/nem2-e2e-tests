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

package io.nem.symbol.sdk.infrastructure.directconnect.auth;

import io.nem.symbol.core.utils.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import javax.net.ssl.*;
import java.io.File;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
//import org.openjsse;

public class TlsSocket {
  private static final String[] protocols = new String[] {"TLSv1.2"};

  private final File clientKeyFile;
  private final File clientCertificateFile;
  private final File remoteCertificateFile;

  private TlsSocket(
      final File clientKeyFile,
      final File clientCertificateFile,
      final File remoteCertificateFile) {
      Provider[] providers = Security.getProviders();
    Security.addProvider(new BouncyCastleJsseProvider());
    Security.addProvider(new BouncyCastleProvider());
    //Security.addProvider(new OpenJsse());
    this.clientKeyFile = clientKeyFile;
    this.clientCertificateFile = clientCertificateFile;
    this.remoteCertificateFile = remoteCertificateFile;
  }

    /**
     *
     * @param clientKeyFile
     * @param clientCertificateFile
     * @param remoteCertificateFile
     * @return
     */
  public static TlsSocket creaate(final File clientKeyFile,
                           final File clientCertificateFile,
                           final File remoteCertificateFile) {
      return new TlsSocket(clientKeyFile, clientCertificateFile, remoteCertificateFile);
  }

  public Socket createSocket(final String hostName, final int port) {
    return ExceptionUtils.propagate(
        () -> {
          final SSLContext sslContext = createSSLContext();
          final SSLSocketFactory factory = sslContext.getSocketFactory();
          final SSLSocket socket = (SSLSocket) factory.createSocket(hostName, port);
          socket.startHandshake();
          return socket;
        });
  }

  private SSLContext createSSLContext() {
    return ExceptionUtils.propagate(
        () -> {
          final String password = "catapult";
          final String providerName = "BCJSSE";
          final String keyAliasName = "automationkey";
          final KeyStore keyStore = PEMHelper.createEmptyKeyStore();
          PEMHelper.addClientKeyAndCertificate(keyStore, keyAliasName, password, clientKeyFile, clientCertificateFile);
          PEMHelper.loadCertificates(keyStore, remoteCertificateFile);
          // Create key manager
          KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509", providerName);
          keyManagerFactory.init(keyStore, password.toCharArray());
          KeyManager[] km = keyManagerFactory.getKeyManagers();
          // Create trust manager
          TrustManagerFactory trustManagerFactory =
              TrustManagerFactory.getInstance("X509", providerName);
          trustManagerFactory.init(keyStore);
          TrustManager[] tm = trustManagerFactory.getTrustManagers();
          // Initialize SSLContext
          SSLContext sslContext = SSLContext.getInstance(protocols[0], providerName);
          sslContext.init(km, tm, null);
          return sslContext;
        });
  }
}
