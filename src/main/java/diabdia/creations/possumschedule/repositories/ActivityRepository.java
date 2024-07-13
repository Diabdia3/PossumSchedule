package diabdia.creations.possumschedule.repositories;

import diabdia.creations.possumschedule.entities.Activity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends CrudRepository<Activity, Integer> {
    List<Activity> findByName(String name);

    @Query(value = "SELECT * FROM activity WHERE user_id = ?1 AND activity.name = ?2 ORDER BY activity.start_time", nativeQuery = true)
    List<Activity> findByNameAndUserSortByStartTime(int userId, String name);

    @Query(value = """
            SELECT a.* FROM activity as a
            LEFT JOIN repetition_rule as rr ON a.repetition_rule_id = rr.id
            WHERE a.user_id = ?1 AND
            (Date(a.start_time) = ?2 OR (
            a.repetition_rule_id is not null AND
            (CASE
            	WHEN rr.rule = 'DAY' THEN TRUE
                WHEN rr.rule = 'WEEK' THEN dayname(DATE(a.start_time)) = dayname(?2)
                WHEN rr.rule = 'DAYS' THEN MOD( DATEDIFF( ?2 , DATE(a.start_time)), rr.days) = 0
                WHEN rr.rule = 'MONTH' THEN day(DATE(a.start_time)) = day(?2)
            	WHEN rr.rule = 'YEAR' THEN day(DATE(a.start_time)) = day(?2) AND month(DATE(a.start_time)) = month(?2)
                ELSE FALSE
            END))) ORDER BY TIME(a.start_time);
            """,
            nativeQuery = true)
    List<Activity> findByDateSortByStartTime(int userId, LocalDate date);

    @Query(value = "SELECT * FROM activity WHERE activity.user_id = ?1", nativeQuery = true)
    List<Activity> findAllByUserId(int userId);

    @Modifying
    @Query(value = "DELETE FROM activity WHERE repetition_rule_id IS NULL AND activity.start_time < (NOW() - INTERVAL 14 DAY)", nativeQuery = true)
    void deleteActivitiesFromTwoWeeksAgo();
}
