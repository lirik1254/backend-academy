package backend.academy.scrapper.services.ORM.updatechecker;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.services.AbstractUpdateChecker;
import backend.academy.scrapper.utils.ORM.UpdateCheckerUtilsORM;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@Slf4j
public class UpdateCheckerORM extends AbstractUpdateChecker<Url> {
    private final UrlRepositoryORM urlRepositoryORM;
    private final UpdateCheckerUtilsORM updateCheckerUtilsORM;

    public UpdateCheckerORM(
            ScrapperConfig scrapperConfig,
            UrlRepositoryORM urlRepositoryORM,
            UpdateCheckerUtilsORM updateCheckerUtilsORM) {
        super(scrapperConfig);
        this.urlRepositoryORM = urlRepositoryORM;
        this.updateCheckerUtilsORM = updateCheckerUtilsORM;
    }

    @Override
    protected Page<Url> fetchPage(int pageNumber, int pageSize) {
        log.atInfo()
                .addKeyValue("pageNumber", pageNumber)
                .addKeyValue("pageSize", pageSize)
                .addKeyValue("access-type", "ORM")
                .setMessage("Получение всех URL с пагинацией")
                .log();
        return urlRepositoryORM.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    protected void processBatch(List<Url> batch) {
        String result = batch.stream().map(Url::url).collect(Collectors.joining(", "));
        log.atInfo()
                .addKeyValue("batch", result)
                .addKeyValue("access-type", "ORM")
                .setMessage("Обработка URL")
                .log();
        updateCheckerUtilsORM.processUrlPage(batch);
    }

    @Override
    protected String getUrl(Url entity) {
        return entity.url();
    }
}
