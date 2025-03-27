package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepositoryORM extends JpaRepository<User, Long> {
    boolean existsByChatId(Long chatId);

    void deleteByChatId(Long chatId);

    User getByChatId(Long chatId);

    User findByChatId(Long chatId);
}
