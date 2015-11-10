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

import java.io.File;
import java.util.Scanner;

import org.gsafeproject.healthcheck.StorageHealthCheck;
import org.gsafeproject.storage.config.StorageConfiguration;
import org.gsafeproject.storage.service.EncryptStorageService;
import org.gsafeproject.storage.service.StorageService;
import org.gsafeproject.storage.webresource.ContainerResource;
import org.gsafeproject.storage.webresource.DocumentResource;

import com.google.common.base.Strings;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class StorageApp extends Service<StorageConfiguration> {

    private Environment env;

    public static void main(String[] args) throws Exception {
        if (isValid(args)) {
            new StorageApp().run(args);
        }
    }

    private static boolean isValid(String[] args) {
        if (1 >= args.length || Strings.isNullOrEmpty(args[1])) {
            System.out.println("Usage java -jar storage.jar server /path/configuration.yml");
            return false;
        }
        if (!new File(args[1]).exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist.", args[1]));
        }
        return true;
    }

    @Override
    public void initialize(Bootstrap<StorageConfiguration> bootstrap) {
        bootstrap.setName("Storage application");
        bootstrap.addBundle(new AssetsBundle("/docs/", "/docs", "index.html"));
    }

    @Override
    public void run(StorageConfiguration config, Environment env) throws Exception {
        this.env = env;
        StorageService service;
        if(config.storage.isEncrypt) {
            System.out.println("ENCRYPT MODE");
            Scanner sc = new Scanner(System.in);

            if(config.storage.encryptMasterKey == null) {
                System.out.println("Please type the master key:");
                config.storage.encryptMasterKey = sc.nextLine();
            }

            if(config.storage.encryptSalt == null) {
                System.out.println("Please type the salt:");
                config.storage.encryptSalt = sc.nextLine();
            }

            service = new EncryptStorageService(config.storage);
        } else {
            service = new StorageService(config.storage);
        }
        env.addResource(new DocumentResource(service));
        env.addResource(new ContainerResource(service));
        env.addHealthCheck(new StorageHealthCheck());
    }

    public void stop() throws Exception {
        if (env != null) {
            env.stop();
        }
    }
}
