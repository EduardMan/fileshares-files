package tech.itparklessons.fileshares.files.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.itparklessons.fileshares.files.model.entity.FilesharesFilesFile;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FilesharesFilesFile, UUID> {
    boolean existsByOwnerIdAndUuidIn(Long ownerId, List<UUID> fileUUID);

    List<FilesharesFilesFile> findAllByOwnerId(Long ownerId);

    FilesharesFilesFile findByUuid(UUID fileUUID);

    List<FilesharesFilesFile> findByUuidIn(List<UUID> fileUUID);
}