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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping("/api/files")
//@CrossOrigin(origins = {"https://congnghetoday.com", "https://congnghetoday.click", "https://anime404.click"}) 
@CrossOrigin(origins = {"*"})
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // @PostMapping("/upload")
    // public ResponseEntity<String> uploadFile(@RequestParam("files")
    // List<MultipartFile> file) {
    // StringBuilder s = new StringBuilder();
    // if(file.size()<=0){
    // return ResponseEntity.badRequest().body(null);
    // }
    // s.append("["+fileStorageService.getFileStorageLocation()+"]");
    // for (MultipartFile multipartFile : file) {

    // String fileName = fileStorageService.storeFile(multipartFile);
    // s.append(fileName).append(",");
    // }

    // return ResponseEntity.ok("File uploaded successfully: " + s);
    // }

    @GetMapping("/view/pdf/{fileName}")
    public ResponseEntity<Resource> viewPdf(@PathVariable String fileName) throws IOException {
        try {
            Path filePath = fileStorageService.loadFile(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            String contentType = Files.probeContentType(filePath);
            
            // Kiểm tra xem file có phải là PDF không
            if (contentType == null || !contentType.equals("application/pdf")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload")
public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("files") List<MultipartFile> files) {
    Map<String, Object> response = new HashMap<>();

    // Kiểm tra danh sách file có trống không
    if (files.isEmpty()) {
        response.put("message", "Không tìm thấy file, bạn quên hả? :>");
        response.put("status", false);
        return ResponseEntity.badRequest().body(response);
    }

    // Lấy vị trí lưu trữ file
    String location = "["+fileStorageService.getFileStorageLocation()+"]";
    response.put("location", location);

    try {
        // Xử lý lưu file bất đồng bộ
        List<CompletableFuture<String>> futures = files.stream()
                .map(fileStorageService::storeFile)
                .collect(Collectors.toList());

        // Chờ tất cả các tác vụ hoàn thành
        List<String> fileNames = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        String pdf = "https://anime404.click/api/files/view/pdf/";
        String other = "https://anime404.click/api/files/view/";
        // Gán kết quả vào phản hồi
        if (fileNames.size() == 1) {
            //System.out.println(fileNames.get(0).toString());
            if(fileNames.get(0).toString().endsWith(".pdf")){
                response.put("file", pdf+fileNames.get(0));
            }else{
                response.put("file", other+fileNames.get(0));
            }

        } else {
            Set<String> set = new HashSet<>();
            for (String string : fileNames) {
                //System.out.println(string);
                if(string.toString().endsWith(".pdf")){
                    set.add(pdf+string);
                }else{
                    set.add(other+string);
                }
                    
                
            }
            response.put("files", set);
        }

        response.put("status", true);
        response.put("message", "Tải file lên thành công!");
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        // Xử lý lỗi nếu xảy ra
        response.put("status", false);
        response.put("message", "Đã xảy ra lỗi trong quá trình tải file lên!");
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            fileStorageService.deleteFile(fileName);
            return ResponseEntity.ok("Đã xóa thành công file: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Xóa không thành công file: " + fileName);
        }
    }

    // @DeleteMapping("/delete-all")
    // public ResponseEntity<String> deleteAllFiles() {
    //     try {
    //         fileStorageService.deleteAllFiles();
    //         return ResponseEntity.ok("All files deleted successfully.");
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("Failed to delete all files.");
    //     }
    // }

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
