package org.gsafeproject.storage.service;

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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;

import com.google.common.io.ByteStreams;

public abstract class TestStorageService {

    private StorageService service;

    public void initService(StorageService service) {
        this.service = service;
    }

    // Container

    @Test
    public void shouldCheckContainerExistence() throws IOException {
        String name = generateContainerName();
        assertFalse(service.isContainerExists(name));
        try {
            service.createContainer(name);
            assertTrue(service.isContainerExists(name));
            service.deleteContainer(name);
            assertFalse(service.isContainerExists(name));
        } finally {
            service.deleteContainer(name);
        }
    }

    @Test
    public void shouldNotRaiseAnException_CreatingAContainerWhichAlreadyExists() throws IOException {
        String name = generateContainerName();
        service.createContainer(name);
        service.createContainer(name);
        service.deleteContainer(name);
    }

    @Test
    public void shouldNotRaiseAnException_DeletingAContainerWhichDoesNotExists() throws IOException {
        String name = generateContainerName();
        service.createContainer(name);
        service.deleteContainer(name);
        service.deleteContainer(name);
    }

    @Test
    public void shouldCreateAndDeleteContainer() throws IOException {
        String name = generateContainerName();
        service.createContainer(name);
        service.deleteContainer(name);
    }

    // Document

    @Test
    public void shouldCheckFileExistence() throws IOException {
        String container = generateContainerName();
        String filePath = "path/file.pdf";
        assertFalse(service.isDocumentExists(container, filePath));
        try {
            service.createContainer(container);
            assertFalse(service.isDocumentExists(container, filePath));
            service.store(container, filePath, getTestBytes());
            assertTrue(service.isDocumentExists(container, filePath));
            service.delete(container, filePath);
            assertFalse(service.isDocumentExists(container, filePath));
        } finally {
            service.deleteContainer(container);
        }
    }

    @Test
    public void shouldReturnNull_LoadingDocWhichDoesNotExist() throws IOException {
        String name = generateContainerName();
        try {
            service.createContainer(name);
            assertNull(service.read(name, "path/no-file-here.pdf"));
        } finally {
            service.deleteContainer(name);
        }
    }

    @Test
    public void shouldSaveLoadAndDeleteDocument() throws IOException {
        String name = generateContainerName();
        try {
            service.createContainer(name);
            service.store(name, "path/file.pdf", getTestBytes());

            assertTrue(Arrays.equals(getTestBytes(), service.read(name, "path/file.pdf")));
            service.delete(name, "path/file.pdf");
            assertNull(service.read(name, "path/file.pdf"));
        } finally {
            service.deleteContainer(name);
        }
    }

    // Check parameters

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_CreatingAContainerWithNullParam() {
        service.checkDocumentParam(null, "path/file.pdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_CreatingAContainerWithEmptyParam() {
        service.checkDocumentParam("", "path/file.pdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_DeletingAContainerWithNullParam() throws IOException {
        service.checkDocumentParam("container", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_DeletingContainerWithEmptyParam() throws IOException {
        service.checkDocumentParam("container", "");
    }

    private String generateContainerName() {
        return "container" + UUID.randomUUID();
    }

    private byte[] getTestBytes() throws IOException {
        return ByteStreams.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("file.pdf"));
    }
}
