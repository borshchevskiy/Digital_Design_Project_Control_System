package ru.borshchevskiy.pcs.service.services.integration.project.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.project.ProjectService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class ProjectServiceFindAllEmptyIT extends IntegrationTestBase {

    @Autowired
    private ProjectService projectService;

    @Test
    void findEmptyList() {

        List<ProjectDto> all = projectService.findAll();

        assertNotNull(all);
        assertThat(all).isEmpty();
    }

}

