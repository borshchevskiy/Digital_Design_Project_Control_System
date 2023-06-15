package ru.borshchevskiy.pcs.service.services.integration.team.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.dto.team.TeamDto;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.team.TeamService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TeamServiceFindAllEmptyIT extends IntegrationTestBase {

    @Autowired
    private TeamService teamService;

    @Test
    void findEmptyList() {

        List<TeamDto> all = teamService.findAll();

        assertNotNull(all);
        assertThat(all).isEmpty();
    }

}

