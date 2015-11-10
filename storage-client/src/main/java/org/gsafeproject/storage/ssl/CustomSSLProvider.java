package org.gsafeproject.storage.ssl;

/*
 * #%L
 * storage-client
 * %%
 * Copyright (C) 2013 - 2015 gSafe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.google.common.collect.ObjectArrays;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Map;

public class CustomSSLProvider {

    private Map<String, String> trustConfigs;
    private Map<String, String> keyConfigs;


    public CustomSSLProvider(Map<String, String> trustConfigs, Map<String, String> keyConfig) {
        this.trustConfigs = trustConfigs;
        this.keyConfigs = keyConfig;
    }

    private char[] readPassword(String pwd) throws IOException {
        return pwd.toCharArray();
    }

    private java.io.InputStream readKeyInputStream(String filePath) throws IOException {
        Path keyPath = Paths.get(filePath);
        return Files.newInputStream(keyPath);
    }

    private java.io.InputStream readTrustInputStream(String filePath) throws IOException {
        Path keyPath = Paths.get(filePath);
        return Files.newInputStream(keyPath);
    }

    private KeyManager[] loadKeyManagers(String filePath, String pwd) throws IOException {
        char[] password = readPassword(pwd);
        try (InputStream keyInputStream = readKeyInputStream(filePath)) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keyInputStream, password);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, password);
            return kmf.getKeyManagers();
        } catch (Exception e) {
            throw new IOException("Unable to load Key manager configuration");
        }
    }

    private TrustManager[] loadTrustManagers(String filePath, String pwd) throws IOException {
        char[] password = readPassword(pwd);
        try (InputStream trustInputStream = readTrustInputStream(filePath)) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(trustInputStream, password);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            return tmf.getTrustManagers();
        } catch (Exception e) {
            throw new IOException("Unable to load trust stire configuration");
        }
    }

    private TrustManager[] readTrustManagers() throws IOException {
        TrustManager[] result = ObjectArrays.newArray(TrustManager.class, 0);
        for (Map.Entry<String, String> entry : trustConfigs.entrySet()) {
            result = ObjectArrays.concat(result, loadTrustManagers(entry.getKey(), entry.getValue()), TrustManager.class);
        }
        return result;
    }

    private KeyManager[] readKeyManagers() throws IOException {
        KeyManager[] result = ObjectArrays.newArray(KeyManager.class, 0);
        for (Map.Entry<String, String> entry : keyConfigs.entrySet()) {
            result = ObjectArrays.concat(result, loadKeyManagers(entry.getKey(), entry.getValue()), KeyManager.class);
        }
        return result;
    }

    public SSLContext createSSLContext() throws IOException {
        try {
            KeyManager[] keyManagers = readKeyManagers();
            TrustManager[] trustManagers = readTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, null);
            return sslContext;
        } catch (Exception e) {
            throw new IOException("Unable to create SSL Context");
        }
    }

}
