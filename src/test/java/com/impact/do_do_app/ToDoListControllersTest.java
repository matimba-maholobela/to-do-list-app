package com.impact.do_do_app;

import com.impact.do_do_app.models.ToDoList;
import com.impact.do_do_app.services.ToDoListServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ToDoListControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ToDoListServices toDoListServices;

    @Test
    public void testRootRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    public void testGetTasksPage() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("tasks"));
    }

    @Test
    public void testAddTask() throws Exception {
        mockMvc.perform(post("/tasks/add")
                        .param("name", "New Test Task")
                        .param("description", "Testing task add")
                        .param("status", "Not Started"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    public void testEditTaskPage() throws Exception {
        ToDoList task = new ToDoList("Edit Test", "Desc", LocalDateTime.now(), null, "Not Started");
        ToDoList savedTask = toDoListServices.createTask(task);

        mockMvc.perform(get("/tasks/edit/" + savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-task"))
                .andExpect(model().attributeExists("task"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        ToDoList task = new ToDoList("Update Test", "Desc", LocalDateTime.now(), null, "Not Started");
        ToDoList savedTask = toDoListServices.createTask(task);

        mockMvc.perform(post("/tasks/update/" + savedTask.getId())
                        .param("name", "Updated Task Name")
                        .param("description", "Updated Desc")
                        .param("status", "Completed"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        ToDoList task = new ToDoList("Status Test", "Desc", LocalDateTime.now(), null, "Not Started");
        ToDoList savedTask = toDoListServices.createTask(task);

        mockMvc.perform(post("/tasks/update-status")
                        .param("id", savedTask.getId().toString())
                        .param("status", "Completed"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    public void testDeleteTask() throws Exception {
        ToDoList task = new ToDoList("Delete Test", "Desc", LocalDateTime.now(), null, "Not Started");
        ToDoList savedTask = toDoListServices.createTask(task);

        mockMvc.perform(post("/tasks/delete")
                        .param("id", savedTask.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    public void testAnalyticsPage() throws Exception {
        mockMvc.perform(get("/analytics"))
                .andExpect(status().isOk())
                .andExpect(view().name("analytics"))
                .andExpect(model().attributeExists("formattedStartDate", "formattedEndDate", "createdCount", "completedCount"));
    }
}
