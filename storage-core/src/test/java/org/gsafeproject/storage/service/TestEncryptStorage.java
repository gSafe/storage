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

import java.io.File;
import java.io.IOException;

import org.gsafeproject.storage.config.StorageServiceConfiguration;

import com.google.common.io.Files;

public class TestEncryptStorage extends TestStorageService {

    public TestEncryptStorage() throws IOException {
        StorageServiceConfiguration config = new StorageServiceConfiguration();
        config.host = "filesystem";
        config.isEncrypt = true;
        config.encryptMasterKey = "my master key";
        config.encryptSalt = "my salt";

        final File tempDir;
        tempDir = Files.createTempDir();
        config.storagePath = tempDir.getCanonicalPath();
        super.initService(new EncryptStorageService(config));
    }
}
