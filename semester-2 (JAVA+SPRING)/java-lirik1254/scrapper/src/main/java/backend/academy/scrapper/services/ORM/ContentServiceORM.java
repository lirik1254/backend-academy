package backend.academy.scrapper.services.ORM;

import backend.academy.scrapper.entities.JPA.GithubContent;
import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.StackOverflowContent;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.repositories.ORM.GithubContentRepositoryORM;
import backend.academy.scrapper.repositories.ORM.LinkRepositoryORM;
import backend.academy.scrapper.repositories.ORM.StackOverflowContentRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.services.interfaces.ContentService;
import dto.ContentDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@Slf4j
public class ContentServiceORM implements ContentService {
    private final StackOverflowContentRepositoryORM stackOverflowContentRepositoryORM;
    private final GithubContentRepositoryORM githubContentRepositoryORM;
    private final LinkRepositoryORM linkRepositoryORM;
    private final UrlRepositoryORM urlRepositoryORM;

    @Override
    public List<ContentDTO> getContentByRandomUrl(Long chatId) {
        List<StackOverflowContent> stackOverflowContents = stackOverflowContentRepositoryORM.findAll();
        List<GithubContent> githubContents = githubContentRepositoryORM.findAll();

        List<Link> allByIdUserId = linkRepositoryORM.findAllById_UserId(chatId);
        if (allByIdUserId.isEmpty()) {
            return List.of();
        } else {
            int index = ThreadLocalRandom.current().nextInt(allByIdUserId.size());
            Link randomLink = allByIdUserId.get(index);
            Url url = randomLink.url();
            List<ContentDTO> allContentByUrl = new ArrayList<>();
            stackOverflowContents.forEach(sc -> {
                if (sc.url().url().equals(url.url())) {
                    allContentByUrl.add(new ContentDTO(
                            sc.updatedType(), sc.title(), sc.userName(), sc.creationTime(), sc.answer()));
                }
            });
            githubContents.forEach(gc -> {
                if (gc.url().equals(url)) {
                    allContentByUrl.add(new ContentDTO(
                            gc.updatedType(), gc.title(), gc.userName(), gc.creationTime(), gc.answer()));
                }
            });
            return allContentByUrl;
        }
    }
}
