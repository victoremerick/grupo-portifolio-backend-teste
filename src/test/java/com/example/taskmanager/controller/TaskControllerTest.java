package com.example.taskmanager.controller;

import com.example.taskmanager.domain.StatusTarefa;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void shouldCreateTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitulo("Task");
        request.setDescricao("Desc");
        request.setStatus(StatusTarefa.PENDENTE);

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitulo("Task");
        response.setDescricao("Desc");
        response.setStatus(StatusTarefa.PENDENTE);

        when(taskService.create(any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Task"));
    }

    @Test
    void shouldValidateTituloMandatory() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitulo(" "); // blank
        request.setDescricao("Desc");
        request.setStatus(StatusTarefa.PENDENTE);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("titulo")));
    }

    @Test
    void shouldHandlePaginationAndSorting() throws Exception {
        TaskResponse t1 = new TaskResponse();
        t1.setId(1L);
        t1.setTitulo("B");

        TaskResponse t2 = new TaskResponse();
        t2.setId(2L);
        t2.setTitulo("A");

        Page<TaskResponse> page = new PageImpl<>(List.of(t1, t2));
        when(taskService.search(isNull(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "titulo,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titulo").value("B"))
                .andExpect(jsonPath("$.content[1].titulo").value("A"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(taskService).search(isNull(), captor.capture());
        Pageable pageable = captor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(2, pageable.getPageSize());
        assertEquals("titulo", pageable.getSort().iterator().next().getProperty());
        assertEquals(org.springframework.data.domain.Sort.Direction.DESC, pageable.getSort().iterator().next().getDirection());
    }
}

