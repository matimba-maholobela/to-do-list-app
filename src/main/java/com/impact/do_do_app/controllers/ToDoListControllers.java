package com.impact.do_do_app.controllers;

import com.impact.do_do_app.models.ToDoList;
import com.impact.do_do_app.services.ToDoListServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ToDoListControllers {

    private final ToDoListServices toDoListService;

    @Autowired
    public ToDoListControllers(ToDoListServices toDoListService) {
        this.toDoListService = toDoListService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/tasks";
    }

    @GetMapping("/tasks")
    public String getAllTasks(Model model) {
        // used to display all tasks in the index.

        List<ToDoList> tasks = toDoListService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "index";
    }

    @PostMapping("/tasks/add")
    public String addTask(@ModelAttribute ToDoList task) {

        //create new task
        toDoListService.createTask(task);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/update-status")
    public String updateTaskStatus(@RequestParam Long id, @RequestParam String status) {
        // Fetch the existing task
        ToDoList existingTask = toDoListService.getTask(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Check if the current status is "Completed"
        if ("Completed".equals(existingTask.getStatus()) && !"Completed".equals(status)) {
            // If moving away from "Completed", clear the completed date
            toDoListService.clearCompletedDate(id);
        }

        // Update the task status
        toDoListService.updateTaskStatus(id, status);

        // Set completed date if the new status is "Completed"
        if ("Completed".equals(status) && !"Completed".equals(existingTask.getStatus())) {
            toDoListService.setCompletedDate(id);
        }

        return "redirect:/tasks";
    }


    @GetMapping("/tasks/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        // Fetch the task by ID and pass it to the view
        ToDoList task = toDoListService.getTask(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        return "edit-task";  // Ensure this template exists in the templates directory
    }


    @PostMapping("/tasks/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute ToDoList updatedTask) {
        // Fetch the existing task from the service
        ToDoList existingTask = toDoListService.getTask(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        if ("Completed".equals(updatedTask.getStatus()) && !"Completed".equals(existingTask.getStatus())) {
            existingTask.setCompletedAt(LocalDateTime.now());
        } else if (!"Completed".equals(updatedTask.getStatus()) && "Completed".equals(existingTask.getStatus())) {
            existingTask.setCompletedAt(null);
        }
        existingTask.setStatus(updatedTask.getStatus());
        // Don't update createdAt

        toDoListService.updateTask(id, existingTask);

        return "redirect:/tasks";
    }

    @PostMapping("/tasks/delete")
    public String deleteTask(@RequestParam Long id) {

        //delete a task
        toDoListService.deleteTask(id);
        return "redirect:/tasks";
    }
}
