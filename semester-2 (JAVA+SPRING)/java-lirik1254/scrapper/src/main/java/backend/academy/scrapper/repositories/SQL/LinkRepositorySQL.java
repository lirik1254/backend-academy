package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.repositories.SQL.RowMappers.LinkRowMapper;
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
public class LinkRepositorySQL {
    private final LinkRowMapper linkRowMapper;
    private final NamedParameterJdbcTemplate template;

    public List<Link> getUserLinks(Long chatId) {
        String getLinksQuery =
                """
            select * from scrapper.link
            where user_id = (select chat_id from scrapper.user where chat_id = (:chatId))""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("chatId", chatId);

        try {
            return template.query(getLinksQuery, parameterSource, linkRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public List<Link> getLinksByUrlAndChatId(String link, Long chatId) {
        String getLinksQuery =
                """
            select * from scrapper.link
            where url_id = (select url_id from scrapper.url where url = (:url))
            and user_id = :chatId""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("url", link).addValue("chatId", chatId);

        try {
            return template.query(getLinksQuery, parameterSource, linkRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public void createLink(Long userId, Long urlId) {
        String createSql = """
            insert into scrapper.link(user_id, url_id) values (:userId, :urlId)""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("userId", userId).addValue("urlId", urlId);

        template.update(createSql, parameterSource);
    }

    //    public Link getLinkByUsersIdAndEmptyUrl(Long usersId) {
    //        String getLinkSql = """
    //            select * from scrapper.link where users_id = (:usersId) and url_id is null""";
    //
    //        SqlParameterSource parameterSource = new MapSqlParameterSource("usersId", usersId);
    //
    //        try {
    //            return template.query(getLinkSql, parameterSource, linkRowMapper).getFirst();
    //        } catch (EmptyResultDataAccessException e) {
    //            return null;
    //        }
    //    }

    public void updateLinkUrl(Long userId, Long urlId) {
        String updateLinkSql = """
            update scrapper.link set url_id = (:urlId) where user_id = (:userId)""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("urlId", urlId).addValue("userId", userId);

        template.update(updateLinkSql, parameterSource);
    }

    public void deleteLink(Long chatId, String link) {
        String deleteLinkSql =
                """
            delete from scrapper.link
            where user_id = (select chat_id from scrapper.user where chat_id = :chatId)
            and url_id = (select url_id from scrapper.url where url = :link)""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("chatId", chatId).addValue("link", link);

        template.update(deleteLinkSql, parameterSource);
    }

    public List<Link> getLinksByChatIdAndTagsIn(Long chatId, List<String> tags) {
        String getTagsByChatIdAndTagsInSql =
                """
            select distinct user_id, url_id from scrapper.link join scrapper.link_tags using (user_id, url_id)
            where user_id = (select chat_id from scrapper.user where chat_id = :chatId)
            and tag in (:tags)""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("chatId", chatId).addValue("tags", tags);

        try {
            return template.query(getTagsByChatIdAndTagsInSql, parameterSource, linkRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public List<Link> getLinksByUrlId(Long urlId) {
        String getLinksSql = """
            select * from scrapper.link where url_id = :urlId""";

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("urlId", urlId);

        try {
            return template.query(getLinksSql, parameterSource, linkRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public List<Link> findAll() {
        String findAllSql = "select * from scrapper.link";
        return template.query(findAllSql, linkRowMapper);
    }
}
