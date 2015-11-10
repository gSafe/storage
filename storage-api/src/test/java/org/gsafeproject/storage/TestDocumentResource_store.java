package org.gsafeproject.storage;

/*
 * #%L
 * storage-api
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

import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.gsafeproject.storage.service.StorageService;
import org.gsafeproject.storage.webresource.DocumentResource;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.yammer.dropwizard.testing.ResourceTest;

public class TestDocumentResource_store extends ResourceTest {

    private StorageService mockService;

    @Override
    protected void setUpResources() throws Exception {
        mockService = Mockito.mock(StorageService.class);
        addResource(new DocumentResource(mockService));
    }

    @Test
    public void testGETInfo() {
        String info = client().resource("/documents/info").get(String.class);
        assertEquals("Documents resource", info);
    }

    @Test
    public void badRequest_IfNUllStream() {
        FormDataMultiPart fdmp = createFormDataMultiPart((File) null);
        ClientResponse response = client().resource("/documents")//
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)//
                .post(ClientResponse.class, fdmp);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void badRequest_IfNUllFingerprint() {
        FormDataMultiPart fdmp = createFormDataMultiPart("test.file");
        fdmp.field("fingerprint", "");
        ClientResponse response = client().resource("/documents")//
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)//
                .post(ClientResponse.class, fdmp);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void badRequest_IfNUllContainer() {
        FormDataMultiPart fdmp = createFormDataMultiPart("test.file");
        fdmp.field("fingerprint", "8df374f7933528b29538602f1f95f04063c3bec4");
        fdmp.field("container", "");
        ClientResponse response = client().resource("/documents")//
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)//
                .post(ClientResponse.class, fdmp);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void badRequest_IfWrongFingerprint() {
        FormDataMultiPart fdmp = createFormDataMultiPart("test.file");
        fdmp.field("fingerprint", "AAAAAAAAAAAAAAAAAAAAAAAA");
        fdmp.field("container", "ABC");
        ClientResponse response = client().resource("/documents")//
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)//
                .post(ClientResponse.class, fdmp);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void notFound_IfContainerDoesNotExist() {
        FormDataMultiPart fdmp = createFormDataMultiPart("test.file");
        fdmp.field("fingerprint", "AAAAAAAAAAAAAAAAAAAAAAAA");
        fdmp.field("container", "ABC");
        fdmp.field("path", "/to/file");
        Mockito.when(mockService.isContainerExists("ABC")).thenReturn(false);
        ClientResponse response = client().resource("/documents")//
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)//
                .post(ClientResponse.class, fdmp);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void conflict_IfDocAlreadyExist() {
        FormDataMultiPart fdmp = createFormDataMultiPart("test.file");
        fdmp.field("fingerprint", "AAAAAAAAAAAAAAAAAAAAAAAA");
        fdmp.field("container", "ABC");
        fdmp.field("path", "/to/file");
        Mockito.when(mockService.isContainerExists("ABC")).thenReturn(true);
        Mockito.when(mockService.isDocumentExists("ABC", "/to/file")).thenReturn(true);
        ClientResponse response = client().resource("/documents")//
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)//
                .post(ClientResponse.class, fdmp);
        assertEquals(409, response.getStatus());
    }

    private FormDataMultiPart createFormDataMultiPart(File file) {
        FormDataMultiPart fdmp = new FormDataMultiPart();
        if (file != null) {
            fdmp.bodyPart(new FileDataBodyPart("on", file, MediaType.TEXT_PLAIN_TYPE));
        } else {
            fdmp.bodyPart(new BodyPart("rien", MediaType.APPLICATION_OCTET_STREAM_TYPE));
        }
        return fdmp;
    }

    private FormDataMultiPart createFormDataMultiPart(String fileNameForClassLoader) {
        return createFormDataMultiPart(loadFile(fileNameForClassLoader));
    }

    private File loadFile(String fileNameForClassLoader) {
        return new File(this.getClass().getClassLoader().getResource(fileNameForClassLoader).getFile());
    }
}
