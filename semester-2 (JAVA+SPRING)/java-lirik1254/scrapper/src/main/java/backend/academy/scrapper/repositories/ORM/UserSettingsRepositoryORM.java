package backend.academy.scrapper.repositories.ORM;

import backend.academy.scrapper.entities.JPA.User;
import backend.academy.scrapper.entities.JPA.UserSettings;
import backend.academy.scrapper.repositories.UserSettingsRepository;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public interface UserSettingsRepositoryORM extends JpaRepository<UserSettings, Long>, UserSettingsRepository {
    UserSettings user(User user);

    List<UserSettings> findByUserId(Long userId);

    List<UserSettings> findAllByNotifyTime(LocalTime notifyTime);

    @Override
    default List<Long> findAllUserIdsByNotifyTime(LocalTime t) {
        return findAllByNotifyTime(t).stream().map(UserSettings::userId).toList();
    }
}
