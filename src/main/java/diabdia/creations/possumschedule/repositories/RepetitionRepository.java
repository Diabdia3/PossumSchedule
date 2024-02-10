package diabdia.creations.possumschedule.repositories;

import diabdia.creations.possumschedule.entities.RepetitionRule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepetitionRepository extends CrudRepository<RepetitionRule, Integer> {
    @Query(value = "SELECT * FROM repetition_rule WHERE (repetition_rule.rule != 'DAYS' AND repetition_rule.rule = ?1) OR ('DAYS' = ?1 AND repetition_rule.days = ?2)", nativeQuery = true)
    List<RepetitionRule> findByRuleAndDays(String rule, int days);
}
