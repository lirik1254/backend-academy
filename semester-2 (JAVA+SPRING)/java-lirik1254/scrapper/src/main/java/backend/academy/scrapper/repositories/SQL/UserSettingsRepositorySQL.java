package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.UserSettings;
import backend.academy.scrapper.repositories.SQL.RowMappers.UserSettingsRowMapper;
import backend.academy.scrapper.repositories.UserSettingsRepository;
import dto.TimeSettingsDTO;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class UserSettingsRepositorySQL implements UserSettingsRepository {
    private final NamedParameterJdbcTemplate template;
    private final UserSettingsRowMapper userSettingsRowMapper;

    public void addTimeSettings(Long chatId, TimeSettingsDTO timeSettingsDTO) {
        String sql =
                """
            insert into scrapper.user_settings(user_id, notify_mood, notify_time)
            values (:userId, :mood, :time)
            on conflict (user_id) do update
            set notify_mood = EXCLUDED.notify_mood,
                notify_time = EXCLUDED.notify_time""";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", chatId)
                .addValue("mood", timeSettingsDTO.notifyMood().name())
                .addValue("time", timeSettingsDTO.notifyTime());

        template.update(sql, sqlParameterSource);
    }

    public List<UserSettings> getUserSettings(Long chatId) {
        String sql = """
            select * from scrapper.user_settings u
            where u.user_id = :chatId""";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("chatId", chatId);

        return template.query(sql, sqlParameterSource, userSettingsRowMapper);
    }

    @Override
    public List<Long> findAllUserIdsByNotifyTime(LocalTime now) {
        String sql =
                """
            select us.user_id from scrapper.user_settings us
            where us.notify_time = :now""";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("now", now);

        return template.queryForList(sql, sqlParameterSource, Long.class);
    }
}
