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

import java.io.IOException;

import org.gsafeproject.storage.service.StorageService;
import org.gsafeproject.storage.webresource.DocumentResource;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;

public class TestDocumentResource_delete extends ResourceTest {

    private StorageService mockService;

    @Override
    protected void setUpResources() throws Exception {
        mockService = Mockito.mock(StorageService.class);
        Mockito.when(mockService.isDocumentExists("unknown", "doc42")).thenReturn(false);
        Mockito.when(mockService.isDocumentExists(null, "doc42")).thenReturn(false);
        Mockito.when(mockService.isDocumentExists("my_container", "unknown")).thenReturn(false);
        Mockito.when(mockService.isDocumentExists("my_container", "doc42")).thenReturn(true);
        Mockito.when(mockService.isDocumentExists("my_container789!@ÉÉEàôÎ", "photo/vacances2014/!*qweWEÉÀÔ.doc7894~p")).thenReturn(true);
        Mockito.when(mockService.isDocumentExists("my_container", "error")).thenReturn(true);
        Mockito.doThrow(new IOException("error! (test)")).when(mockService).delete("my_container", "error");
        addResource(new DocumentResource(mockService));
    }

    @Test
    public void testGETInfo() {
        String info = client().resource("/documents/info").get(String.class);
        assertEquals("Documents resource", info);
    }

    @Test
    public void notFound_ifInexistantContainer() throws IOException {
        ClientResponse response = client().resource("/documents/unknown/doc42")//
                .delete(ClientResponse.class);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void notFound_ifEmptyContainer() {
        ClientResponse response = client().resource("/documents//doc42")//
                .delete(ClientResponse.class);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void notFound_ifInexistantDocument() {
        ClientResponse response = client().resource("/documents/my_container/unknown")//
                .delete(ClientResponse.class);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void notFound_ifEmptyDocument() {
        ClientResponse response = client().resource("/documents/my_container")//
                .delete(ClientResponse.class);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void shouldDeleteDoc() throws ClientHandlerException, UniformInterfaceException, IOException {
        ClientResponse response = client().resource("/documents/my_container/doc42")//
                .delete(ClientResponse.class);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).delete("my_container", "doc42");
    }

    @Test
    public void shouldDeleteDiacriticDoc() throws ClientHandlerException, UniformInterfaceException, IOException {
        ClientResponse response = client().resource("/documents/my_container789!@ÉÉEàôÎ/photo%2Fvacances2014%2F!*qweWEÉÀÔ.doc7894~p")//
                .delete(ClientResponse.class);
        assertEquals(200, response.getStatus());
        Mockito.verify(mockService).delete("my_container789!@ÉÉEàôÎ", "photo/vacances2014/!*qweWEÉÀÔ.doc7894~p");
    }

    @Test
    public void serverError_onIOException() throws ClientHandlerException, UniformInterfaceException, IOException {
        ClientResponse response = client().resource("/documents/my_container/error")//
                .delete(ClientResponse.class);
        assertEquals(500, response.getStatus());
    }
}
