package backend.academy.scrapper.services.update;

import backend.academy.scrapper.clients.update.UpdateLinkClientFacade;
import backend.academy.scrapper.repositories.UserSettingsRepository;
import dto.SendUpdateDTO;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateLinkService {
    private final UpdateLinkRedisService updateLinkRedisService;
    private final UpdateLinkClientFacade updateLinkClient;

    private final UserSettingsRepository userSettingsRepository;

    @Scheduled(fixedRate = 60000)
    public void sendNotifications() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<Long> allByNotifyTime = userSettingsRepository.findAllUserIdsByNotifyTime(now);
        allByNotifyTime.forEach(userId -> {
            List<SendUpdateDTO> updates = updateLinkRedisService.getUpdates(userId);
            updates.forEach(update -> updateLinkClient.sendUpdate(userId, update.url(), update.content()));
            updateLinkRedisService.clearUpdates(userId);
        });
    }
}
