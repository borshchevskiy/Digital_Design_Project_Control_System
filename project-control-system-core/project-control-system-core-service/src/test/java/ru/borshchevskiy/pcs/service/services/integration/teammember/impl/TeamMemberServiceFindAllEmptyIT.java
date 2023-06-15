package ru.borshchevskiy.pcs.service.services.integration.teammember.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import ru.borshchevskiy.pcs.dto.teammember.TeamMemberDto;
import ru.borshchevskiy.pcs.service.services.integration.IntegrationTestBase;
import ru.borshchevskiy.pcs.service.services.teammember.TeamMemberService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class TeamMemberServiceFindAllEmptyIT extends IntegrationTestBase {

    @Autowired
    private TeamMemberService teamMemberService;

    @Test
    void findEmptyList() {

        List<TeamMemberDto> all = teamMemberService.findAll();

        assertNotNull(all);
        assertThat(all).isEmpty();
    }

}

