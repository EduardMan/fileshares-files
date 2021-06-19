package tech.itparklessons.fileshares.files.service;

import org.springframework.web.multipart.MultipartFile;
import tech.itparklessons.fileshares.files.model.User;
import tech.itparklessons.fileshares.files.model.entity.FilesharesFilesFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface FileService {
    UUID upload(MultipartFile multipartFile, User user) throws IOException;

    boolean checkOwner(List<UUID> fileUUID, Long userId);

    List<FilesharesFilesFile> getAllOwnerFiles(Long userId);

    File getFile(UUID fileUUID);

    File getFile(UUID fileUUID, User user);

    File getFile(String shareLink, User user);

    void deleteFile(List<UUID> fileUUID, User user);
}
