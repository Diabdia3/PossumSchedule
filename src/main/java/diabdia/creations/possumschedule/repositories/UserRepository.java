package diabdia.creations.possumschedule.repositories;

import diabdia.creations.possumschedule.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    @Transactional
    @Modifying
    @Query(value = "UPDATE user SET activated = 1, role = 'USER' WHERE user.id = ?1", nativeQuery = true)
    void activateUser(int userId);
}
