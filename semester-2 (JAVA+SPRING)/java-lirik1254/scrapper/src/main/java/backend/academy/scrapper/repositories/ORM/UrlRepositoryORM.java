package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepositoryORM extends JpaRepository<Url, Long> {
    boolean existsUrlByUrl(String link);

    Url getUrlByUrl(String url);
}
