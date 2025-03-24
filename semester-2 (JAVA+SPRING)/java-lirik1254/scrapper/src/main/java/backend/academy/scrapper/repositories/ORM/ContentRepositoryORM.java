package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepositoryORM extends JpaRepository<Content, Long> {}
