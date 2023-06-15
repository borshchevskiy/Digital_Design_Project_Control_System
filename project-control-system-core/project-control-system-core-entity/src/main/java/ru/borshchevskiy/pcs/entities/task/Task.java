package ru.borshchevskiy.pcs.entities.task;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import ru.borshchevskiy.pcs.common.enums.TaskStatus;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.project.Project;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tasks")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implementer_id")
    private Employee implementer;

    @Column(name = "labor_costs")
    private Integer laborCosts;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Employee author;

    @CreationTimestamp
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;

    @ToString.Exclude
    @ManyToMany
    private List<Task> referencesTo = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany(mappedBy = "referencesTo")
    private List<Task> referencedBy = new ArrayList<>();

    public void addReference(Task task) {
        referencesTo.add(task);
        task.getReferencedBy().add(this);
    }

    public void removeReference(Task task) {
        referencesTo.remove(task);
        task.getReferencedBy().remove(this);
    }

}
