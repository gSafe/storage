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
import java.net.URISyntaxException;

import org.junit.Test;

public class TestStorageClient_upload extends TestStorageClient {

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfNullParameters() throws IOException, URISyntaxException {
        client.upload(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfNullDirectoriesParameters() throws IOException,
            URISyntaxException {
        client.upload("", "", getTestFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfEmptyDirectoriesParameters() throws IOException,
            URISyntaxException {
        client.upload("", "", getTestFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfContainerParameterIsMissing() throws IOException,
            URISyntaxException {
        client.upload(null, "path", getTestFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfContainerParameterIsEmpty() throws IOException,
            URISyntaxException {
        client.upload("", "path", getTestFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfPathParameterIsMissing() throws IOException,
            URISyntaxException {
        client.upload("container", null, getTestFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfPathParameterIsEmpty() throws IOException,
            URISyntaxException {
        client.upload("container", "", getTestFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfFileParameterIsMissing() throws IOException,
            URISyntaxException {
        client.upload("container", "path", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseAnException_IfFileDoesNotExist() throws IOException, URISyntaxException {
        client.upload("container", "path", new File("/this/file/doesnt/not/exist"));
    }

    @Test(expected = IOException.class)
    public void shouldRaiseAnException_IfServiceCantStoreDocument() throws IOException,
            URISyntaxException {
        client.upload("bad", "error", getTestFile());
    }

    @Test
    public void shouldUploadDocument() throws IOException, URISyntaxException {
        client.upload("container", "path", getTestFile());
        client.upload("container", "path/42/24b", getTestFile());
        client.upload("container@#-é", "path/%€éàô", getTestFile());
    }

    private File getTestFile() throws URISyntaxException {
        return new File(Thread.currentThread().getContextClassLoader().getResource("file.pdf")
                .toURI());
    }
}
