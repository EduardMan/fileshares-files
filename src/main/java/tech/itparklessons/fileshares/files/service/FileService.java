package tech.itparklessons.fileshares.files.service;

import kotlin.Pair;
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

    Pair<String, File> getFile(UUID fileUUID);

    Pair<String, File> getFile(UUID fileUUID, User user);

    Pair<String, File> getFile(String shareLink, User user);

    void deleteFile(List<UUID> fileUUID, User user);
}
