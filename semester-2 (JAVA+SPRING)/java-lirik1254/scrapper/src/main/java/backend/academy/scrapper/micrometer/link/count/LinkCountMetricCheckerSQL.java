package backend.academy.scrapper.micrometer.link.count;

import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.repositories.SQL.LinkRepositorySQL;
import backend.academy.scrapper.utils.LinkType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@RequiredArgsConstructor
public class LinkCountMetricCheckerSQL implements LinkCountMetricChecker {
    private final LinkRepositorySQL linkRepositorySQL;
    private final LinkCountMetric linkCountMetric;

    @Override
    public void check() {
        List<Link> stackoverflowLinks = linkRepositorySQL.findAllByUrlLinkType(LinkType.STACKOVERFLOW.toString());
        linkCountMetric.setLinkCount(LinkType.STACKOVERFLOW, stackoverflowLinks.size());

        List<Link> githubLinks = linkRepositorySQL.findAllByUrlLinkType(LinkType.GITHUB.toString());
        linkCountMetric.setLinkCount(LinkType.GITHUB, githubLinks.size());
    }
}
