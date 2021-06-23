package tech.itparklessons.fileshares.files.controller;

import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.itparklessons.fileshares.files.model.User;
import tech.itparklessons.fileshares.files.model.entity.FilesharesFilesFile;
import tech.itparklessons.fileshares.files.service.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/files")
@RequiredArgsConstructor
@RestController
public class FileController {
    private final FileService fileService;

    @GetMapping
    public List<FilesharesFilesFile> getAllOwnerFiles(@AuthenticationPrincipal User user) {
        return fileService.getAllOwnerFiles(user.getId());
    }

    @PostMapping("/upload")
    public UUID uploadFile(@RequestParam("file") MultipartFile multipartFile,
                           @AuthenticationPrincipal User user) throws IOException {
        return fileService.upload(multipartFile, user);
    }

    @GetMapping(value = "/getFile")
    public ResponseEntity<Resource> getFile(@RequestParam UUID fileUUID,
                                            @AuthenticationPrincipal User user) throws IOException {

        Pair<String, File> fileServiceFile = fileService.getFile(fileUUID, user);
        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(fileServiceFile.component2()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileServiceFile.component1() + "\"")
                .body(inputStreamResource);
    }

    @GetMapping("/getFileByShareLink")
    public ResponseEntity<Resource> getFile(@RequestParam String shareLink,
                        @AuthenticationPrincipal User user) throws FileNotFoundException {
        Pair<String, File> fileServiceFile = fileService.getFile(shareLink, user);
        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(fileServiceFile.component2()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileServiceFile.component1() + "\"")
                .body(inputStreamResource);
    }

    @PostMapping("/deleteFile")
    public void deleteFile(@RequestParam List<UUID> fileUUID,
                           @AuthenticationPrincipal User user) {
        fileService.deleteFile(fileUUID, user);
    }
}