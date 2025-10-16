# MinIO Java Example Project

This project demonstrates how to use the MinIO Java SDK to interact with a MinIO object storage server. It includes basic examples for connecting, uploading, and downloading objects.

## Features
- Connect to a MinIO server
- Upload an object
- Download an object

## Requirements
- Java 11 or higher
- Maven
- Access to a running MinIO server

## Getting Started

1. Clone this repository or copy the project files.
2. Update the MinIO endpoint, access key, and secret key in the source code as needed.
3. Build the project:
   ```sh
   mvn clean package
   ```
4. Run the example:
   ```sh
   mvn exec:java -Dexec.mainClass="com.example.MinioExample"
   ```

## Dependencies
- [MinIO Java SDK](https://github.com/minio/minio-java)

## License
This project is licensed under the MIT License.
