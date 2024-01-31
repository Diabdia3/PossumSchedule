package diabdia.creations.possumschedule.repositories;

import diabdia.creations.possumschedule.entities.Activity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends CrudRepository<Activity, Integer> {
    List<Activity> findByName(String name);

    @Query(value = """
            SELECT activity.* FROM activity 
            LEFT JOIN repetition_rule ON activity.repetition_rule_id = repetition_rule.id 
            WHERE activity.user_id = ?1 AND 
            (activity.start_time >= ?2 AND activity.start_time < ?3 ) OR 
            (activity.start_time < ?2 AND activity.end_time >= ?2 ) OR 
            (repetition_rule_id IS NOT NULL AND 
            (CASE
                 WHEN repetition_rule.rule = 'DAY' THEN TRUE
                 WHEN repetition_rule.rule = 'WEEK' THEN DATEDIFF(DATE( ?2 ), DATE(activity.start_time)) % 7 <= DAteDIFF(activity.end_time, activity.start_time)
                 WHEN repetition_rule.rule = 'DAYS' THEN DATEDIFF(DATE( ?2 ), DATE(activity.start_time)) % repetition_rule.days <= DATEDIFF(activity.end_time, activity.start_time)
                ELSE false
            END)) ORDER BY TIME(activity.start_time);
            """,
            nativeQuery = true)
    List<Activity> findByDateSortByStartTime(int userId, LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT * FROM task WHERE task.user_id = ?1", nativeQuery = true)
    List<Activity> findAllByUserId(int userId);

    @Query(value="SELECT COUNT(id) FROM activity WHERE activity.user_id = ?1 AND activity.start_time >= (SELECT DATE_ADD(CURDATE(), interval  -WEEKDAY(CURDATE()) day)) AND activity.start_time <= (DATE_ADD(DATE_ADD(CURDATE(), interval  -WEEKDAY(CURDATE()) day), interval 6 day));", nativeQuery = true)
    Integer getAllWeeklyActivitiesCount(int userId);

    @Query(value="SELECT COUNT(id) FROM activity WHERE activity.user_id = ?1 AND activity.start_time >= CURDATE() AND activity.start_time <= (DATE_ADD(DATE_ADD(CURDATE(), interval  -WEEKDAY(CURDATE()) day), interval 6 day));",
            nativeQuery = true)
    Integer getRemainingWeeklyActivitiesCount(int userId);

    @Modifying
    @Query(value = "DELETE FROM activity WHERE repetition_rule_id IS NULL AND activity.start_time < (NOW() - INTERVAL 14 DAY)", nativeQuery = true)
    void deleteActivitiesFromTwoWeeksAgo();
}
