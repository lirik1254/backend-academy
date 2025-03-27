package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.LinkId;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LinkRepositoryORM extends JpaRepository<Link, LinkId> {

    @Query("SELECT DISTINCT l.tags FROM Link l JOIN l.user u WHERE u.chatId = :chatId")
    List<String> getTagsByUsers_ChatId(Long chatId);

    @Query("SELECT l FROM Link l WHERE l.url.url = :url AND l.user.chatId = :chatId")
    List<Link> findByUrlAndChatId(String url, Long chatId);

    List<Link> findByUser_ChatId(Long usersChatId);

    List<Link> findByUser_ChatIdAndTagsIn(Long userChatId, Collection<String> tags);
}
