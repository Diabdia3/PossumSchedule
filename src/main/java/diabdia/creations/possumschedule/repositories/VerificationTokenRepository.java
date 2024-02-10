package diabdia.creations.possumschedule.repositories;

import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.entities.VerificationToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {
    Optional<VerificationToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM verification_token WHERE verification_token.user_id = ?1", nativeQuery = true)
    void deleteByUser(int userId);
}
