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
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectParser;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/** PEM handling utilities */
public class PEMHelper {
  private PEMHelper() {}

  public static KeyStore addClientKeyAndCertificate(
      final KeyStore keyStore,
      final String aliasName,
      final String password,
      final File keyFile,
      final File certFile) {
    Object obj;
    try (final PemReader keyFileReader = new PemReader(new FileReader(keyFile))) {
      PemObject pemObject = keyFileReader.readPemObject();
      obj = parsePrivateKey(pemObject);
    } catch (Exception e) {
      throw new RuntimeException("Error parsing PEM " + keyFile, e);
    }
    Key key = (Key) obj;
    List<Certificate> certs = new ArrayList<Certificate>();
    try (final PemReader keyNodeReader = new PemReader(new FileReader(certFile))) {
      while ((obj = parse(keyNodeReader.readPemObject())) != null) {
        if (obj instanceof Certificate) {
          final Certificate cert = (Certificate) obj;
          certs.add(cert);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error parsing PEM " + certFile, e);
    }

    try {
      keyStore.setKeyEntry(
          aliasName, key, password.toCharArray(), certs.toArray(new Certificate[certs.size()]));
    } catch (Exception ex) {
      throw new RuntimeException("Error parsing PEM " + keyFile, ex);
    }
    return keyStore;
  }

  /**
   * Load one or more X.509 Certificates from a PEM file
   *
   * @param pemFile A PKCS8 PEM file containing only <code>CERTIFICATE</code> / <code>
   *     X.509 CERTIFICATE</code> blocks
   * @return a JKS KeyStore with the certificate aliases "cert<code>index</code>" where index is the
   *     0-based index of the certificate in the PEM
   * @throws RuntimeException if a problem occurs
   */
  public static KeyStore loadCertificates(final KeyStore keyStore, final File pemFile) {
    try (final PemReader pem = new PemReader(new FileReader(pemFile))) {
      int certIndex = 0;
      Object obj;
      while ((obj = parse(pem.readPemObject())) != null) {
        if (obj instanceof Certificate) {
          final Certificate cert = (Certificate) obj;

          keyStore.setCertificateEntry("cert" + Integer.toString(certIndex++), cert);
        } else {
          throw new RuntimeException("Unknown PEM contents: " + obj + ". Expected a Certificate");
        }
      }

      return keyStore;
    } catch (Exception e) {
      throw new RuntimeException("Error parsing PEM " + pemFile, e);
    }
  }

  /**
   * Parse a PemObject. Currently only supports <code>CERTIFICATE</code> / <code>X.509 CERTIFICATE
   * </code> types
   *
   * @param obj a PemObject with a type and with contents
   * @return a parsed object (or null if the input is null)
   * @throws GeneralSecurityException if there is a parsing problem
   * @throws IllegalArgumentException if the PemObject cannot be recognised
   */
  public static Object parse(final PemObject obj) throws GeneralSecurityException {
    if (obj == null) {
      return null;
    } else if (obj.getType() == null) {
      throw new RuntimeException("Encountered invalid PemObject with null type: " + obj);
    } else if (obj.getType().equalsIgnoreCase("CERTIFICATE")
        || obj.getType().equalsIgnoreCase("X.509 CERTIFICATE")) {
      return parseX509Certificate(obj);
    } else if (obj.getType().equalsIgnoreCase("PRIVATE KEY")) {
      return parsePrivateKey(obj);
    } else {
      throw new IllegalArgumentException(
          "Unknown PEM contents: encountered unsupported entry of type " + obj.getType());
    }
  }

  private static Certificate parseX509Certificate(final PemObject obj)
      throws CertificateException, NoSuchProviderException {
    final CertificateFactory factory = CertificateFactory.getInstance("X.509", "BC");

    return factory.generateCertificate(new ByteArrayInputStream(obj.getContent()));
  }

  private static Key parsePrivateKey(final PemObject obj) {
    return (Key) new PrivateKeyParser("BC").parseObject(obj);
  }

  /**
   * Create an empty key store.
   *
   * @return Empty key store.
   */
  public static KeyStore createEmptyKeyStore() {
    return ExceptionUtils.propagate(
        () -> {
          final KeyStore keyStore = KeyStore.getInstance("JKS");
          // Initialise as empty
          keyStore.load(null);
          return keyStore;
        });
  }
}

/** PEM private key parser. */
class PrivateKeyParser implements PemObjectParser {
  private String provider;

  /** @param provider */
  public PrivateKeyParser(String provider) {
    this.provider = provider;
  }

  /**
   * Parse a PEM key object.
   *
   * @param obj PEM object.
   * @return Key object.
   */
  public Object parseObject(PemObject obj) {
    return ExceptionUtils.propagate(
        () -> {
          final PrivateKeyInfo info =
              PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(obj.getContent()));
          final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(obj.getContent());
          final KeyFactory keyFact =
              KeyFactory.getInstance(
                  info.getPrivateKeyAlgorithm().getAlgorithm().getId(), provider);

          return keyFact.generatePrivate(keySpec);
        });
  }
}
