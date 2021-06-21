package tech.itparklessons.fileshares.files.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.itparklessons.fileshares.files.service.FileService;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Secured("ROLE_BACK")
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
    public File getFile(@RequestParam UUID fileUUID) {
        return fileService.getFile(fileUUID);
    }
}
