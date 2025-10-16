package com.example;

import io.minio.errors.MinioException;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinioBucketManagerTest {
    private static final String endpoint = "http://localhost:9000";
    private static final String accessKey = "minioadmin";
    private static final String secretKey = "minioadmin";
    private static final String bucketName = "testbucket";
    private static final String TEST_FILES_DIR = "test_files";
    private static final String DOWNLOAD_DIR = "downloaded_files";
    private static final int NUMBER_OF_FILES = 50;
    private static final int CONTENT_LENGTH = 100;
    
    private static List<String> createdFiles = new ArrayList<>();
    private static Random random = new Random();

    public static void main(String[] args) {
        try {
            // Create test directories
            createDirectories();

            // Create and initialize MinioBucketManager
            MinioBucketManager bucketManager = new MinioBucketManager(endpoint, accessKey, secretKey, bucketName);
            bucketManager.ensureBucketExists();

            // Create and upload test files
            System.out.println("Creating and uploading test files...");
            createAndUploadFiles(bucketManager);

            // List bucket contents after upload
            System.out.println("\nListing bucket contents after upload:");
            bucketManager.listContents();

            // Download files
            System.out.println("\nDownloading files...");
            downloadFiles(bucketManager);

            // Verify downloads
            verifyDownloads();

            // Delete files from bucket
            System.out.println("\nDeleting files from bucket...");
            deleteFiles(bucketManager);

            // List bucket contents after deletion
            System.out.println("\nListing bucket contents after deletion:");
            bucketManager.listContents();

            // Clean up local test files
            cleanup();

            System.out.println("\nTest completed successfully!");

        } catch (MinioException e) {
            System.err.println("MinIO error: " + e);
        } catch (Exception e) {
            System.err.println("Error: " + e);
            e.printStackTrace();
        }
    }

    private static void createDirectories() throws Exception {
        Files.createDirectories(Paths.get(TEST_FILES_DIR));
        Files.createDirectories(Paths.get(DOWNLOAD_DIR));
        System.out.println("Created test directories: " + TEST_FILES_DIR + " and " + DOWNLOAD_DIR);
    }

    private static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static void createAndUploadFiles(MinioBucketManager bucketManager) throws Exception {
        for (int i = 0; i < NUMBER_OF_FILES; i++) {
            String fileName = generateRandomString(5) + ".txt";
            String filePath = TEST_FILES_DIR + File.separator + fileName;
            createdFiles.add(fileName);

            // Create file with random content
            String content = generateRandomString(CONTENT_LENGTH);
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(content);
            }

            // Upload file
            bucketManager.uploadFile(fileName, filePath);
            System.out.printf("Created and uploaded file %d/%d: %s%n", i + 1, NUMBER_OF_FILES, fileName);
        }
    }

    private static void downloadFiles(MinioBucketManager bucketManager) throws Exception {
        for (String fileName : createdFiles) {
            String downloadPath = DOWNLOAD_DIR + File.separator + fileName;
            bucketManager.downloadFile(fileName, downloadPath);
            System.out.println("Downloaded: " + fileName);
        }
    }

    private static void verifyDownloads() throws Exception {
        System.out.println("\nVerifying downloaded files...");
        int verified = 0;
        for (String fileName : createdFiles) {
            Path originalPath = Paths.get(TEST_FILES_DIR, fileName);
            Path downloadedPath = Paths.get(DOWNLOAD_DIR, fileName);
            
            if (Files.exists(downloadedPath)) {
                String originalContent = Files.readString(originalPath);
                String downloadedContent = Files.readString(downloadedPath);
                
                if (originalContent.equals(downloadedContent)) {
                    verified++;
                } else {
                    System.out.println("Content mismatch for file: " + fileName);
                }
            } else {
                System.out.println("Downloaded file not found: " + fileName);
            }
        }
        System.out.printf("Verified %d/%d files successfully%n", verified, NUMBER_OF_FILES);
    }

    private static void deleteFiles(MinioBucketManager bucketManager) throws Exception {
        for (String fileName : createdFiles) {
            bucketManager.deleteFile(fileName);
            System.out.println("Deleted from bucket: " + fileName);
        }
    }

    private static void cleanup() throws Exception {
        // Delete test directories and their contents
        deleteDirectory(Paths.get(TEST_FILES_DIR));
        deleteDirectory(Paths.get(DOWNLOAD_DIR));
        System.out.println("\nCleaned up test directories");
    }

    private static void deleteDirectory(Path path) throws Exception {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted((a, b) -> b.compareTo(a)) // Reverse order to delete files before directories
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (Exception e) {
                        System.err.println("Error deleting: " + file);
                    }
                });
        }
    }
}
