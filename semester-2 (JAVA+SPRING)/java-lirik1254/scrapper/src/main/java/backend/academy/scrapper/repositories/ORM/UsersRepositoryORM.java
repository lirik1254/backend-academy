package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepositoryORM extends JpaRepository<Users, Long> {
    boolean existsByChatId(Long chatId);

    void deleteByChatId(Long chatId);

    Users getByChatId(Long chatId);

    Users findByChatId(Long chatId);
}
