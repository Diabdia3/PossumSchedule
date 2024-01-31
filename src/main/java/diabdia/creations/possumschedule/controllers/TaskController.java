package diabdia.creations.possumschedule.controllers;

import diabdia.creations.possumschedule.entities.Task;
import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    private User user;

    private User getUser(){
        if(user != null)
            return user;
        else
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        Task task =  taskRepository.findById(id);
        if(!task.getUser().getId().equals(getUser().getId()))
            throw new AccessDeniedException("403");
        model.addAttribute("task", task);
        return "tasks/taskForm";
    }

    @PostMapping("/task")
    public String taskSubmit(@ModelAttribute Task task, Model model) {
        task.setCompleted(false);
        task.setUser(getUser());
        taskRepository.save(task);
        model.addAttribute("task", task);
        return "tasks/taskInfo";
    }

    @GetMapping("/task/{id}")
    public String showTask(@PathVariable("id") int id, Model model) {
        Task task =  taskRepository.findById(id);
        if(!task.getUser().getId().equals(getUser().getId()))
            throw new AccessDeniedException("403");
        model.addAttribute("task", task);
        return "tasks/taskInfo";
    }

    @GetMapping("/all")
    public String showAll(Model model) {
        model.addAttribute("tasks", taskRepository.findAllByUserIdSortByPriority(getUser().getId()));
        return "tasks/allTasks";
    }

    @GetMapping("/history")
    public String showCompleted(Model model) {
        model.addAttribute("tasks", taskRepository.findAllCompletedByUserIdSortByPriority(getUser().getId()));
        return "tasks/taskHistory";
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeTask(@RequestParam("taskId") int taskId) {
        try{
            Task task =  taskRepository.findById(taskId);
            if(!task.getUser().getId().equals(getUser().getId()))
                throw new AccessDeniedException("403");
            taskRepository.deleteById(taskId);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Task removed successfully.");
    }

    @PostMapping("/complete")
    public ResponseEntity<String> completeTask(@RequestParam("taskId") int taskId) {
        try{
            Task task =  taskRepository.findById(taskId);
            if(!task.getUser().getId().equals(getUser().getId()))
                throw new AccessDeniedException("403");
            taskRepository.completeTask(taskId);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Task completed successfully.");
    }

    @PostMapping("/incomplete")
    public ResponseEntity<String> incompleteTask(@RequestParam("taskId") int taskId) {
        try{
            Task task =  taskRepository.findById(taskId);
            if(!task.getUser().getId().equals(getUser().getId()))
                throw new AccessDeniedException("403");
            taskRepository.incompleteTask(taskId);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Task incompleted successfully.");
    }
}
