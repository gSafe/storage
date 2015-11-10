package org.gsafeproject.storage.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.gsafeproject.storage.config.StorageServiceConfiguration;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.filesystem.reference.FilesystemConstants;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

/*
 * #%L
 * storage-core
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

public class StorageService {

    public static final String FILESYSTEM = "filesystem";
    public static final String S3 = "s3";

    protected StorageServiceConfiguration config;
    protected BlobStoreContext context;

    public StorageService(StorageServiceConfiguration config) throws IOException {
        this.config = config;
        context = getContext(config);
    }

    public void createContainer(String name) {
        checkContainerParam(name);
        try {
            context.getBlobStore().createContainerInLocation(null, name);
        } finally {
            context.close();
        }
    }

    public void deleteContainer(String name) throws IOException {
        checkContainerParam(name);
        try {
            BlobStore blobStore = context.getBlobStore();
            if (isContainerExists(name)) {
                blobStore.deleteContainer(name);
            }
        } finally {
            context.close();
        }
    }

    public String store(String container, String filePath, byte[] bytes) throws IOException {
        checkDocumentParam(container, filePath);
        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            BlobStore blobStore = context.getBlobStore();
            Blob blob = blobStore.blobBuilder(filePath).payload(stream).build();
            blobStore.putBlob(container, blob);
        } finally {
            context.close();
        }
        return String.format("/%s/%s", container, filePath);
    }

    public byte[] read(String container, String filePath) throws IOException {
        checkDocumentParam(container, filePath);
        try {
            BlobStore blobStore = context.getBlobStore();
            Blob blob = blobStore.getBlob(container, filePath);
            if (null != blob) {
                try (InputStream stream = blob.getPayload().openStream()) {
                    return ByteStreams.toByteArray(stream);
                }
            }
        } finally {
            context.close();
        }
        return null;
    }

    public void delete(String container, String filePath) throws IOException {
        checkDocumentParam(container, filePath);
        try {
            BlobStore blobStore = context.getBlobStore();
            blobStore.removeBlob(container, filePath);
        } finally {
            context.close();
        }
    }

    public BlobStoreContext getContext(StorageServiceConfiguration config) {
        switch (config.host) {
        case FILESYSTEM:
            Properties props = new Properties();
            props.setProperty(FilesystemConstants.PROPERTY_BASEDIR, config.storagePath);
            return ContextBuilder.newBuilder("filesystem").overrides(props).buildView(BlobStoreContext.class);
        case S3:
            return ContextBuilder.newBuilder("aws-s3").credentials(config.identity, config.credential).buildView(BlobStoreContext.class);
        default:
            throw new IllegalArgumentException(String.format("Host %s is not valid.", config.host));
        }
    }

    public boolean isContainerExists(String name) {
        try {
            return context.getBlobStore().containerExists(name);
        } finally {
            context.close();
        }
    }

    public boolean isDocumentExists(String container, String filePath) {
        try {
            BlobStore blobStore = context.getBlobStore();
            return blobStore.containerExists(container) && blobStore.blobExists(container, filePath);
        } finally {
            context.close();
        }
    }

    protected void checkContainerParam(String container) {
        if (Strings.isNullOrEmpty(container)) {
            throw new IllegalArgumentException("Container name is a mandatory parameter.");
        }
    }

    protected void checkDocumentParam(String container, String path) {
        checkContainerParam(container);
        if (Strings.isNullOrEmpty(path)) {
            throw new IllegalArgumentException("Path is a mandatory parameter.");
        }
    }
}
