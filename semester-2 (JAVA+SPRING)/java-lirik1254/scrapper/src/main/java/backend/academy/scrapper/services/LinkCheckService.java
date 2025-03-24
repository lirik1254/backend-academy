package backend.academy.scrapper.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkCheckService {
    private final UpdateChecker updateChecker;

    @Scheduled(fixedRate = 15000)
    public void scheduleAllChecks() {
        log.info("Происходит проверка");
        updateChecker.checkUpdates();
    }
}
