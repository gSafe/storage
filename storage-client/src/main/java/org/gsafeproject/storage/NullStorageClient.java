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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class NullStorageClient extends StorageClient {

    public NullStorageClient() {
        super(null);
    }

    public NullStorageClient(String serviceUrl) {
        this();
    }

    // ---------------------------------------------------------------------------------------------
    // CONTAINER API
    // ---------------------------------------------------------------------------------------------

    @Override
    public NullStorageClient createContainer(String container) throws IllegalArgumentException {
        return this;
    }

    @Override
    public NullStorageClient deleteContainer(String container) throws IllegalArgumentException {
        return this;
    }

    // ---------------------------------------------------------------------------------------------
    // DOCUMENT API
    // ---------------------------------------------------------------------------------------------

    @Override
    public NullStorageClient upload(String container, String path, File file) throws IOException {
        return this;
    }

    @Override
    public InputStream download(String container, String path) {
        return null;
    }

    @Override
    public NullStorageClient delete(String container, String path) {
        return this;
    }
}
