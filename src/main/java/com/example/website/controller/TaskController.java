package com.example.website.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.website.dto.TaskRequest;
import com.example.website.dto.TaskResponse;
import com.example.website.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("")
    public ResponseEntity<String> addTask(Authentication authentication, @RequestBody TaskRequest request) {
        Long userId = (Long) authentication.getCredentials();
        taskService.addNewTask(userId, request);
        return ResponseEntity.ok("Task added successfully");
    }

    @GetMapping("/{registrationId}")
    public ResponseEntity<List<TaskResponse>> getTasksByUser(Authentication authentication) {
        Long userId = (Long) authentication.getCredentials();
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
    	taskService.deleteTask(taskId);
    	return ResponseEntity.ok("Task Deleted Successfully");
    }
}
