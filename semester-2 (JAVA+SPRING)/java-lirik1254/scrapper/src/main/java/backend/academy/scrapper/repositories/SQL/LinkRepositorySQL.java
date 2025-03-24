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
            select * from link
            where users_id = (select users_id from users where chat_id = (:chat_id))""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("chat_id", chatId);

        try {
            return template.query(getLinksQuery, parameterSource, linkRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public List<Link> getLinksByUrlAndChatId(String link, Long chatId) {
        String getLinksQuery =
                """
            select * from link
            where url_id = (select url_id from url where url = (:url))
            and users_id = (select users_id from users where chat_id = (:chat_id))""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("url", link).addValue("chat_id", chatId);

        try {
            return template.query(getLinksQuery, parameterSource, linkRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public void createLink(Long usersId) {
        String createSql = """
            insert into link(users_id) values (:users_id)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("users_id", usersId);

        template.update(createSql, parameterSource);
    }

    public Link getLinkByUsersIdAndEmptyUrl(Long usersId) {
        String getLinkSql = """
            select * from link where users_id = (:usersId) and url_id is null""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("usersId", usersId);

        try {
            return template.query(getLinkSql, parameterSource, linkRowMapper).getFirst();
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateLinkUrl(Long linkId, Long urlId) {
        String updateLinkSql = """
            update link set url_id = (:urlId) where link_id = (:linkId)""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("urlId", urlId).addValue("linkId", linkId);

        template.update(updateLinkSql, parameterSource);
    }

    public void deleteLink(Long chatId, String link) {
        String deleteLinkSql =
                """
            delete from link
            where users_id = (select users_id from users where chat_id = :chatId)
            and url_id = (select url_id from url where url = :link)""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("chatId", chatId).addValue("link", link);

        template.update(deleteLinkSql, parameterSource);
    }

    public List<Link> getLinksByChatIdAndTagsIn(Long chatId, List<String> tags) {
        String getTagsByChatIdAndTagsInSql =
                """
            select distinct link_id, users_id, url_id from link join link_tags using (link_id)
            where users_id = (select users_id from users where chat_id = :chatId)
            and tags in (:tags)""";

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
            select * from link where url_id = :urlId""";

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("urlId", urlId);

        try {
            return template.query(getLinksSql, parameterSource, linkRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public List<Link> findAll() {
        String findAllSql = "select * from link";
        return template.query(findAllSql, linkRowMapper);
    }
}
