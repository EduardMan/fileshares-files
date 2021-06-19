package tech.itparklessons.fileshares.files.model.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity
public class FilesharesFilesFile {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalName;

    private String extension;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Long ownerId;

    private boolean deleted;
}