package com.example.website.service;

import java.util.List;

import com.example.website.dto.TaskRequest;
import com.example.website.dto.TaskResponse;

public interface TaskService {

    void addNewTask(TaskRequest request);

    List<TaskResponse> getAllTasks();
}
