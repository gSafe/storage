package org.gsafeproject.storage.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.gsafeproject.storage.config.StorageServiceConfiguration;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;

import com.google.common.io.ByteStreams;

/*
 * #%L
 * storage-core
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

public class EncryptStorageService extends StorageService {

    private SecretKeySpec secret;

    public EncryptStorageService(StorageServiceConfiguration config) throws IOException {
        super(config);
        char[] key = config.encryptMasterKey.toCharArray();
        byte[] salt = config.encryptSalt.getBytes();
        secret = deriveKey(key, salt);
    }

    @Override
    public String store(String container, String filePath, byte[] bytes) throws IOException {
        byte[] encryptBytes = encrypt(bytes, secret);

        checkDocumentParam(container, filePath);
        try (InputStream stream = new ByteArrayInputStream(encryptBytes)) {
            BlobStore blobStore = context.getBlobStore();
            Blob blob = blobStore.blobBuilder(filePath).payload(stream).build();
            blobStore.putBlob(container, blob);
        } finally {
            context.close();
        }
        return String.format("/%s/%s", container, filePath);
    }

    @Override
    public byte[] read(String container, String filePath) throws IOException {
        checkDocumentParam(container, filePath);
        try {
            BlobStore blobStore = context.getBlobStore();
            Blob blob = blobStore.getBlob(container, filePath);
            if (null != blob) {
                try (InputStream stream = blob.getPayload().openStream()) {
                    return decrypt(stream, secret);
                }
            }
        } finally {
            context.close();
        }
        return null;
    }

    protected Cipher initEncryptCipher(SecretKeySpec secret) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec("1234567890123456".getBytes()));
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    protected Cipher initDecryptCipher(SecretKeySpec secret) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec("1234567890123456".getBytes()));
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    protected SecretKeySpec deriveKey(char[] key, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(key, salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public byte[] encrypt(byte[] input, SecretKeySpec secret) throws IOException {
        Cipher cipher = initEncryptCipher(secret);
        try (ByteArrayInputStream is = new ByteArrayInputStream(input); ByteArrayOutputStream os = new ByteArrayOutputStream(); CipherOutputStream cos = new CipherOutputStream(os, cipher);) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
            cos.close();
            return os.toByteArray();
        }
    }

    public byte[] decrypt(InputStream cipherInput, SecretKeySpec secret) throws IOException {
        Cipher cipher = initDecryptCipher(secret);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream(); CipherInputStream cis = new CipherInputStream(cipherInput, cipher);) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            return os.toByteArray();
        }
    }

}
