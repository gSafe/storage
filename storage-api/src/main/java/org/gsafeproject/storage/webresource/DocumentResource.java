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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gsafeproject.storage.service.StorageService;
import org.gsafeproject.storage.webresource.exception.BadRequest;
import org.gsafeproject.storage.webresource.exception.Conflict;
import org.gsafeproject.storage.webresource.exception.InternalServerError;
import org.gsafeproject.storage.webresource.exception.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/documents")
public class DocumentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger("DocumentResource");

    private StorageService service;

    public DocumentResource(StorageService service) {
        this.service = service;
    }

    @GET
    @Path("info")
    public String info() {
        return "Documents resource";
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response store(@FormDataParam("on") InputStream fileStream, //
            @FormDataParam("on") FormDataContentDisposition fileDetail, //
            @FormDataParam("on") FormDataBodyPart body, //
            @FormDataParam("fingerprint") String fingerprint,//
            @FormDataParam("container") String container,//
            @FormDataParam("path") String path) {
        checkParams(fileDetail, fileStream, fingerprint, container, path);
        checkContainerExistence(container);
        if (service.isDocumentExists(container, path)) {
            throw new Conflict(String.format("Document %s in container %s already exists.", path, container));
        }
        String filePath = path + File.separator + fileDetail.getFileName();
        try {
            // TODO : optionnal
            // checkFileIntegrity(fileStream, fingerprint);
            String storePath = service.store(container, filePath, ByteStreams.toByteArray(fileStream));
            return Response.ok("/documents" + storePath).build();
        } catch (IOException e) {
            throw new InternalServerError("Error during document storage", e);
        }
    }

    @GET
    @Path("/{container}/{path:.+}")
    public Response retrieve(@PathParam("container") String container, @PathParam("path") String path) {
        checkDocumentExistence(container, path);
        byte[] doc = null;
        try {
            doc = service.read(container, path);
        } catch (IOException e) {
            throw new InternalServerError("Error during document download", e);
        }
        return Response.ok(doc)//
                .header("Content-Length", doc.length)//
                .header("Content-Disposition", "attachment; filename=\"document\"")//
                .build();
    }

    @DELETE
    @Path("/{container}/{path:.+}")
    public Response delete(@PathParam("container") String container, @PathParam("path") String path) {
        checkDocumentExistence(container, path);
        try {
            service.delete(container, path);
        } catch (IOException e) {
            throw new InternalServerError("Error during document deletion", e);
        }
        return Response.ok().build();
    }

    private void checkParams(FormDataContentDisposition fileDetail, InputStream fileStream, String fingerprint, String container, String path) {
        if (null == fileDetail || Strings.isNullOrEmpty(fileDetail.getName())) {
            throw new BadRequest("file name is a mandatory parameter. Cannot be null!");
        }
        if (fileStream == null) {
            throw new BadRequest("on is a mandatory parameter. Cannot be null!");
        }
        if (Strings.isNullOrEmpty(fingerprint)) {
            throw new BadRequest("fingerprint is a mandatory parameter. Cannot be null!");
        }
        if (Strings.isNullOrEmpty(container)) {
            throw new BadRequest("container is a mandatory parameter. Cannot be null!");
        }

        if (Strings.isNullOrEmpty(path)) {
            throw new BadRequest("path is a mandatory parameter. Cannot be null!");
        }
    }

    // TODO fingerprint algo SHA1 for the moment
    private void checkFileIntegrity(InputStream fileStream, String fingerprint) throws IOException {
        File temp = File.createTempFile("temp", ".tmp");
        ByteStreams.copy(fileStream, new FileOutputStream(temp));
        if (!Files.hash(temp, Hashing.sha1()).toString().equalsIgnoreCase(fingerprint)) {
            throw new BadRequest("Supplied fingerprint is different from document hash");
        }
        if (!temp.delete()) {
            LOGGER.warn("Temp file " + temp.getAbsolutePath() + " has not been deleted");
        }
    }

    private void checkContainerExistence(String container) {
        if (!service.isContainerExists(container)) {
            throw new NotFound(String.format("Container %s not found.", container));
        }
    }

    private void checkDocumentExistence(String container, String path) {
        if (!service.isDocumentExists(container, path)) {
            throw new NotFound(String.format("File %s not found in container %s.", path, container));
        }
    }
}
