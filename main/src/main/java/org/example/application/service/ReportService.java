package org.example.application.service;

import org.example.application.dto.ReportDTO;
import org.example.application.model.Report;
import org.example.application.repository.ReportRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService implements BaseService<Report, ReportDTO> {
    private final ModelMapper modelMapper;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ModelMapper modelMapper, ReportRepository reportRepository) {
        this.modelMapper = modelMapper;
        this.reportRepository = reportRepository;
    }

    public List<ReportDTO> getAll() {
        return reportRepository.getAll().stream().map(this::mapReportToDTO).collect(Collectors.toList());
    }

    public ReportDTO save(ReportDTO reportDTO) {
        return mapReportToDTO(reportRepository.save(mapDTOToReport(reportDTO)));
    }

    public Optional<ReportDTO> getById(int id) {
        Optional<Report> reportOptional = reportRepository.getById(id);
        return reportOptional.map(this::mapReportToDTO);
    }


    public Optional<ReportDTO> update(int id, ReportDTO updatedReportDTO) {
        Optional<Report> reportOptional = reportRepository.update(id, mapDTOToReport(updatedReportDTO));
        return reportOptional.map(this::mapReportToDTO);
    }

    public void delete(int id) {
        reportRepository.delete(id);
    }

    private Report mapDTOToReport(ReportDTO reportDTO) {
        return modelMapper.map(reportDTO, Report.class);
    }

    private ReportDTO mapReportToDTO(Report report) {
        return modelMapper.map(report, ReportDTO.class);
    }
}

