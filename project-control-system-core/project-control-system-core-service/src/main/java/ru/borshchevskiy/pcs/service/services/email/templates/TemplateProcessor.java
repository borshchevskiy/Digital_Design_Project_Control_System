package ru.borshchevskiy.pcs.service.services.email.templates;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.borshchevskiy.pcs.entities.task.Task;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateProcessor {

    @Value("${spring.mail.templates.path}")
    private String newTaskTemplateLocation;
    private final SpringTemplateEngine templateEngine;


    public String prepareNewTaskTemplate(Task task) {
        Context context = new Context();
        Map<String, Object> templateContext = new HashMap<>();
        templateContext.put("firstname", task.getImplementer().getFirstname());
        templateContext.put("lastname", task.getImplementer().getLastname());
        templateContext.put("email", task.getImplementer().getEmail());
        templateContext.put("projectName", task.getProject().getName());
        templateContext.put("taskName", task.getName());
        templateContext.put("taskDescription", task.getDescription());
        templateContext.put("deadline", task.getDeadline().toLocalDate());
        templateContext.put("laborCosts", task.getLaborCosts());
        templateContext.put("authorName", task.getAuthor().getFirstname() + " " + task.getAuthor().getLastname());
        templateContext.put("taskId", task.getId());

        context.setVariables(templateContext);

        return templateEngine.process(newTaskTemplateLocation, context);
    }
}
