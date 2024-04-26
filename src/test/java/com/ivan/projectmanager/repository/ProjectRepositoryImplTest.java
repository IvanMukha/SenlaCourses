package com.ivan.projectmanager.repository;

import com.ivan.projectmanager.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRepositoryConfiguration.class)
@WebAppConfiguration
public class ProjectRepositoryImplTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    public void testGetAll() {
        List<Project> projects = projectRepository.getAll();
        assertThat(projects).isNotEmpty();
    }

    @Test
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    public void testGetById() {
        Optional<Project> project = projectRepository.getById(1L);
        assertTrue(project.isPresent());
        assertEquals("Project ABC", project.get().getTitle());
    }

    @Test
    @DirtiesContext
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteProject() {
        Optional<Project> project = projectRepository.getById(1L);
        assertTrue(project.isPresent());

        projectRepository.delete(1L);
        assertFalse(projectRepository.getById(1L).isPresent());
    }

    @Test
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdate() {
        Optional<Project> project = projectRepository.getById(1L);
        assertTrue(project.isPresent());

        Project updatedProject = project.get();
        updatedProject.setTitle("Updated Project");
        projectRepository.update(1L, updatedProject);

        Optional<Project> updatedProjectOptional = projectRepository.getById(1L);
        assertTrue(updatedProjectOptional.isPresent());
        assertEquals("Updated Project", updatedProjectOptional.get().getTitle());
    }

    @Test
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testFindByStatusCriteria() {
        List<Project> foundProjects = projectRepository.findByStatusCriteria("Active");
        assertThat(foundProjects).isNotEmpty();
        assertThat(foundProjects).hasSize(1);
        assertThat(foundProjects.getFirst().getStatus()).isEqualTo("Active");
    }

    @Test
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    public void testFindByTitleJpql() {
        List<Project> foundProjects = projectRepository.findByTitleJpql("Project ABC");
        assertThat(foundProjects).isNotEmpty();
        assertThat(foundProjects).hasSize(1);
        assertThat(foundProjects.getFirst().getTitle()).isEqualTo("Project ABC");
    }

    @Test
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    public void testFindAllCriteriaFetch() {
        List<Project> foundProjects = projectRepository.findAllCriteriaFetch();
        assertThat(foundProjects).isNotEmpty();
        assertThat(foundProjects.getFirst().getTeam()).isNotNull();
    }

    @Test
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    public void testFindAllJpqlFetch() {
        List<Project> foundProjects = projectRepository.findAllJpqlFetch();
        assertThat(foundProjects).isNotEmpty();
        assertThat(foundProjects.getFirst().getTeam()).isNotNull();
    }

    @Test
    @Sql(scripts = {"classpath:data/projectrepositorytests/delete-projects.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql("classpath:data/projectrepositorytests/insert-projects.sql")
    public void testFindAllEntityGraphFetch() {
        List<Project> foundProjects = projectRepository.findAllEntityGraphFetch();
        assertThat(foundProjects).isNotEmpty();
        assertThat(foundProjects.getFirst().getTeam()).isNotNull();
    }
}
