package com.impact.do_do_app.services;

import com.impact.do_do_app.models.ToDoList;
import com.impact.do_do_app.repositories.ToDoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ToDoListServiceImp implements ToDoListServices {

    private final ToDoListRepository toDoListRepository;

    @Autowired
    public ToDoListServiceImp(ToDoListRepository toDoListRepository) {
        this.toDoListRepository = toDoListRepository;
    }

    @Override
    public ToDoList createTask(ToDoList task) {
        task.setCreatedAt(LocalDateTime.now());
        if ("Completed".equals(task.getStatus()) && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }
        return toDoListRepository.save(task);
    }

    @Override
    public ToDoList updateTask(Long id, ToDoList updatedTask) {
        if (!toDoListRepository.existsById(id)) {
            throw new RuntimeException("Unable to retrieve task with id " + id);
        }
        updatedTask.setId(id);
        return toDoListRepository.save(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        if (!toDoListRepository.existsById(id)) {
            throw new RuntimeException("Unable to retrieve task with id " + id);
        }
        toDoListRepository.deleteById(id);
    }

    @Override
    public Optional<ToDoList> getTask(Long id) {
        return toDoListRepository.findById(id);
    }

    @Override
    public List<ToDoList> getAllTasks() {
        return (List<ToDoList>) toDoListRepository.findAll();
    }

    @Override
    public ToDoList updateTaskStatus(Long id, String status) {
        Optional<ToDoList> optionalTask = toDoListRepository.findById(id);
        if (!optionalTask.isPresent()) {
            throw new RuntimeException("Task with id " + id + " not found");
        }
        ToDoList task = optionalTask.get();
        task.setStatus(status);
        return toDoListRepository.save(task);
    }

    @Override
    public ToDoList setCompletedDate(Long id) {
        Optional<ToDoList> optionalTask = toDoListRepository.findById(id);
        if (!optionalTask.isPresent()) {
            throw new RuntimeException("Task with id " + id + " not found");
        }
        ToDoList task = optionalTask.get();
        task.setCompletedAt(LocalDateTime.now());
        return toDoListRepository.save(task);
    }

    @Override
    public ToDoList clearCompletedDate(Long id) {
        ToDoList task = getTask(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setCompletedAt(null);
        return updateTask(id, task);
    }

    @Override
    public long countTasksCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return toDoListRepository.countByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public long countTasksCompletedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return toDoListRepository.countByCompletedAtBetween(startDate, endDate);
    }

    @Override
    public long countTasksByStatus(String status) {
        return toDoListRepository.countByStatus(status);
    }

    @Override
    public long countTasksByStatusBetween(String status, LocalDateTime startDate, LocalDateTime endDate) {
        return toDoListRepository.countByStatusAndCreatedAtBetween(status, startDate, endDate);
    }
}
