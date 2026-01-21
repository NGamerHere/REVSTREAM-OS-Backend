package com.example.website.service;

import java.util.List;
import com.example.website.dto.TaskRequest;
import com.example.website.dto.TaskResponse;

public interface TaskService {

    void addNewTask(Long registrationId, TaskRequest request);

    List<TaskResponse> getTasksByUser(Long registrationId);
    TaskResponse updateTask(Long taskId, TaskRequest request);
    void deleteTask(Long taskId);

}
