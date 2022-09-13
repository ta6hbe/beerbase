package com.hperperidis.beerbase.service;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.hperperidis.beerbase.Exceptions.FileServiceException;
import lombok.Getter;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Class providing functionality to assist in downloading file content via HTTP requests, in order to
 * injest data in our database.
 *
 * Uses Non Blocking IO and returns futures. Can be instantiated with no args. In that case it creates the temporary
 * file store in a random UUID named folder.
 *
 * Alternatively, it can be instantiated with a path string, which is where it will attempt to store temporary files.
 *
 * All temp storage is marked for deletion upon JVM exit. I.e. will be deleted when the application exits.
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 */
@Service
@Getter
public class FileService {

    private final Path baseFileStore;

    public FileService() {
        try {
            UUID random = UUID.randomUUID();
            baseFileStore = Files.createTempDirectory("http_buffer_files_" + random.toString());
            baseFileStore.toFile().deleteOnExit();
        } catch (IOException exception) {
            throw new FileServiceException(
                    String.format( "Failed to create base file storage directory with error: %s.", exception.getMessage(), exception));
        }
    }

    public FileService(String path) {
        try {
            baseFileStore = Files.createDirectories(Paths.get(StringUtils.cleanPath(path)));
            baseFileStore.toFile().deleteOnExit();
        } catch (IOException exception) {
            throw new FileServiceException(
                    String.format( "Failed to create base file storage directory with error: %s.", exception.getMessage(), exception));
        }
    }

    /**
     * Creates a local temporary file in the base file store location, which is altogether marked for deletion
     * as soon as the jvm exists. (On app termination).
     *
     * Makes an HTTP GET connection to the provided URL, using NIO, and copies the contents of the URL to the file.
     * Returns a future, containing the file.
     *
     * On error will return a failed future.
     *
     * @param url {@code String} - The url to call to to download the file from.
     * @return {@code CompletableFuture<File>} - The file, containing the response content from the http call.*
     */
    public CompletableFuture<File> getFile(String url) {

        try {
            File data = File.createTempFile("httpget", "response", baseFileStore.toFile());

            try(FileOutputStream fileOutputStream = new FileOutputStream(data)) {
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
                FileChannel fileChannel = fileOutputStream.getChannel();
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            }

            return CompletableFuture.completedFuture(data);
        } catch (IOException exception) {
            throw new FileServiceException(String.format("Failed to download file from URL: [ %s ] with error: [ %s ]",
                                                         url, exception.getMessage(), exception));
        }
    }

    /**
     * Creates a local temporary file in the base file store localtion, which is altogether marked for deletion
     * as soon as the jvm exists. (On app termination).
     *
     * Handles a Multipart file uploaded, using NIO, and copies the contents of the Multipart file to the local store.
     * Returns a future, containing the file.
     *
     * On error will return a failed future.
     *
     * @param file {@code MultipartFile} - The multipart file to read from.
     * @return {@code CompletableFuture<File>} - The local file, that represents the stored file from the MultipartFile request.
     *
     */
    public CompletableFuture<File> getMultiPartFile(MultipartFile file) {
        if (file == null || file.isEmpty() ) {
            return CompletableFuture.failedFuture(
                    new FileNotFoundException("File upload to local storage has failed. No file found."));
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try(InputStream fileInputStream = file.getInputStream()) {
            Path localFilePath = this.baseFileStore.resolve(fileName);
            Files.copy(fileInputStream, localFilePath, StandardCopyOption.REPLACE_EXISTING);

            return CompletableFuture.completedFuture(localFilePath.toFile());
        } catch (IOException exception) {
            throw new FileServiceException(String.format("Failed to process Multipart File upload for File: [ %s ] with error: [ %s ]",
                                                         file.getOriginalFilename(), exception.getMessage(), exception));
        }
    }
}
