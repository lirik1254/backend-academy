package backend.academy.scrapper.services;

import static backend.academy.scrapper.ScrapperConfig.THREAD_NUMBER;

import backend.academy.scrapper.ScrapperConfig;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractUpdateChecker<T> implements UpdateChecker {
    private final ScrapperConfig scrapperConfig;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER);

    protected abstract Page<T> fetchPage(int pageNumber, int pageSize);

    protected abstract void processBatch(List<T> batch);

    protected abstract String getUrl(T entity);

    @Override
    public void checkUpdates() {
        int pageSize = scrapperConfig.batchSize();
        int pageNumber = 0;
        Page<T> page;

        do {
            page = fetchPage(pageNumber, pageSize);
            List<T> entities = page.getContent();
            int totalRecords = entities.size();
            int threadCount = THREAD_NUMBER;

            int baseBatchSize = totalRecords / threadCount;
            int remaining = totalRecords % threadCount;

            int currentIndex = 0;
            for (int i = 0; i < threadCount; i++) {
                int batchSize = baseBatchSize + (i >= threadCount - remaining ? 1 : 0);
                if (batchSize == 0) continue;

                int fromIndex = currentIndex;
                int toIndex = Math.min(fromIndex + batchSize, totalRecords);
                List<T> subList = entities.subList(fromIndex, toIndex);

                executorService.submit(() -> {
                    log.info(
                            "Обрабатываются в этом потоке: {}",
                            subList.stream().map(this::getUrl).collect(Collectors.joining(", ")));
                    processBatch(subList);
                });

                currentIndex = toIndex;
            }
            pageNumber++;
        } while (page.hasNext());
    }
}
