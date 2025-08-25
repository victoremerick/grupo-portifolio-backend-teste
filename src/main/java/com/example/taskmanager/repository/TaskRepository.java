package com.example.taskmanager.repository;

import com.example.taskmanager.domain.StatusTarefa;
import com.example.taskmanager.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for managing {@link Task} entities.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Returns a paged list of tasks filtered by status when provided.
     *
     * @param status   the task status to filter by; if {@code null}, all tasks are returned
     * @param pageable pagination and sorting information
     * @return a page of tasks
     */
    @Query("SELECT t FROM Task t WHERE (:status IS NULL OR t.status = :status)")
    Page<Task> findByStatus(@Param("status") StatusTarefa status, Pageable pageable);
}
