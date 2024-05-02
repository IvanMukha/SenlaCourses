package com.ivan.projectmanager.service;

import com.ivan.projectmanager.dto.AttachmentDTO;

import java.util.List;
import java.util.Optional;

public interface AttachmentService {
    List<AttachmentDTO> getAll(Long projectId, Long taskId);

    AttachmentDTO save(Long projectId, Long taskId, AttachmentDTO attachmentDTO);

    Optional<AttachmentDTO> getById(Long projectId, Long taskId, Long id);

    Optional<AttachmentDTO> update(Long projectId, Long taskId, Long id, AttachmentDTO updatedAttachmentDTO);

    void delete(Long projectId, Long taskId, Long id);
}
