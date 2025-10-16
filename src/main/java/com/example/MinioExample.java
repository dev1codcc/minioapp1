package com.example;

import io.minio.errors.MinioException;

public class MinioExample {
    private static final String endpoint = "http://localhost:9000";
    private static final String accessKey = "minioadmin";
    private static final String secretKey = "minioadmin";
    private static final String bucketName = "drpfiles";

    public static void main(String[] args) {
        try {
            // Create a bucket manager instance
            MinioBucketManager bucketManager = new MinioBucketManager(endpoint, accessKey, secretKey, bucketName);

            // Ensure bucket exists
            bucketManager.ensureBucketExists();

            // Example usage of all functions
            String testFile = "7-2610174x10_hanspub.png";
            
            // Upload file
            bucketManager.uploadFile(testFile, testFile);
            
            // List files
            System.out.println("\nListing bucket contents:");
            bucketManager.listContents();
            
            // Download file
            String downloadedFile = "downloaded_" + testFile;
            bucketManager.downloadFile(testFile, downloadedFile);
            
            // Delete file
            bucketManager.deleteFile(testFile);
            
            System.out.println("\nAfter deletion - listing bucket contents:");
            bucketManager.listContents();
            
        } catch (MinioException e) {
            System.err.println("MinIO error: " + e);
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }
}
