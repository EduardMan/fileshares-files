package tech.itparklessons.fileshares.files.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.itparklessons.fileshares.files.model.User;
import tech.itparklessons.fileshares.files.model.entity.FilesharesFilesFile;
import tech.itparklessons.fileshares.files.service.FileService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/file-storage")
@RequiredArgsConstructor
@RestController
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public UUID uploadFile(@RequestParam("file") MultipartFile multipartFile,
                           @AuthenticationPrincipal User user) throws IOException {
        return fileService.upload(multipartFile, user);
    }

    @GetMapping("/files")
    public List<FilesharesFilesFile> getAllOwnerFiles(@AuthenticationPrincipal User user) {
        return fileService.getAllOwnerFiles(user.getId());
    }

    @GetMapping("/checkOwner")
    public boolean checkOwner(@RequestParam List<UUID> fileUUID, @RequestParam Long userId) {
        return fileService.checkOwner(fileUUID, userId);
    }

    @Secured("ROLE_BACK")
    @GetMapping("/internal/getFile")
    public File getFile(@RequestParam UUID fileUUID) {
        return fileService.getFile(fileUUID);
    }

    @GetMapping("/getFile")
    public File getFile(@RequestParam UUID fileUUID,
                        @AuthenticationPrincipal User user) {
        return fileService.getFile(fileUUID, user);
    }

    @GetMapping("/getFile-by-share-link")
    public File getFile(@RequestParam String shareLink,
                        @AuthenticationPrincipal User user) {
        return fileService.getFile(shareLink, user);
    }
}