package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public interface UsersRepositoryORM extends JpaRepository<User, Long> {
    boolean existsByChatId(Long chatId);

    User getByChatId(Long chatId);

    User findByChatId(Long chatId);
}
