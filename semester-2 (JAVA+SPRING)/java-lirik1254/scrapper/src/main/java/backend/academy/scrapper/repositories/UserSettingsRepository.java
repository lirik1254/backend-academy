package backend.academy.scrapper.repositories;

import java.time.LocalTime;
import java.util.List;

public interface UserSettingsRepository {
    List<Long> findAllUserIdsByNotifyTime(LocalTime notify);
}
