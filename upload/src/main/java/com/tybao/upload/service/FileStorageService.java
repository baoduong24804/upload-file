package com.tybao.upload.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public Path getFileStorageLocation() {
        return this.fileStorageLocation;
    }

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        try {

            String UUID = uniqueIDCreate(file.getOriginalFilename());
            String UUIDImageName = UUID + file.getOriginalFilename();
            Path targetLocation = this.fileStorageLocation.resolve(UUIDImageName);
            System.out.println(targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.getFileName().toString();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!",
                    ex);
        }
    }

    public void deleteFile(String fileName) throws IOException {
        Path filePath = fileStorageLocation.resolve(fileName);
        Files.deleteIfExists(filePath); // Xóa file nếu tồn tại
    }

    public void deleteAllFiles() throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fileStorageLocation)) {
            for (Path path : directoryStream) {
                Files.delete(path); // Xóa từng file trong thư mục
            }
        }
    }

    public String uniqueIDCreate(String fileUploadName) {
        // Tạo ID duy nhất cho file
        String uniqueID = UUID.randomUUID().toString() + "_";
        for (int i = 0; i < 99; i++) {
            if (checkExistFile(uniqueID + fileUploadName)) {
                // neu ton tai file
                uniqueID = UUID.randomUUID().toString() + "_";
            } else {
                break;
            }
        }
        return uniqueID;
    }

    public Boolean checkExistFile(String fileName) {
        if (loadFile(fileName) == null) {
            return false;
        }
        // Ton tai file
        return true;
    }

    public Path loadFile(String fileName) {
        return fileStorageLocation.resolve(fileName).normalize();
    }
}
