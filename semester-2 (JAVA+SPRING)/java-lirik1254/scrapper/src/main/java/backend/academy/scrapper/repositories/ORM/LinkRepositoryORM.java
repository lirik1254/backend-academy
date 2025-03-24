package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.Link;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LinkRepositoryORM extends JpaRepository<Link, Long> {

    @Query("SELECT DISTINCT l.tags FROM Link l JOIN l.users u WHERE u.chatId = :chatId")
    List<String> getTagsByUsers_ChatId(Long chatId);

    @Query("SELECT l FROM Link l WHERE l.url.url = :url AND l.users.chatId = :chatId")
    List<Link> findByUrlAndChatId(String url, Long chatId);

    List<Link> findByUsers_ChatId(Long usersChatId);

    List<Link> findByUsers_ChatIdAndTagsIn(Long usersChatId, Collection<String> tags);
}
