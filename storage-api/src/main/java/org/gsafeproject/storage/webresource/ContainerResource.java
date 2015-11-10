package org.gsafeproject.storage.webresource;

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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.gsafeproject.storage.service.StorageService;
import org.gsafeproject.storage.webresource.exception.Conflict;
import org.gsafeproject.storage.webresource.exception.InternalServerError;
import org.gsafeproject.storage.webresource.exception.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/containers")
public class ContainerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger("ContainerResource");
    private StorageService service;

    public ContainerResource(StorageService service) {
        this.service = service;
    }

    @GET
    @Path("info")
    public String info() {
        return "Containers resource";
    }

    @POST
    @Path("{name}")
    public Response create(@PathParam("name") String name) {
        if (service.isContainerExists(name)) {
            throw new Conflict(String.format("Container %s already exists.", name));
        }
        try {
            service.createContainer(name);
            return Response.status(200).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerError("Error creating container.", e);
        }
    }

    @DELETE
    @Path("{name}")
    public Response delete(@PathParam("name") String name) {
        if (!service.isContainerExists(name)) {
            throw new NotFound(String.format("Container %s not found.", name));
        }
        try {
            service.deleteContainer(name);
            return Response.status(200).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerError("Error deleting container.", e);
        }
    }

    @GET
    @Path("{name}")
    public Response isExist(@PathParam("name") String name) {
        return Response.status(200).build();
    }
}