package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.User;
import backend.academy.scrapper.repositories.SQL.RowMappers.UsersRowMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class UsersRepositorySQL {
    private final NamedParameterJdbcTemplate template;
    private final UsersRowMapper usersRowMapper;

    public void createUser(Long chatId) {
        String createSql =
                """
            INSERT INTO scrapper.user (chat_id)
            VALUES (:chat_id)
            ON CONFLICT (chat_id) DO NOTHING;""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("chat_id", chatId);
        template.update(createSql, parameterSource);
    }

    public void deleteUser(Long chatId) {
        String getUrlsToDelete =
                """
            select url_id from scrapper.link l join scrapper.user u on l.user_id = u.chat_id
            where chat_id = (:chat_id)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("chat_id", chatId);
        List<Long> urlIdsToDelete = template.queryForList(getUrlsToDelete, parameterSource, Long.class);

        String deleteSql = """
            DELETE FROM scrapper.user where chat_id = (:chat_id)""";

        SqlParameterSource deleteSource = new MapSqlParameterSource("chat_id", chatId);
        template.update(deleteSql, deleteSource);

        if (!urlIdsToDelete.isEmpty()) {
            String deleteUrlsSql = """
            DELETE FROM scrapper.url WHERE id IN (:urlIds)""";

            SqlParameterSource deleteParams = new MapSqlParameterSource("urlIds", urlIdsToDelete);
            template.update(deleteUrlsSql, deleteParams);
        }
    }

    public User getByChatId(Long chatId) {
        String getByChatId = """
            select * from scrapper.user where chat_id = (:chat_id)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("chat_id", chatId);

        try {
            return template.queryForObject(getByChatId, parameterSource, usersRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //    public Users getByUsersId(Long userId) {
    //        String getByUserId = """
    //            select * from scrapper.user
    //            where users_id = :userId""";
    //
    //        SqlParameterSource parameterSource = new MapSqlParameterSource("userId", userId);
    //
    //        try {
    //            return template.queryForObject(getByUserId, parameterSource, usersRowMapper);
    //        } catch (EmptyResultDataAccessException e) {
    //            return null;
    //        }
    //    }

    public List<User> findAll() {
        String findAllSql = "select * from scrapper.user";
        return template.query(findAllSql, usersRowMapper);
    }
}
