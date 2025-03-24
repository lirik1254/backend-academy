package backend.academy.scrapper.services.SQL.updatechecker;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.entities.SQL.Url;
import backend.academy.scrapper.repositories.SQL.UrlRepositorySQL;
import backend.academy.scrapper.services.AbstractUpdateChecker;
import backend.academy.scrapper.utils.SQL.UpdateCheckerUtilsSQL;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@Slf4j
public class UpdateCheckerSQL extends AbstractUpdateChecker<Url> {
    private final UrlRepositorySQL urlRepositorySQL;
    private final UpdateCheckerUtilsSQL updateCheckerUtilsSQL;

    public UpdateCheckerSQL(
            ScrapperConfig scrapperConfig,
            UrlRepositorySQL urlRepositorySQL,
            UpdateCheckerUtilsSQL updateCheckerUtilsSQL) {
        super(scrapperConfig);
        this.urlRepositorySQL = urlRepositorySQL;
        this.updateCheckerUtilsSQL = updateCheckerUtilsSQL;
    }

    @Override
    protected Page<Url> fetchPage(int pageNumber, int pageSize) {
        log.atInfo()
                .addKeyValue("pageNumber", pageNumber)
                .addKeyValue("pageSize", pageSize)
                .addKeyValue("access-type", "SQL")
                .setMessage("Получение всех URL с пагинацией")
                .log();
        return urlRepositorySQL.findAllWithPagination(pageNumber, pageSize);
    }

    @Override
    protected void processBatch(List<Url> batch) {
        String result = batch.stream().map(Url::url).collect(Collectors.joining(" "));
        log.atInfo()
                .addKeyValue("batch", result)
                .addKeyValue("access-type", "SQL")
                .setMessage("Обработка URL")
                .log();
        updateCheckerUtilsSQL.processUrlPage(batch);
    }

    @Override
    protected String getUrl(Url entity) {
        return entity.url();
    }
}
