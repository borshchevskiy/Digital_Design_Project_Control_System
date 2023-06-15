package ru.borshchevskiy.pcs.service.services.files;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.nio.file.Path;

public interface FileService {

    StreamingResponseBody downloadFile(Path filePath);

    Path uploadFile(Path filePath, InputStream content);

    void deleteFile(Path filePath);

    void deleteFiles(Path filePath);
}
