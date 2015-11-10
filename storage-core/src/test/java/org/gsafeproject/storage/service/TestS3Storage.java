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

import java.io.IOException;
import java.util.Properties;

import org.gsafeproject.storage.config.StorageServiceConfiguration;
import org.junit.Test;

public class TestS3Storage extends TestStorageService {

    public TestS3Storage() throws IOException {
        Properties props = new Properties();
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("s3.properties"));
        StorageServiceConfiguration config = new StorageServiceConfiguration();
        config.host = props.getProperty("host");
        config.identity = props.getProperty("identity");
        config.credential = props.getProperty("credential");

        super.initService(new StorageService(config));
    }

    @Test
    public void shouldCreateAndDeleteContainer() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldCheckContainerExistence() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldNotRaiseAnException_CreatingAContainerWhichAlreadyExists() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldNotRaiseAnException_DeletingAContainerWhichDoesNotExists() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldCheckFileExistence() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldReturnNull_LoadingDocWhichDoesNotExist() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldSaveLoadAndDeleteDocument() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldRaiseAnException_CreatingAContainerWithNullParam() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldRaiseAnException_CreatingAContainerWithEmptyParam() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldRaiseAnException_DeletingAContainerWithNullParam() {
        // No credentials for gsafe and s3
    }

    @Test
    public void shouldRaiseAnException_DeletingContainerWithEmptyParam() {
        // No credentials for gsafe and s3
    }

}
