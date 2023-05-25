package ru.borshchevskiy.pcs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.borshchevskiy.pcs.services.teammember.TeamMemberService;

@RequiredArgsConstructor
@RestController
public class TeamMemberController {

    private final TeamMemberService teamMemberService;
}
