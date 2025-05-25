package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.LinkId;
import backend.academy.scrapper.utils.LinkType;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public interface LinkRepositoryORM extends JpaRepository<Link, LinkId> {

    @Query(
            """
        SELECT DISTINCT t.id.tag
          FROM Link l
          JOIN l.tags t
         WHERE l.user.chatId = :chatId
        """)
    List<String> getTagsByUsers_ChatId(Long chatId);

    @Query("SELECT l FROM Link l WHERE l.url.url = :url AND l.user.chatId = :chatId")
    List<Link> findByUrlAndChatId(String url, Long chatId);

    List<Link> findByUser_ChatId(Long usersChatId);

    List<Link> findByUser_ChatIdAndTags_Id_TagIn(Long userChatId, Collection<String> tagsIdTags);

    List<Link> findAllById_UserId(Long idUserId);

    List<Link> findByUrl_LinkType(LinkType urlLinkType);
}
