package fileupload.fileupload.controller;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import fileupload.fileupload.message.ResponseMessage;
import fileupload.fileupload.model.FileInfo;
import fileupload.fileupload.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Controller
//@CrossOrigin("http://localhost:8081")
@Slf4j
public class FileController {
    @Autowired
    FileStorageService storageService;
    @GetMapping("/")
    public  String index()
    {
        return "upload";
    }
    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        String message = "";
        String transactionId= UUID.randomUUID().toString();
        try {
            List<String> fileNames = new ArrayList<>();
            Arrays.stream(files).forEach(file -> {
                storageService.save(file);
                fileNames.add(file.getOriginalFilename());
            });
            message = "Uploaded the files successfully: " + fileNames;
            log.info(Date.from(Instant.now()) + " " + transactionId + " " + message);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(transactionId, message));
        } catch (Exception e) {
            message = "Fail to upload files!";
            log.info(Date.from(Instant.now()) + " " + transactionId + " " + message);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(transactionId, message));
        }
    }
    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles(Model model) {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String id = UUID.randomUUID().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FileController.class, "getFile", path.getFileName().toString()).build().toString();
            return new FileInfo(id, filename, url);
        }).collect(Collectors.toList());
        //fileInfos.forEach(fileInfo -> model.addAttribute("file", fileInfo.getName()));
        model.addAttribute("fileList", fileInfos);
      // model.addAttribute("/files/file/{fileName}(fileName=${file.name})", "/files/file/{fileName}(fileName=${file.name})");
        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    @GetMapping("/files/file/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource file = storageService.load(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}