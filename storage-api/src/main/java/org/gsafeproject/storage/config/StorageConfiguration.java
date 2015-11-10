package org.gsafeproject.storage.config;

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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.gsafeproject.storage.service.StorageService;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.validation.ValidationMethod;

public class StorageConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    public StorageServiceConfiguration storage = new StorageServiceConfiguration();

    @ValidationMethod(message = "Host is missing.")
    public boolean isHostSet() {
        return !Strings.isNullOrEmpty(storage.host);
    }

    @ValidationMethod(message = "Storage path is missing.")
    public boolean isStoragePathSet() {
        if (null != storage.host && storage.host.equals(StorageService.FILESYSTEM)) {
            if (Strings.isNullOrEmpty(storage.storagePath)) {
                return false;
            }
        }
        return true;
    }

    @ValidationMethod(message = "Identity is missing.")
    public boolean isIdentitySet() {
        if (null != storage.host && storage.host.equals(StorageService.S3)) {
            if (Strings.isNullOrEmpty(storage.identity)) {
                return false;
            }
        }
        return true;
    }

    @ValidationMethod(message = "Credential is missing.")
    public boolean isCredentialSet() {
        if (null != storage.host && storage.host.equals(StorageService.S3)) {
            if (Strings.isNullOrEmpty(storage.credential)) {
                return false;
            }
        }
        return true;
    }
}
