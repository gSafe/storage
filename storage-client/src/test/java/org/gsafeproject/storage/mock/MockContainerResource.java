package org.gsafeproject.storage.mock;

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

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/containers")
public class MockContainerResource {

    @POST
    @Path("{name}")
    public Response create(@PathParam("name") String name) {
        if ("bad".equals(name)) {
            return Response.serverError().build();
        }
        if ("AlreadyExists".equals(name)) {
            return Response.status(Status.CONFLICT.getStatusCode()).build();
        }
        return Response.ok("ok").build();
    }

    @DELETE
    @Path("{name}")
    public Response delete(@PathParam("name") String name) {
        if ("bad".equals(name)) {
            return Response.serverError().build();
        }
        if ("unknown".equals(name)) {
            return Response.status(Status.NOT_FOUND.getStatusCode()).build();
        }
        return Response.ok("ok").build();
    }
}
