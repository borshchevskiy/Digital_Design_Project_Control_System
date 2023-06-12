package ru.borshchevskiy.pcs.repository.task;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.borshchevskiy.pcs.entities.task.attachment.TaskAttachment;

import java.util.Optional;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    Optional<TaskAttachment> findByTaskIdAndFilename(Long id, String filename);
}
