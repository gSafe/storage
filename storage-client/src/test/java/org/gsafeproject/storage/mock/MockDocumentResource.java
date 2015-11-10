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

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/documents")
public class MockDocumentResource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response store(@FormDataParam("on") InputStream fileStream, //
            @FormDataParam("on") FormDataContentDisposition fileDetail, //
            @FormDataParam("on") FormDataBodyPart body, //
            @FormDataParam("fingerprint") String fingerprint,//
            @FormDataParam("container") String container,//
            @FormDataParam("path") String path) {
        if ("bad".equals(container) //
                || "bad".equals(path) //
                || "bad".equals(fingerprint)) {
            return Response.serverError().build();
        }
        if ("d2542264a54ac8672c2638762c3c5c2965d35651".equals(fingerprint)) {
            return Response.ok("ok").build();
        }
        return Response.serverError().build();
    }

    @GET
    @Path("/{container}/{path}")
    public Response retrieve(@PathParam("container") String container, @PathParam("path") String path) {
        if ("bad".equals(container) //
                || "bad".equals(path)) {
            return Response.serverError().build();
        }
        if ("unknown".equals(path)) {
            return null;
        }
        byte[] doc = { 1, 0, 0, 1, 1 };
        return Response.ok(doc)//
                .header("Content-Length", doc.length)//
                .header("Content-Disposition", "attachment; filename=\"document\"")//
                .build();
    }
}
