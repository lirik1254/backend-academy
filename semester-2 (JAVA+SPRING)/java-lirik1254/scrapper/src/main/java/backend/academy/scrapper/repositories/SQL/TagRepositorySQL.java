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

    public void addTag(Long userId, Long urlId, String tag) {
        String addSql =
                """
            insert into scrapper.link_tags(user_id, url_id, tag) values (:userId, :urlId, :tag)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("urlId", urlId)
                .addValue("tag", tag);

        template.update(addSql, parameterSource);
    }

    public void deleteTags(Long userId, Long urlId) {
        String addSql =
                """
            delete from scrapper.link_tags
            where user_id = :userId and url_id = :urlId""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("userId", userId).addValue("urlId", urlId);

        template.update(addSql, parameterSource);
    }

    public List<LinkTags> getTagsByUrlIdAndUserId(Long urlId, Long userId) {
        String getTags =
                """
            select * from scrapper.link_tags where user_id = :userId
            and url_id = :urlId""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("urlId", urlId).addValue("userId", userId);

        return template.query(getTags, parameterSource, linkTagsRowMapper);
    }

    public List<String> getAllTagsByUserId(Long userId) {
        String allTagsSql = """
            select distinct tag from scrapper.link_tags where user_id = :userId""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("userId", userId);

        return template.queryForList(allTagsSql, parameterSource, String.class);
    }

    public List<String> findAll() {
        String findAllSql = "select * from scrapper.link_tags";
        return template.query(findAllSql, linkTagsRowMapper).stream()
                .map(LinkTags::tag)
                .toList();
    }
}
