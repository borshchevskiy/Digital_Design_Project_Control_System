package ru.borshchevskiy.pcs.service.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import ru.borshchevskiy.pcs.entities.task.Task;

public interface NewTaskRabbitListener {

    @RabbitListener(queues = "app.task.new")
    void receiveNewTask(Task task);
}
