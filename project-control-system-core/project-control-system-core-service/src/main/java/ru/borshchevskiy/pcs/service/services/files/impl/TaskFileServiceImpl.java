package ru.borshchevskiy.pcs.service.services.files.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.borshchevskiy.pcs.common.exceptions.FileUploadException;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.task.attachment.TaskAttachmentDto;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.task.attachment.TaskAttachment;
import ru.borshchevskiy.pcs.repository.task.TaskAttachmentRepository;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;
import ru.borshchevskiy.pcs.service.services.files.TaskFileService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskFileServiceImpl extends AbstractFileService implements TaskFileService {

    @Value("${app.files.upload.bucket?:files}")
    private String bucket;

    private final TaskRepository taskRepository;
    private final TaskAttachmentRepository taskAttachmentRepository;

    @Override
    @Transactional(readOnly = true)
    public TaskAttachmentDto uploadTaskFile(Long taskId, InputStream content, long fileSize, String originalFilename) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task with id=" + taskId + " not found!"));

        // Строим путь для сохранения файла на диске
        String projectCode = task.getProject().getCode();
        String filePath = bucket + "/tasks/uploads/project_" + projectCode + "/" + "task_" + task.getId() + "/";
        Path fullFilePath = Path.of(filePath, originalFilename);

        try {
            Files.createDirectories(fullFilePath.getParent());
        } catch (IOException e) {
            log.error("File " + filePath + " was not uploaded.", e);
            throw new FileUploadException("File was not uploaded.", e);
        }

        // Сохраняем файл и получаем путь к нему
        Path savedFilePath = uploadFile(fullFilePath, content);

        // Подготавливаем ответ с актуальной информацией о сохраненном файле
        TaskAttachmentDto attachmentDto = new TaskAttachmentDto();

        try {
            attachmentDto.setId(taskAttachmentRepository.findByTaskIdAndFilename(taskId, originalFilename)
                    .map(TaskAttachment::getId)
                    .orElse(null));
            attachmentDto.setPath(savedFilePath.getParent().toString());
            attachmentDto.setFilename(savedFilePath.getFileName().toString());
            attachmentDto.setSize(Files.size(savedFilePath));
            attachmentDto.setTaskId(taskId);
        } catch (IOException e) {
            log.error("Can't determine size for uploaded file " + filePath + ".", e);
            throw new FileUploadException("File was uploaded with error. Try again.", e);
        }

        return attachmentDto;
    }

    @Override
    public StreamingResponseBody downloadTaskFile(TaskAttachmentDto dto) {
        Path filePath = Path.of(dto.getPath(), dto.getFilename());
        return downloadFile(filePath);
    }

    @Override
    public void deleteTaskFile(TaskAttachmentDto dto) {
        Path filePath = Path.of(dto.getPath(), dto.getFilename());
        deleteFile(filePath);
    }

    @Override
    @Transactional
    public void deleteAllFiles(Task task) {
        String filePathString = bucket + "/tasks/uploads/project_" + task.getProject().getCode() + "/" + "task_" + task.getId() + "/";
        Path filePath = Path.of(filePathString);
        deleteFiles(filePath);
    }

    // Метод возвращает мапу с записями Имя файла:Путь на скачивание файла
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getTaskFileList(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));

        Map<String, String> files = new HashMap<>();
        String downloadLink = "/api/v1/tasks/" + id + "/files/download/";


        String projectCode = task.getProject().getCode();
        String filePathString = bucket + "/tasks/uploads/project_" + projectCode + "/" + "task_" + task.getId() + "/";
        File taskFilesDir = Path.of(filePathString).toFile();

        if (!taskFilesDir.exists()) {
            return files;
        }

        String[] fileList = taskFilesDir.list();

        if (fileList == null) {
            return files;
        }

        for (String fileName : fileList) {
            files.put(fileName, downloadLink + fileName);
        }

        return files;
    }
}
