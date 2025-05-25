package backend.academy.scrapper.micrometer.link.count;

import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.repositories.ORM.LinkRepositoryORM;
import backend.academy.scrapper.utils.LinkType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@RequiredArgsConstructor
public class LinkCountMetricCheckerORM implements LinkCountMetricChecker {
    private final LinkRepositoryORM linkRepositoryORM;
    private final LinkCountMetric linkCountMetric;

    @Override
    public void check() {
        List<Link> githubLinks = linkRepositoryORM.findByUrl_LinkType(LinkType.GITHUB);
        linkCountMetric.setLinkCount(LinkType.GITHUB, githubLinks.size());

        List<Link> stackoverflowLinks = linkRepositoryORM.findByUrl_LinkType(LinkType.STACKOVERFLOW);
        linkCountMetric.setLinkCount(LinkType.STACKOVERFLOW, stackoverflowLinks.size());
    }
}
