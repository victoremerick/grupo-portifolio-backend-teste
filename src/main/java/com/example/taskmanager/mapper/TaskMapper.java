package com.example.taskmanager.mapper;

import com.example.taskmanager.domain.Task;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;

/**
 * Utility class for converting between {@link Task} entities and DTOs.
 */
public final class TaskMapper {

    private TaskMapper() {
        // Utility class
    }

    public static Task toEntity(TaskRequest request) {
        if (request == null) {
            return null;
        }
        Task task = new Task();
        task.setTitulo(request.getTitulo());
        task.setDescricao(request.getDescricao());
        task.setStatus(request.getStatus());
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitulo(task.getTitulo());
        response.setDescricao(task.getDescricao());
        response.setStatus(task.getStatus());
        response.setDataCriacao(task.getDataCriacao());
        response.setDataAtualizacao(task.getDataAtualizacao());
        response.setUsuarioAtualizacao(task.getUsuarioAtualizacao());
        return response;
    }
}
