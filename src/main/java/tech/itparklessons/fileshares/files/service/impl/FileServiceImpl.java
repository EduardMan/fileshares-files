package tech.itparklessons.fileshares.files.service.impl;

import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${application.storage-path}")
    private String storagePath;

    private final FileRepository fileRepository;
    private final SocialClient socialClient;
    private final RabbitTemplate template;

    @Override
    public UUID upload(MultipartFile multipartFile, User user) throws IOException {
        String fileName = RandomStringUtils.random(15, true, true);
        String extension = getExtension(multipartFile.getOriginalFilename());

        String fullFileName = getFullStoragePath() + fileName + "." + extension;
        File file = new File(getFullStoragePath());
        file.mkdirs();

        multipartFile.transferTo(Path.of(fullFileName));

        FilesharesFilesFile filesharesFilesFile = new FilesharesFilesFile();
        filesharesFilesFile.setOriginalName(multipartFile.getOriginalFilename());
        filesharesFilesFile.setFileName(fileName);
        filesharesFilesFile.setPath(getFullStoragePath());
        filesharesFilesFile.setOwnerId(user.getId());
        filesharesFilesFile.setExtension(extension);
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

        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(0).substring(1);
        }

        return null;
    }

    @Override
    public boolean checkOwner(List<UUID> fileId, Long userId) {
        return fileRepository.existsByOwnerIdAndUuidIn(userId, fileId);
    }

    @Override
    public List<FilesharesFilesFile> getAllOwnerFiles(Long userId) {
        return fileRepository.findAllByOwnerIdAndDeletedFalse(userId);
    }

    @Override
    public File getFile(UUID fileUUID) {
        FilesharesFilesFile filesharesFilesFile = fileRepository.findByUuid(fileUUID);
        String path = filesharesFilesFile.getPath();
        return new File(path);
    }

    @Override
    public Pair<String, File> getFile(UUID fileUUID, User user) {
        FilesharesFilesFile filesharesFilesFile = fileRepository.findByUuid(fileUUID);

        if (filesharesFilesFile.getOwnerId().equals(user.getId()) || socialClient.checkAccess(fileUUID)) {
            String fullFileName = filesharesFilesFile.getPath() + filesharesFilesFile.getFileName() + "." + filesharesFilesFile.getExtension();
            return new Pair<>(filesharesFilesFile.getOriginalName(), new File(fullFileName));
        }

        throw new RuntimeException("");
    }

    @Override
    public Pair<String, File> getFile(String shareLink, User user) {
        FilesharesSocialFile filesharesSocialFile = socialClient.getFilesharesSocialFile(shareLink);

        if (filesharesSocialFile != null) {
            FilesharesFilesFile filesharesFilesFile = fileRepository.findByUuid(filesharesSocialFile.getFilesServiceFileUUID());
            String fullFileName = filesharesFilesFile.getPath() + filesharesFilesFile.getFileName() + "." + filesharesFilesFile.getExtension();
            return new Pair<>(filesharesFilesFile.getOriginalName(), new File(fullFileName));
        }

        return null;
    }

    @Override
    public void deleteFile(List<UUID> fileUUID, User user) {
        List<FilesharesFilesFile> filesharesFilesFiles = fileRepository.findByUuidIn(fileUUID);
        boolean isAnyNotOwnedFile = filesharesFilesFiles.stream().anyMatch(filesharesFilesFile -> !filesharesFilesFile.getOwnerId().equals(user.getId()));
        if (isAnyNotOwnedFile) {
            return;
        }

        List<UUID> uuidsForDelete = new ArrayList<>();
        for (FilesharesFilesFile filesharesFilesFile : filesharesFilesFiles) {
            filesharesFilesFile.setDeleted(true);

            File file = new File(filesharesFilesFile.getPath() + filesharesFilesFile.getFileName() + "." + filesharesFilesFile.getExtension());
            if (file.delete()) {
                uuidsForDelete.add(filesharesFilesFile.getUuid());
            }
        }
        fileRepository.saveAll(filesharesFilesFiles);
        template.convertAndSend("fileshares", "files-deleted-social-queue", uuidsForDelete);
        template.convertAndSend("fileshares", "files-deleted-archiver-queue", uuidsForDelete);
    }
}