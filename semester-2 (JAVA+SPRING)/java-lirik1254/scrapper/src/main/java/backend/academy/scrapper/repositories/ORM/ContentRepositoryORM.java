package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.Content;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public interface ContentRepositoryORM extends JpaRepository<Content, Long> {}
