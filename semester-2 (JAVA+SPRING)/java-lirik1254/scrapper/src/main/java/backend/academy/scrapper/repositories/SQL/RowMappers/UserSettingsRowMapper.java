package backend.academy.scrapper.repositories.SQL.RowMappers;

import backend.academy.scrapper.entities.SQL.UserSettings;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class UserSettingsRowMapper implements RowMapper<UserSettings> {
    @Override
    public UserSettings mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserSettings userSettings = new UserSettings();
        userSettings.userId(rs.getLong("user_id"));
        userSettings.notifyMood(rs.getString("notify_mood"));

        Time notifyTime = rs.getTime("notify_time");
        userSettings.notifyTime(notifyTime == null ? null : notifyTime.toLocalTime());

        return userSettings;
    }
}
