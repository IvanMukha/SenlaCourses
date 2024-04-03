package org.example.application.repository;

import org.example.application.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class ProjectRepository implements BaseRepository<Project> {
    private final Logger log = LoggerFactory.getLogger(ProjectRepository.class);
    List<Project> projects = new ArrayList<>();

    public List<Project> getAll() {
        return projects;
    }

    public Project save(Project project) {
        projects.add(project);
        return project;
    }

    public Optional<Project> getById(int id) {
        Optional<Project> optionalProject = projects.stream()
                .filter(project -> project.getId() == id)
                .findFirst();
        if (optionalProject.isEmpty()) {
            log.error("Object with id " + id + " does not exist");
        }
        return optionalProject;
    }

    public Optional<Project> update(int id, Project updatedProject) {
        Optional<Project> optionalProject = getById(id);
        optionalProject.ifPresent(project -> project.setTitle(updatedProject.getTitle()).setDescription(updatedProject.getDescription()).setStatus(
                        updatedProject.getStatus()).setTeamId(updatedProject.getTeamId()).
                setManagerId(updatedProject.getManagerId()));
        return optionalProject;
    }

    public void delete(int id) {
        projects.removeIf(project -> project.getId() == id);
    }
}
