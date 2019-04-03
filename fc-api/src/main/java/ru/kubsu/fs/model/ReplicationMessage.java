package ru.kubsu.fs.model;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class ReplicationMessage {

    private ReplicationStatus status;
    private String fullName;
    private String serialNumber;
    private String eventDate;
    @Default
    private String message = "";

}
