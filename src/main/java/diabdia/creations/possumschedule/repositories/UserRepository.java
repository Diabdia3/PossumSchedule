package diabdia.creations.possumschedule.repositories;

import diabdia.creations.possumschedule.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByName(String name);

}
