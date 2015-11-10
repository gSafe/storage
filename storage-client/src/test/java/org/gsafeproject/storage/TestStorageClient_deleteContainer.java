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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class TestStorageClient_deleteContainer extends TestStorageClient {

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfNullParameter() throws IOException, URISyntaxException {
        client.deleteContainer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfEmptyParameter() throws IOException, URISyntaxException {
        client.deleteContainer("");
    }

    @Test(expected = IOException.class)
    public void shouldRaiseAnException_IfServiceError() throws IOException, URISyntaxException {
        client.deleteContainer("bad");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfContainerDoesntExists() throws IOException, URISyntaxException {
        client.deleteContainer("unknown");
    }

    @Test
    public void shouldCreateContainer() throws IOException, URISyntaxException {
        client.deleteContainer("OkContainer");
        assertTrue(true);
    }
}
