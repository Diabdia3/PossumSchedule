package diabdia.creations.possumschedule.repositories;

import diabdia.creations.possumschedule.entities.Task;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Integer> {
    List<Task> findByName(String name);

    Task findById(int id);

    @Query(value = "SELECT * FROM task WHERE task.user_id = ?1 AND task.completed = 0 ORDER BY task.priority", nativeQuery = true)
    List<Task> findAllByUserIdSortByPriority(int userId);

    @Query(value = "SELECT * FROM task WHERE task.user_id = ?1 AND task.completed = 1 ORDER BY task.priority", nativeQuery = true)
    List<Task> findAllCompletedByUserIdSortByPriority(int userId);

    @Query(value = "SELECT COUNT(id) FROM task WHERE task.user_id = ?1 AND completed = 0", nativeQuery = true)
    Integer getUnfinishedTasksCount(int userId);

    @Query(value = "SELECT COUNT(id) FROM task WHERE task.user_id = ?1", nativeQuery = true)
    Integer getTasksCount(int userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE task SET completed = 1 WHERE task.id = ?1", nativeQuery = true)
    void completeTask(int taskId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE task SET completed = 0 WHERE task.id = ?1", nativeQuery = true)
    void incompleteTask(int taskId);
}
