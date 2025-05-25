package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.Url;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public interface UrlRepositoryORM extends JpaRepository<Url, Long> {
    boolean existsUrlByUrl(String link);

    Url getUrlByUrl(String url);

    List<Url> getUrlById(Long id);
}
