package diabdia.creations.possumschedule.service;

import diabdia.creations.possumschedule.entities.Task;
import diabdia.creations.possumschedule.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    TaskRepository repository;

    public Task getTask(int id, int userId){
        Task task =  repository.findById(id);
        if(!task.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        return task;
    }

    public void saveTask(Task task){
        repository.save(task);
    }

    public List<Task> getAll(int userId){
        return repository.findAllByUserIdSortByPriority(userId);
    }

    public List<Task> getAllCompleted(int userId){
        return repository.findAllCompletedByUserIdSortByPriority(userId);
    }

    public void removeTask(int id, int userId){
        Task task =  repository.findById(id);
        if(!task.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        repository.deleteById(id);
    }

    public void completeTask(int id, int userId){
        Task task =  repository.findById(id);
        if(!task.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        repository.completeTask(id);
    }

    public void incompleteTask(int id, int userId){
        Task task =  repository.findById(id);
        if(!task.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        repository.incompleteTask(id);
    }
}
