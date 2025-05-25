package backend.academy.scrapper.services;

import backend.academy.scrapper.micrometer.link.count.LinkCountMetricChecker;
import backend.academy.scrapper.services.interfaces.UpdateChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkCheckService {
    private final UpdateChecker updateChecker;
    private final LinkCountMetricChecker linkCountMetricChecker;

    @Scheduled(fixedRate = 15000)
    public void scheduleUpdatesChecks() {
        log.info("Происходит проверка");
        updateChecker.checkUpdates();
    }

    @Scheduled(fixedRate = 15000)
    public void scheduleMetricChecks() {
        log.info("Обновление кол-ва активных ссылок в бд");
        linkCountMetricChecker.check();
    }
}
