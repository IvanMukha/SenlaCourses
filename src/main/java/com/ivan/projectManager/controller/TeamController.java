package com.ivan.projectManager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan.projectManager.dto.TeamDTO;
import com.ivan.projectManager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class TeamController {
    private final TeamService teamService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TeamController(TeamService teamService, ObjectMapper objectMapper) {
        this.teamService = teamService;
        this.objectMapper = objectMapper;
    }

    public String getAll() throws JsonProcessingException {
        return objectMapper.writeValueAsString(teamService.getAll());
    }

    public String save(TeamDTO teamDTO) throws JsonProcessingException {
        return objectMapper.writeValueAsString(teamService.save(teamDTO));
    }

    public String getById(int id) throws JsonProcessingException {
        Optional<TeamDTO> teamDTOOptional = teamService.getById(id);
        return objectMapper.writeValueAsString(teamDTOOptional.orElse(null));
    }

    public String update(int id, TeamDTO teamDTO) throws JsonProcessingException {
        return objectMapper.writeValueAsString(teamService.update(id, teamDTO));
    }

    public void delete(int id) {
        teamService.delete(id);
    }
}