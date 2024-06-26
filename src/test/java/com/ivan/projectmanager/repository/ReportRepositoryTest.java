package com.ivan.projectmanager.repository;

import com.ivan.projectmanager.model.Report;
import com.ivan.projectmanager.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
public class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Test
    @Sql("classpath:data/reportrepositorytests/insert-reports.sql")
    public void testGetAll() {
        Page<Report> reportPage = reportRepository.getAll(1L, 1L, PageRequest.of(0, 10));
        assertThat(reportPage).isNotEmpty();
    }

    @Test
    @Sql("classpath:data/reportrepositorytests/insert-reports.sql")
    public void testGetById() {
        Optional<Report> report = reportRepository.getById(1L, 1L, 1L);

        assertTrue(report.isPresent());
        assertEquals("title", report.get().getTitle());
    }

    @Test
    @Sql("classpath:data/reportrepositorytests/insert-reports.sql")
    public void testDeleteReport() {
        Optional<Report> report = reportRepository.getById(1L, 1L, 1L);
        assertTrue(report.isPresent());

        reportRepository.delete(1L, 1L, 1L);
        assertFalse(reportRepository.getById(1L, 1L, 1L).isPresent());
    }

    @Test
    @Sql("classpath:data/reportrepositorytests/insert-reports.sql")
    public void testUpdate() {
        Optional<Report> report = reportRepository.getById(1L, 1L, 1L);
        assertTrue(report.isPresent());

        Report updatedReport = report.get();
        updatedReport.setTitle("Updated Report");
        reportRepository.update(1L, 1L, 1L, updatedReport);

        Optional<Report> updatedReportOptional = reportRepository.getById(1L, 1L, 1L);
        assertTrue(updatedReportOptional.isPresent());
        assertEquals("Updated Report", updatedReportOptional.get().getTitle());
    }

    @Test
    @Sql("classpath:data/reportrepositorytests/insert-reports.sql")
    public void testGetReportsByUser() {
        User user = new User();
        user.setId(1L);
        List<Report> foundReports = reportRepository.getReportsByUser(user);

        assertThat(foundReports).isNotEmpty();
        assertThat(foundReports).hasSize(1);
        assertThat(foundReports.getFirst().getUser().getId()).isEqualTo(1L);
    }
}
