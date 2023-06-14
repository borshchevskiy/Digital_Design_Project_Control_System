package ru.borshchevskiy.pcs.entities.task.attachment;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.borshchevskiy.pcs.entities.task.Task;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "task_attachments")
public class TaskAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path")
    private String path;

    @Column(name = "filename")
    private String filename;

    @Column(name = "size")
    private Long size;

    @CreationTimestamp
    @Column(name = "date_uploaded")
    private LocalDateTime dateUploaded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Task task;
}
