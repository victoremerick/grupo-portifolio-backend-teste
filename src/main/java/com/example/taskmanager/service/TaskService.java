package com.example.taskmanager.service;

import com.example.taskmanager.domain.StatusTarefa;
import com.example.taskmanager.domain.Task;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service for managing {@link Task} entities.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = TaskMapper.toEntity(request);
        task.setUsuarioAtualizacao(getCurrentUser());
        Task saved = taskRepository.save(task);
        return TaskMapper.toResponse(saved);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));
        task.setTitulo(request.getTitulo());
        task.setDescricao(request.getDescricao());
        task.setStatus(request.getStatus());
        task.setUsuarioAtualizacao(getCurrentUser());
        Task saved = taskRepository.save(task);
        return TaskMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));
        return TaskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> search(StatusTarefa status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable)
                .map(TaskMapper::toResponse);
    }

    private String getCurrentUser() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            String user = servletAttributes.getRequest().getHeader("X-User");
            if (user != null && !user.isBlank()) {
                return user;
            }
        }
        return "system";
    }
}
