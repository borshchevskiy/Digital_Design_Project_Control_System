package ru.borshchevskiy.pcs.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.borshchevskiy.pcs.common.exceptions.RequestDataValidationException;
import ru.borshchevskiy.pcs.dto.task.TaskStatusDto;
import ru.borshchevskiy.pcs.dto.task.attachment.TaskAttachmentDto;
import ru.borshchevskiy.pcs.dto.task.filter.TaskFilter;
import ru.borshchevskiy.pcs.dto.task.reference.TaskReferenceDto;
import ru.borshchevskiy.pcs.dto.task.status.TaskDto;
import ru.borshchevskiy.pcs.service.services.files.TaskFileService;
import ru.borshchevskiy.pcs.service.services.task.TaskAttachmentService;
import ru.borshchevskiy.pcs.service.services.task.TaskService;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Задачи", description = "Управление задачами")
@SecurityRequirement(name = "Swagger auth")
@EnableAsync
public class TaskController {

    private final TaskService taskService;
    private final TaskFileService taskFileService;
    private final TaskAttachmentService taskAttachmentService;

    @Operation(summary = "Получение задачи", description = "Получение задачи по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public TaskDto getTask(@PathVariable Long id) {

        return taskService.findById(id);
    }

    @Operation(summary = "Получение задач", description = "Получение всех задач")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<TaskDto> getAll() {

        return taskService.findAll();
    }

    @PostMapping(value = "/filter", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<TaskDto> getAllByFilter(@RequestBody TaskFilter filter) {

        return taskService.findAllByFilter(filter);
    }

    @Operation(summary = "Создание задачи", description = "Создание новой задачи")
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public TaskDto createTask(@RequestBody TaskDto request) {
        System.out.println(request);
        return taskService.save(request);
    }

    @Operation(summary = "Изменение задачи", description = "Изменение задачи по id")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public TaskDto updateTask(@RequestBody TaskDto request) {

        return taskService.save(request);
    }

    @Operation(summary = "Удаление задачи", description = "Удаление задачи по id")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public TaskDto deleteTask(@PathVariable Long id) {

        return taskService.deleteById(id);
    }

    @Operation(summary = "Изменение статуса", description = "Изменение статуса задачи по id")
    @PostMapping(value = "/{id}/status", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public TaskDto updateStatus(@PathVariable Long id,
                                @RequestBody TaskStatusDto request) {

        return taskService.updateStatus(id, request);
    }

    @Operation(summary = "Добавление файла", description = "Добавление файла к задаче")
    @PostMapping(value = "/{taskId}/files/upload/{filename:.+}", produces = APPLICATION_JSON_VALUE)
    public TaskAttachmentDto uploadFile(@PathVariable Long taskId,
                                        @PathVariable String filename,
                                        InputStream fileContent,
                                        HttpServletRequest request) {

        long fileSize = Long.parseLong(request.getHeader("Content-Length"));

        if (fileSize == 0) {
            throw new RequestDataValidationException("No file found.");
        }

        // Сначала загружаем файл, если загрузка успешна, создаем объект taskAttachment с информацией о файле
        TaskAttachmentDto attachmentRequest = taskFileService.uploadTaskFile(taskId, fileContent, fileSize, filename);
        TaskAttachmentDto savedAttachment = taskAttachmentService.save(attachmentRequest);

        // В TaskAttachmentDto указан путь к файлу в файловой системе. Клиенту он не нужен,
        // меняем его на ссылку для скачивания файла и отправляем клиенту
        savedAttachment.setPath("/api/v1/tasks/" + taskId + "/files/download/" + savedAttachment.getFilename());

        return savedAttachment;

    }

    @Operation(summary = "Получение файла", description = "Получение файла прикрепленного к задаче")
    @GetMapping(value = "/{id}/files/download/{filename:.+}", produces = APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable Long id,
                                                              @PathVariable String filename) {

        TaskAttachmentDto attachmentDto = taskAttachmentService.findByTaskIdAndFileName(id, filename);

        StreamingResponseBody stream = taskFileService.downloadTaskFile(attachmentDto);

        return ResponseEntity
                .status(OK)
                .header("Content-Disposition", "attachment;filename=" + attachmentDto.getFilename())
                .body(stream);
    }

    @Operation(summary = "Получение списка файлов", description = "Получение списка файлов прикрепленных к задаче")
    @GetMapping(value = "/{id}/files", produces = APPLICATION_JSON_VALUE)
    public Map<String, String> getFileList(@PathVariable Long id) {

        return taskFileService.getTaskFileList(id);
    }

    @Operation(summary = "Получение списка файлов", description = "Получение списка файлов прикрепленных к задаче")
    @DeleteMapping(value = "/{id}/files/delete/{filename:.+}", produces = APPLICATION_JSON_VALUE)
    public TaskAttachmentDto deleteFile(@PathVariable Long id,
                                        @PathVariable String filename) {

        TaskAttachmentDto attachmentDto = taskAttachmentService.findByTaskIdAndFileName(id, filename);
        taskFileService.deleteTaskFile(attachmentDto);
        TaskAttachmentDto deletedAttachment = taskAttachmentService.delete(attachmentDto);
        deletedAttachment.setPath(null);

        return deletedAttachment;
    }


    @Operation(summary = "Добавить связанные задачи", description = "Добавление задач от которых зависит данная задача")
    @PostMapping(value = "/{id}/references/add", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<TaskDto> addReferences(@PathVariable Long id,
                                       @RequestBody TaskReferenceDto referenceIds) {

        return taskService.addReferences(id, referenceIds.referenceIds());


    }

    @Operation(summary = "Удалить связанные задачи", description = "Удаление задач от которых зависит данная задача")
    @PostMapping(value = "/{id}/references/remove", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<TaskDto> removeReferences(@PathVariable Long id,
                                          @RequestBody TaskReferenceDto referenceIds) {

        return taskService.removeReferences(id, referenceIds.referenceIds());

    }

    @Operation(summary = "Получить список зависимостей задачи", description = "Получение списка задач от которых зависит задача")
    @GetMapping(value = "/{id}/references", produces = APPLICATION_JSON_VALUE)
    public List<TaskDto> getReferences(@PathVariable Long id) {

        return taskService.getReferences(id);

    }
}
