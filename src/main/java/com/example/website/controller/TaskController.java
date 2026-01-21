package com.example.website.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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

    //  CREATE TASK for a specific user
    @PostMapping("/{registrationId}")
    public ResponseEntity<String> addTask(@PathVariable Long registrationId,
                                          @RequestBody TaskRequest request) {
        taskService.addNewTask(registrationId, request);
        return ResponseEntity.ok("Task added successfully");
    }

    //  GET TASKS for a specific user
    @GetMapping("/{registrationId}")
    public ResponseEntity<List<TaskResponse>> getTasksByUser(@PathVariable Long registrationId) {
        return ResponseEntity.ok(taskService.getTasksByUser(registrationId));
    }
    
    // Update Task
    
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId,
                                                   @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    
    // Delete Task 
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
    	taskService.deleteTask(taskId);
    	return ResponseEntity.ok("Task Deleted Successfully");
    }
}
