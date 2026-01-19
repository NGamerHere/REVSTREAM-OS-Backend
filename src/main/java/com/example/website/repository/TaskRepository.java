package com.example.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.website.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
