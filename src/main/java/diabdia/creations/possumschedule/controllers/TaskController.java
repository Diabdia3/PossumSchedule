package diabdia.creations.possumschedule.controllers;

import diabdia.creations.possumschedule.entities.Task;
import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.repositories.TaskRepository;
import diabdia.creations.possumschedule.service.TaskService;
import diabdia.creations.possumschedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    private User user;

    private User getUser(){
        if(user != null)
            return user;
        else
            user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        return user;
    }

    @GetMapping("/task")
    public String taskForm(Model model) {
        Task task = new Task();
        task.setPriority(1);
        model.addAttribute("task", task);
        return "tasks/taskForm";
    }

    @GetMapping("/task/{id}/edit")
    public String editTask(@PathVariable("id") int id, Model model) {
        model.addAttribute("task", taskService.getTask(id, getUser().getId()));
        model.addAttribute("editMode", true);
        return "tasks/taskForm";
    }

    @PostMapping("/task")
    public String taskSubmit(@ModelAttribute Task task, Model model) {
        task.setCompleted(false);
        task.setUser(getUser());
        taskService.saveTask(task);
        model.addAttribute("task", task);
        return "tasks/taskInfo";
    }

    @GetMapping("/task/{id}")
    public String showTask(@PathVariable("id") int id, Model model) {
        model.addAttribute("task", taskService.getTask(id, getUser().getId()));
        return "tasks/taskInfo";
    }

    @GetMapping("/all")
    public String showAll(Model model) {
        model.addAttribute("tasks", taskService.getAll(getUser().getId()));
        return "tasks/allTasks";
    }

    @GetMapping("/history")
    public String showCompleted(Model model) {
        model.addAttribute("tasks", taskService.getAllCompleted(getUser().getId()));
        return "tasks/taskHistory";
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeTask(@RequestParam("taskId") int taskId) {
        try{
            taskService.removeTask(taskId, getUser().getId());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Task removed successfully.");
    }

    @PostMapping("/complete")
    public ResponseEntity<String> completeTask(@RequestParam("taskId") int taskId) {
        try{
            taskService.completeTask(taskId, getUser().getId());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Task completed successfully.");
    }

    @PostMapping("/incomplete")
    public ResponseEntity<String> incompleteTask(@RequestParam("taskId") int taskId) {
        try{
            taskService.incompleteTask(taskId, getUser().getId());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Task incompleted successfully.");
    }
}
