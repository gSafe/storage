package org.gsafeproject.storage;

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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.gsafeproject.storage.ssl.CustomSSLProvider;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//Builder Usage for https:
//StorageClientBuilder scb = new StorageClientBuilder();
//StorageClient storageClient = scb.serviceUrl("https://localhost:8443")
//        .verifyHost(false)
//        .addTrustStore("path to trust.jks", "trustPwd")
//        .addKeyStore("path to key.jks", "keyPwd")
//        .build();

public class StorageClientBuilder {

    private Map<String, String> trustConfigs = new HashMap<>();
    private Map<String, String> keyConfigs = new HashMap<>();
    private String serviceUrl;
    private boolean verifyHost = true;

    public StorageClientBuilder addTrustStore(String filePath, String password) {
        trustConfigs.put(filePath, password);
        return this;
    }

    public StorageClientBuilder addKeyStore(String filePath, String password) {
        keyConfigs.put(filePath, password);
        return this;
    }

    public StorageClientBuilder serviceUrl(String url) {
        serviceUrl = url;
        return this;
    }

    public StorageClientBuilder verifyHost(boolean verify) {
        verifyHost = verify;
        return this;
    }

    public StorageClient build() throws IOException {
        DefaultClientConfig dcc = new DefaultClientConfig();
        if (!trustConfigs.isEmpty() || !keyConfigs.isEmpty()) {
            SSLContext sslContext = loadSSL();
            HTTPSProperties props = new HTTPSProperties(getHostnameVerifier(), sslContext);
            dcc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, props);
        }
        Client client = Client.create(dcc);
        return new StorageClient(serviceUrl, client);
    }

    private SSLContext loadSSL() throws IOException {
        CustomSSLProvider cSSLp = new CustomSSLProvider(trustConfigs, keyConfigs);
        return cSSLp.createSSLContext();
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                //fasle == !verifyHost, if you want to validate dns hostname
                return !verifyHost;
            }
        };
    }

}
