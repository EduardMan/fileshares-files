package tech.itparklessons.fileshares.files.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.itparklessons.fileshares.files.client.SocialClient;
import tech.itparklessons.fileshares.files.model.User;
import tech.itparklessons.fileshares.files.model.dto.FilesharesSocialFile;
import tech.itparklessons.fileshares.files.model.entity.FilesharesFilesFile;
import tech.itparklessons.fileshares.files.repository.FileRepository;
import tech.itparklessons.fileshares.files.service.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${application.storage-path}")
    private String storagePath;

    private final FileRepository fileRepository;
    private final SocialClient socialClient;

    @Override
    public UUID upload(MultipartFile multipartFile, User user) throws IOException {
        String fullFileName = getFullStoragePath() + multipartFile.getOriginalFilename();

        multipartFile.transferTo(Path.of(fullFileName));

        FilesharesFilesFile filesharesFilesFile = new FilesharesFilesFile();
        filesharesFilesFile.setName(multipartFile.getOriginalFilename());
        filesharesFilesFile.setPath(fullFileName);
        filesharesFilesFile.setOwnerId(user.getId());
        filesharesFilesFile.setExtension(getExtension(multipartFile.getOriginalFilename()));
        filesharesFilesFile.setSize(multipartFile.getSize());
        FilesharesFilesFile save = fileRepository.save(filesharesFilesFile);

        return save.getUuid();
    }

    private String getFullStoragePath() {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        int year = ldt.getYear();
        int month = ldt.getMonthValue();
        int dayOfMonth = ldt.getDayOfMonth();

        return storagePath + year + "/" + month + "/" + dayOfMonth + "/";
    }

    private String getExtension(String fileName) {
        Pattern pattern = Pattern.compile("(\\.[^.]*)$", Pattern.CASE_INSENSITIVE);

        if (pattern.matcher(fileName).find()) {
            return pattern.matcher(fileName).group(0).substring(1);
        }

        return null;
    }

    @Override
    public boolean checkOwner(List<UUID> fileId, Long userId) {
        return fileRepository.existsByOwnerIdAndUuidIn(userId, fileId);
    }

    @Override
    public List<FilesharesFilesFile> getAllOwnerFiles(Long userId) {
        return fileRepository.findAllByOwnerId(userId);
    }

    @Override
    public File getFile(UUID fileUUID) {
        FilesharesFilesFile filesharesFilesFile = fileRepository.findByUuid(fileUUID);
        String path = filesharesFilesFile.getPath();
        return new File(path);
    }

    @Override
    public File getFile(UUID fileUUID, User user) {
        boolean isAccessible = socialClient.checkAccess(fileUUID);
        if (isAccessible) {
            FilesharesFilesFile filesharesFilesFile = fileRepository.findByUuid(fileUUID);
            String path = filesharesFilesFile.getPath();
            return new File(path);
        }

        throw new RuntimeException("");
    }

    @Override
    public File getFile(String shareLink, User user) {
        FilesharesSocialFile filesharesSocialFile = socialClient.getFilesharesSocialFile(shareLink);

        if (filesharesSocialFile != null) {
            FilesharesFilesFile filesharesFilesFile = fileRepository.findByUuid(filesharesSocialFile.getFilesServiceFileUUID());
            String path = filesharesFilesFile.getPath();
            return new File(path);
        }

        return null;
    }
}