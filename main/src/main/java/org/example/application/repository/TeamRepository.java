package org.example.application.repository;

import org.example.application.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepository implements BaseRepository<Team> {
    private final Logger log = LoggerFactory.getLogger(TeamRepository.class);
    List<Team> teams = new ArrayList<>();

    public List<Team> getAll() {
        return teams;
    }

    public Team save(Team team) {
        teams.add(team);
        return team;
    }

    public Optional<Team> getById(int id) {
        Optional<Team> optionalTeam = teams.stream()
                .filter(team -> team.getId() == id)
                .findFirst();
        if (optionalTeam.isEmpty()) {
            log.error("Object with id " + id + " does not exist");
        }
        return optionalTeam;
    }


    public Optional<Team> update(int id, Team updatedTeam) {
        Optional<Team> optionalTeam = getById(id);
        optionalTeam.ifPresent(team -> team.setName(updatedTeam.getName()));
        return optionalTeam;
    }

    public void delete(int id) {
        teams.removeIf(team -> team.getId() == id);
    }
}
