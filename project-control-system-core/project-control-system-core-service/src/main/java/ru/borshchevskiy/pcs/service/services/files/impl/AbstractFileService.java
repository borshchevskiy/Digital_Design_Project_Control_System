package ru.borshchevskiy.pcs.service.services.files.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.borshchevskiy.pcs.common.exceptions.FileDeleteException;
import ru.borshchevskiy.pcs.common.exceptions.FileUploadException;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.service.services.files.FileService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public abstract class AbstractFileService implements FileService {

    @Override
    public StreamingResponseBody downloadFile(Path filePath) {
        return outputStream -> {
            InputStream inputStream = new FileInputStream(filePath.toFile());

            FileCopyUtils.copy(inputStream, outputStream);
        };
    }

    @Override
    public Path uploadFile(Path filePath, InputStream content) {

        try (content; OutputStream output = Files.newOutputStream(filePath)) {

            FileCopyUtils.copy(content, output);

        } catch (IOException e) {
            log.error("File " + filePath + " was not uploaded.", e);
            throw new FileUploadException("File was not uploaded.", e);
        }

        return filePath;
    }

    @Override
    public void deleteFile(Path filePath) {
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                log.error("File " + filePath + " was not deleted.", e);
                throw new FileDeleteException("File was not deleted.", e);
            }
        } else {
            throw new NotFoundException("File not found");
        }
    }

    @Override
    public void deleteFiles(Path filePath) {
        try {
            FileSystemUtils.deleteRecursively(filePath);
        } catch (IOException e) {
            log.error("File " + filePath + " was not deleted.", e);
            throw new FileDeleteException("File was not deleted.", e);
        }
    }
}
