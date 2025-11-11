package com.example.projectmanagement.repository;

import com.example.projectmanagement.enums.Priority;
import com.example.projectmanagement.enums.Status;
import com.example.projectmanagement.model.TaskEntity;
import com.example.projectmanagement.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    // Get all tasks of a user (across projects)
    List<TaskEntity> findByUser(UserEntity user);

    //  Search by title or description
    @Query("SELECT t FROM TaskEntity t WHERE t.user = :user AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<TaskEntity> searchTasks(@Param("user") UserEntity user, @Param("keyword") String keyword);

    //Filter by status or priority
    List<TaskEntity> findByUserAndStatus(UserEntity user, Status status);
    List<TaskEntity> findByUserAndPriority(UserEntity user, Priority priority);


}
