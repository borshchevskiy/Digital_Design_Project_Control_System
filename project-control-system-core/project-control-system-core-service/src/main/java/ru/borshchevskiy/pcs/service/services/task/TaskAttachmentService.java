package ru.borshchevskiy.pcs.service.services.task;


import ru.borshchevskiy.pcs.dto.task.attachment.TaskAttachmentDto;

public interface TaskAttachmentService {


    TaskAttachmentDto findById(Long id);

    TaskAttachmentDto findByTaskIdAndFileName(Long id, String filename);

    TaskAttachmentDto save(TaskAttachmentDto dto);

    TaskAttachmentDto delete(TaskAttachmentDto attachmentDto);
}
