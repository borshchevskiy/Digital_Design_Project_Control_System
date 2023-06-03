package ru.borshchevskiy.pcs.service.listeners;

import org.springframework.context.event.EventListener;
import ru.borshchevskiy.pcs.entities.task.Task;

public interface NewTaskListener {

    @EventListener
    void receiveNewTask(Task task);
}
