package org.gsafeproject.storage;

/*
 * #%L
 * storage-client
 * %%
 * Copyright (C) 2013 - 2014 gSafe
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

import java.io.*;
import java.security.KeyStore;


import javax.net.ssl.SSLContext;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;


public class StorageClient {

    private final String serviceUrl;
    private final Client client;

    public StorageClient(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        client = Client.create(new DefaultClientConfig());
    }

    protected StorageClient(String serviceUrl, Client client) {
        this.serviceUrl = serviceUrl;
        this.client = client;
    }

    // ---------------------------------------------------------------------------------------------
    // CONTAINER API
    // ---------------------------------------------------------------------------------------------

    public StorageClient createContainer(String container) throws IllegalArgumentException, ClientHandlerException, UniformInterfaceException, IOException {
        checkParams(container);
        ClientResponse response = client.resource(serviceUrl).path("/containers/" + container).post(ClientResponse.class);
        if (ClientResponse.Status.CONFLICT.getStatusCode() == response.getStatus()) {
            throw new IllegalArgumentException("Container already exists!");
        }
        if (ClientResponse.Status.OK.getStatusCode() != response.getStatus()) {
            throw new IOException(String.format("Storage service error. Response %s : %s.", response.getStatus(), response.getEntity(String.class)));
        }
        return this;
    }

    public StorageClient deleteContainer(String container) throws IllegalArgumentException, ClientHandlerException, UniformInterfaceException, IOException {
        checkParams(container);
        ClientResponse response = client.resource(serviceUrl).path("/containers/" + container).delete(ClientResponse.class);
        if (ClientResponse.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
            throw new IllegalArgumentException("Container doesn't exists!");
        }
        if (ClientResponse.Status.OK.getStatusCode() != response.getStatus()) {
            throw new IOException(String.format("Storage service error. Response %s : %s.", response.getStatus(), response.getEntity(String.class)));
        }
        return this;
    }

    // ---------------------------------------------------------------------------------------------
    // DOCUMENT API
    // ---------------------------------------------------------------------------------------------

    public StorageClient upload(String container, String path, File file) throws IOException {
        checkParams(container, path, file);
        ClientResponse response = client.resource(serviceUrl).path("/documents").type(MediaType.MULTIPART_FORM_DATA)//
                .post(ClientResponse.class, buildQueryParams(container, path, file));
        if (ClientResponse.Status.OK.getStatusCode() != response.getStatus()) {
            throw new IOException(String.format("Storage service error. Response %s : %s.", response.getStatus(), response.getEntity(String.class)));
        }
        return this;
    }

    public StorageClient upload(String container, String path, File file, String algo, String hash) throws IOException {
        // TODO : not yet implemented
        return this;
    }

    public InputStream download(String container, String path) throws ClientHandlerException, UniformInterfaceException, IOException {
        checkParams(container, path);
        ClientResponse response = client.resource(serviceUrl).path(String.format("/documents/%s/%s", container, path.replaceAll("/", "%2F")))//
                .get(ClientResponse.class);
        if (ClientResponse.Status.NO_CONTENT.getStatusCode() == response.getStatus()) {
            return null;
        }
        if (ClientResponse.Status.OK.getStatusCode() != response.getStatus()) {
            throw new IOException(String.format("Storage service error. Response %s : %s.", response.getStatus(), response.getEntity(String.class)));
        }
        byte[] bytes = response.getEntity(byte[].class);
        return new ByteArrayInputStream(bytes);
    }

    public StorageClient delete(String container, String path) throws ClientHandlerException, UniformInterfaceException, IOException {
        checkParams(container, path);
        ClientResponse response = client.resource(serviceUrl).path(String.format("/documents/%s/%s", container, path.replaceAll("/", "%2F")))//
                .delete(ClientResponse.class);
        if (ClientResponse.Status.OK.getStatusCode() != response.getStatus()) {
            throw new IOException(String.format("Storage service error. Response %s : %s.", response.getStatus(), response.getEntity(String.class)));
        }
        return this;
    }

    public StorageClient isConsistent(String container, String path, String algo, String hash) {
        // TODO : not implemented
        return this;
    }

    public StorageClient isExist(String container, String path) {
        // TODO : not implemented
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    private void checkParams(String container) {
        if (Strings.isNullOrEmpty(container)) {
            throw new IllegalArgumentException("Container is missing.");
        }
    }

    private void checkParams(String container, String path) {
        checkParams(container);
        if (Strings.isNullOrEmpty(path)) {
            throw new IllegalArgumentException("Path is missing.");
        }
    }

    private void checkParams(String container, String path, File file) {
        checkParams(container, path);
        if (null == file || !file.exists()) {
            throw new IllegalArgumentException("File is missing.");
        }
    }

    private FormDataMultiPart buildQueryParams(String container, String path, File file) throws IOException {
        // TODO : default algo is sha1
        // TODO add algo
        String fingerprint = com.google.common.io.Files.hash(file, Hashing.sha1()).toString();

        FormDataMultiPart queryParams = new FormDataMultiPart();
        queryParams.bodyPart(new FormDataBodyPart("container", container, MediaType.TEXT_PLAIN_TYPE));
        queryParams.bodyPart(new FormDataBodyPart("path", path, MediaType.TEXT_PLAIN_TYPE));
        queryParams.bodyPart(new FormDataBodyPart("fingerprint", fingerprint, MediaType.TEXT_PLAIN_TYPE));
        queryParams.bodyPart(new FileDataBodyPart("on", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));

        return queryParams;
    }
}
