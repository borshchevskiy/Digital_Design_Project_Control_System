package ru.borshchevskiy.pcs.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    private final String NEW_TASK_ROUTING_KEY = "app.task.new.#";

    @Bean
    public Queue newTaskQueue() {
        return new Queue("app.task.new", false);
    }

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange("app.task");
    }

    @Bean
    public Binding newTaskBinding() {
        return BindingBuilder.bind(newTaskQueue()).to(taskExchange()).with(NEW_TASK_ROUTING_KEY);
    }

}
