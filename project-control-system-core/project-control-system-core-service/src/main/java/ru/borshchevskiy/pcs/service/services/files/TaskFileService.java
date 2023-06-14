package ru.borshchevskiy.pcs.service.services.files;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.borshchevskiy.pcs.dto.task.attachment.TaskAttachmentDto;

import java.io.InputStream;
import java.util.Map;

public interface TaskFileService {

    TaskAttachmentDto uploadTaskFile(Long taskId, InputStream content, long fileSize, String originalFilename);

    StreamingResponseBody downloadTaskFile(TaskAttachmentDto dto);

    void deleteTaskFile(TaskAttachmentDto dto);

    Map<String, String> getTaskFileList(Long id);
}
