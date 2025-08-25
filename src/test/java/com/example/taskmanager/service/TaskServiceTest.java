package com.example.taskmanager.service;

import com.example.taskmanager.domain.StatusTarefa;
import com.example.taskmanager.domain.Task;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskRequest request;

    @BeforeEach
    void setUp() {
        request = new TaskRequest();
        request.setTitulo("Test Task");
        request.setDescricao("Desc");
        request.setStatus(StatusTarefa.PENDENTE);
    }

    @Test
    void createShouldSaveTask() {
        Task saved = new Task();
        saved.setId(1L);
        saved.setTitulo("Test Task");
        saved.setDescricao("Desc");
        saved.setStatus(StatusTarefa.PENDENTE);
        saved.setUsuarioAtualizacao("system");

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskResponse response = taskService.create(request);

        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitulo());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateShouldModifyExistingTask() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setTitulo("Old");
        existing.setDescricao("Old desc");
        existing.setStatus(StatusTarefa.PENDENTE);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskRequest update = new TaskRequest();
        update.setTitulo("New");
        update.setDescricao("New desc");
        update.setStatus(StatusTarefa.CONCLUIDA);

        TaskResponse response = taskService.update(1L, update);

        assertEquals("New", response.getTitulo());
        assertEquals(StatusTarefa.CONCLUIDA, response.getStatus());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(existing);
    }

    @Test
    void updateShouldThrowWhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.update(1L, request));
    }

    @Test
    void deleteShouldRemoveWhenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteShouldThrowWhenNotFound() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taskService.delete(1L));
    }

    @Test
    void findByIdShouldReturnTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitulo("Test Task");
        task.setStatus(StatusTarefa.PENDENTE);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.findById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitulo());
    }

    @Test
    void searchShouldReturnPagedResponses() {
        Task task = new Task();
        task.setId(1L);
        task.setTitulo("Test Task");
        task.setStatus(StatusTarefa.PENDENTE);

        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskRepository.findByStatus(eq(StatusTarefa.PENDENTE), any(Pageable.class))).thenReturn(page);

        Page<TaskResponse> result = taskService.search(StatusTarefa.PENDENTE, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Task", result.getContent().get(0).getTitulo());
    }
}

