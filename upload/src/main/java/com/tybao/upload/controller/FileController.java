package com.tybao.upload.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tybao.upload.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("files") List<MultipartFile> file) {
        StringBuilder s = new StringBuilder();
        if(file.size()<=0){
            return ResponseEntity.badRequest().body(null);
        }
        s.append("["+fileStorageService.getFileStorageLocation()+"]");
        for (MultipartFile multipartFile : file) {
            
            String fileName = fileStorageService.storeFile(multipartFile);
            s.append(fileName).append(",");
        }
        
        return ResponseEntity.ok("File uploaded successfully: " + s);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            fileStorageService.deleteFile(fileName);
            return ResponseEntity.ok("File deleted successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to delete file: " + fileName);
        }
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<String> deleteAllFiles() {
        try {
            fileStorageService.deleteAllFiles();
            return ResponseEntity.ok("All files deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to delete all files.");
        }
    }



    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        Path filePath = fileStorageService.loadFile(fileName);
        Resource resource = new UrlResource(filePath.toUri());

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewImage(@PathVariable String fileName) throws IOException {
        Path filePath = fileStorageService.loadFile(fileName);
        Resource resource = new UrlResource(filePath.toUri());

        String contentType = Files.probeContentType(filePath);
        System.out.println(contentType);
        if (contentType == null || !contentType.startsWith("image")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/view/all")
    public ResponseEntity<List<String>> getAllFiles() {
        try (Stream<Path> paths = Files.list(fileStorageService.getFileStorageLocation())) {
            List<String> fileNames = paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(fileNames);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
