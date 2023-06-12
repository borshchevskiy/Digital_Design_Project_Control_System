package ru.borshchevskiy.pcs.service.services.task.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.borshchevskiy.pcs.common.exceptions.NotFoundException;
import ru.borshchevskiy.pcs.dto.task.attachment.TaskAttachmentDto;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.entities.task.attachment.TaskAttachment;
import ru.borshchevskiy.pcs.repository.task.TaskAttachmentRepository;
import ru.borshchevskiy.pcs.service.mappers.task.attachment.TaskAttachmentMapper;
import ru.borshchevskiy.pcs.service.services.task.TaskAttachmentService;


@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAttachmentServiceImpl implements TaskAttachmentService {

    private final TaskAttachmentRepository repository;
    private final TaskAttachmentMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public TaskAttachmentDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("File not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskAttachmentDto findByTaskIdAndFileName(Long id, String filename) {
        return repository.findByTaskIdAndFilename(id, filename)
                .map(mapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("File " + filename + " not found!"));
    }

    @Override
    @Transactional
    public TaskAttachmentDto save(TaskAttachmentDto dto) {

        return dto.getId() == null
                ? create(dto)
                : update(dto);

    }



    private TaskAttachmentDto create(TaskAttachmentDto dto) {

        TaskAttachment attachment = repository.save(mapper.createTaskAttachment(dto));

        log.debug("New attachment for task id=" + dto.getTaskId() + " added. " +
                  "File: " + dto.getFilename() + ". Path: " + dto.getPath());

        return mapper.mapToDto(attachment);
    }

    private TaskAttachmentDto update(TaskAttachmentDto dto) {
        TaskAttachment attachment = repository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("File mot found!"));

        attachment = mapper.mergeAttachment(attachment, dto);

        log.debug("Attachment for task id=" + dto.getTaskId() + " rewritten. " +
                  "File: " + dto.getFilename() + ". Path: " + dto.getPath());

        return mapper.mapToDto(repository.save(attachment));
    }


    @Override
    @Transactional
    public TaskAttachmentDto delete(TaskAttachmentDto attachmentDto) {
        TaskAttachment attachment = repository.findById(attachmentDto.getId())
                .map(a -> {
                    repository.delete(a);
                    return a;
                }).orElseThrow(() -> new NotFoundException("Attachment with id=" + attachmentDto.getId() + " not found!"));

        return mapper.mapToDto(attachment);
    }
}
