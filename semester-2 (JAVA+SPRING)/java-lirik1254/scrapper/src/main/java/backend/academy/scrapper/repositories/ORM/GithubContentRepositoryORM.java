package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.GithubContent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public interface GithubContentRepositoryORM extends JpaRepository<GithubContent, Long> {}
