package com.ivan.projectmanager.controller;

import com.ivan.projectmanager.dto.CommentDTO;
import com.ivan.projectmanager.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/projects/{projectId}/tasks/{taskId}/comments")
@Validated
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<CommentDTO>> getAll(@PathVariable("projectId") Long projectId,
                                                   @PathVariable("taskId") Long taskId,
                                                   @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Page<CommentDTO> commentsPage = commentService.getAll(projectId, taskId, page, size);
        return ResponseEntity.ok().body(commentsPage);
    }


    @PostMapping()
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommentDTO> save(@PathVariable("projectId") Long projectId,
                                           @PathVariable("taskId") Long taskId,
                                           @RequestBody @Valid CommentDTO commentDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        CommentDTO savedComment = commentService.save(projectId, taskId, commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommentDTO> getById(@PathVariable("projectId") Long projectId,
                                              @PathVariable("taskId") Long taskId,
                                              @PathVariable("id") Long id) {
        Optional<CommentDTO> commentDTOOptional = commentService.getById(projectId, taskId, id);
        return commentDTOOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CommentDTO> update(@PathVariable("projectId") Long projectId,
                                             @PathVariable("taskId") Long taskId,
                                             @PathVariable("id") Long id,
                                             @RequestBody @Valid CommentDTO commentDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<CommentDTO> updatedComment = commentService.update(projectId, taskId, id, commentDTO);
        return updatedComment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("projectId") Long projectId,
                                       @PathVariable("taskId") Long taskId,
                                       @PathVariable("id") Long id) {
        commentService.delete(projectId, taskId, id);
        return ResponseEntity.noContent().build();
    }
}