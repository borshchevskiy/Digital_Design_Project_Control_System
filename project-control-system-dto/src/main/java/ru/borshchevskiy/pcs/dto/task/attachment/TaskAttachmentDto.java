package ru.borshchevskiy.pcs.dto.task.attachment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Приложение к задаче")
public class TaskAttachmentDto {
    @Schema(description = "id")
    private Long id;
    @Schema(description = "Путь к файлу")
    private String path;
    @Schema(description = "Имя файла")
    private String filename;
    @Schema(description = "Размер в байтах")
    private Long size;
    @Schema(description = "дата загрузки")
    private LocalDateTime dateUploaded;
    @Schema(description = "id задачи")
    private Long taskId;

}
