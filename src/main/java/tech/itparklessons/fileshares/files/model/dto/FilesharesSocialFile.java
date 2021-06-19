package tech.itparklessons.fileshares.files.model.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
public class FilesharesSocialFile {
    private Long ownerId;
    private UUID filesServiceFileUUID;
}
