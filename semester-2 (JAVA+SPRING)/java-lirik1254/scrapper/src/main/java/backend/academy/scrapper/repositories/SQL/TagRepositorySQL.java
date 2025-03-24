package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.LinkTags;
import backend.academy.scrapper.repositories.SQL.RowMappers.LinkTagsRowMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class TagRepositorySQL {
    private final NamedParameterJdbcTemplate template;
    private final LinkTagsRowMapper linkTagsRowMapper;

    public void addTag(Long linkId, String tag) {
        String addSql = """
            insert into link_tags (link_id, tags) values ((:link_id), (:tag))""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("link_id", linkId).addValue("tag", tag);

        template.update(addSql, parameterSource);
    }

    public void deleteTags(Long linkId) {
        String addSql = """
            delete from link_tags
            where link_id = (:linkId)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("linkId", linkId);

        template.update(addSql, parameterSource);
    }

    public List<LinkTags> getTagsByLinkId(Long linkId) {
        String getTags = """
            select * from link_tags where link_id = :linkId""";

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("linkId", linkId);

        return template.query(getTags, parameterSource, linkTagsRowMapper);
    }

    public List<String> getAllTagsByChatId(Long chatId) {
        String allTagsSql =
                """
            select distinct tags from link_tags where link_id in
            (select link_id from link where users_id = (
            select users_id from users where chat_id = :chatId
            ))""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("chatId", chatId);

        return template.queryForList(allTagsSql, parameterSource, String.class);
    }

    public List<String> findAll() {
        String findAllSql = "select * from link_tags";
        return template.query(findAllSql, linkTagsRowMapper).stream()
                .map(LinkTags::text)
                .toList();
    }
}
