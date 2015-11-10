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

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import java.io.IOException;

public abstract class TestStorageClient extends JerseyTest {

    protected static final int PORT = 8989;
    protected StorageClient client;

    @Override
    protected int getPort(int defaultPort) {
        return PORT;
    }

    public TestStorageClient()  {
        super(new WebAppDescriptor.Builder("org.gsafeproject.storage.mock").servletClass(com.sun.jersey.spi.container.servlet.ServletContainer.class)//
                .initParam("com.sun.jersey.api.json.POJOMappingFeature", "true").contextPath("/").build());
        StorageClientBuilder scb = new StorageClientBuilder();
        try {
            client = scb.serviceUrl(String.format("http://localhost:%s", PORT)).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
