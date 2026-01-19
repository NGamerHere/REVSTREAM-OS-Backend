package com.example.website.service;

import org.springframework.stereotype.Service;

import com.example.website.dto.TaskRequest;
import com.example.website.dto.TaskResponse;
import com.example.website.entity.Task;
import com.example.website.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void addNewTask(TaskRequest request) {

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setUrgency(request.getUrgency());

        task.setAttachments(request.getAttachments());
        task.setNote(request.getNote());

        task.setPrice(request.getPrice());
        task.setSkills(request.getSkills());

        task.setCreatedAt(LocalDateTime.now());

        taskRepository.save(task);
    }

    @Override
    public List<TaskResponse> getAllTasks() {

        return taskRepository.findAll()
                .stream()
                .map(task -> {
                    TaskResponse res = new TaskResponse();
                    res.setId(task.getId());
                    res.setTitle(task.getTitle());
                    res.setDescription(task.getDescription());
                    res.setDueDate(task.getDueDate());
                    res.setUrgency(task.getUrgency());
                    res.setAttachments(task.getAttachments());
                    res.setNote(task.getNote());
                    res.setPrice(task.getPrice());
                    res.setSkills(task.getSkills());
                    res.setCreatedAt(task.getCreatedAt());
                    return res;
                })
                .collect(Collectors.toList());
    }
}
