package ru.borshchevskiy.pcs.service.mappers.task.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.task.attachment.TaskAttachmentDto;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.entities.task.attachment.TaskAttachment;
import ru.borshchevskiy.pcs.repository.task.TaskRepository;

@Component
@RequiredArgsConstructor
public class TaskAttachmentMapper {

    private final TaskRepository taskRepository;

    public TaskAttachmentDto mapToDto(TaskAttachment taskAttachment) {
        TaskAttachmentDto attachmentDto = new TaskAttachmentDto();

        attachmentDto.setId(taskAttachment.getId());
        attachmentDto.setFilename(taskAttachment.getFilename());
        attachmentDto.setPath(taskAttachment.getPath());
        attachmentDto.setSize(taskAttachment.getSize());
        attachmentDto.setTaskId(taskAttachment.getTask().getId());

        return attachmentDto;
    }

    public TaskAttachment createTaskAttachment(TaskAttachmentDto dto) {
        TaskAttachment taskAttachment = new TaskAttachment();

        copyToTaskAttachment(taskAttachment, dto);

        return taskAttachment;
    }

    private Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id=" + id + " not found!"));
    }

    private TaskAttachment copyToTaskAttachment(TaskAttachment copyTo, TaskAttachmentDto copyFrom) {

        copyTo.setId(copyTo.getId());
        copyTo.setPath(copyFrom.getPath());
        copyTo.setFilename(copyFrom.getFilename());
        copyTo.setSize(copyFrom.getSize());
        copyTo.setTask(getTask(copyFrom.getTaskId()));

        return copyTo;
    }

    public TaskAttachment mergeAttachment(TaskAttachment attachment, TaskAttachmentDto dto) {
        return copyToTaskAttachment(attachment, dto);
    }
}
