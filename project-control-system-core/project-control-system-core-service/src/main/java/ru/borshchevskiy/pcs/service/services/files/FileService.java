package ru.borshchevskiy.pcs.service.services.files;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.borshchevskiy.pcs.dto.task.attachment.TaskAttachmentDto;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface FileService {

    StreamingResponseBody downloadFile(Path filePath);

    Path uploadFile(Path filePath, InputStream content);

    void deleteFile(Path filePath);
}
