package com.example;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.Result;
import io.minio.messages.Item;
import io.minio.errors.MinioException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MinioBucketManager {
    private final MinioClient minioClient;
    private final String bucketName;

    public MinioBucketManager(String endpoint, String accessKey, String secretKey, String bucketName) throws MinioException {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
    }

    public void ensureBucketExists() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            System.out.println("Created bucket: " + bucketName);
        }
    }

    public void downloadFile(String objectName, String downloadFilePath) throws Exception {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
             java.io.FileOutputStream out = new java.io.FileOutputStream(downloadFilePath)) {
            byte[] buf = new byte[8192];
            int bytesRead;
            while ((bytesRead = stream.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            System.out.println("File downloaded successfully: " + downloadFilePath);
        }
    }

    public void uploadFile(String objectName, String filePath) throws Exception {
        Path path = Paths.get(filePath);
        try (InputStream stream = Files.newInputStream(path)) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(stream, Files.size(path), -1)
                    .contentType(Files.probeContentType(path))
                    .build());
            System.out.println("File uploaded successfully: " + objectName);
        }
    }

    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
        System.out.println("File deleted successfully: " + objectName);
    }

    public void listContents() throws Exception {
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .recursive(true)
                .build());
        
        System.out.println("Objects in bucket '" + bucketName + "':");
        for (Result<Item> result : results) {
            Item item = result.get();
            System.out.printf("- %s (size: %d bytes)%n", item.objectName(), item.size());
        }
    }
}
