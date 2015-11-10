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

import javax.ws.rs.core.MediaType;

import org.gsafeproject.storage.service.StorageService;
import org.gsafeproject.storage.webresource.ContainerResource;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;

public class TestContainerResource_create extends ResourceTest {

    private StorageService mockService;

    @Override
    protected void setUpResources() throws Exception {
        mockService = Mockito.mock(StorageService.class);
        addResource(new ContainerResource(mockService));
    }

    @Test
    public void testGETInfo() {
        String info = client().resource("/containers/info").get(String.class);
        assertEquals("Containers resource", info);
    }

    @Test
    public void shouldReturn405_IfContainerNameIsMissing() {
        ClientResponse response = client().resource("/containers").type(MediaType.APPLICATION_JSON)//
                .post(ClientResponse.class);
        assertEquals(405, response.getStatus());
    }

    @Test
    public void shouldReturn409_IfContainerAlreadyExists() {
        Mockito.when(mockService.isContainerExists("exist")).thenReturn(true);
        ClientResponse response = client().resource("/containers/exist").type(MediaType.APPLICATION_JSON)//
                .post(ClientResponse.class);
        assertEquals(409, response.getStatus());
    }

    @Test
    public void shouldReturn200_SavingContainer() {
        ClientResponse response = client().resource("/containers/test_vault").type(MediaType.APPLICATION_JSON)//
                .post(ClientResponse.class);
        assertEquals(200, response.getStatus());
    }
}
