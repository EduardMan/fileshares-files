package tech.itparklessons.fileshares.files.controller;

import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.itparklessons.fileshares.files.service.FileService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/internal/files")
@RequiredArgsConstructor
@RestController
public class InternalFileController {
    private final FileService fileService;

    @GetMapping("/checkOwner")
    public boolean checkOwner(@RequestParam List<UUID> fileUUID, @RequestParam Long userId) {
        return fileService.checkOwner(fileUUID, userId);
    }

    @GetMapping("/getFile")
    public ResponseEntity<Resource> getFile(@RequestParam UUID fileUUID) throws FileNotFoundException {
        Pair<String, File> fileServiceFile = fileService.getFile(fileUUID);
        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(fileServiceFile.component2()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileServiceFile.component1() + "\"")
                .body(inputStreamResource);
    }
}