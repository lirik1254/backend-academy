package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.Content;
import backend.academy.scrapper.repositories.SQL.RowMappers.ContentRowMapper;
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
public class ContentRepositorySQL {
    private final NamedParameterJdbcTemplate template;

    private final ContentRowMapper contentRowMapper;

    public void addContent(
            String updatedType, String answer, String creationTime, String title, String userName, long url) {
        String addSql =
                """
            insert into content (answer, creation_time, title, updated_type, user_name, url_id)
            values (:answer, :creationTime, :title, :updatedType, :userName, :url )""";

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("answer", answer)
                .addValue("creationTime", creationTime)
                .addValue("title", title)
                .addValue("updatedType", updatedType)
                .addValue("userName", userName)
                .addValue("url", url);

        template.update(addSql, parameterSource);
    }

    public List<Content> getContentByUrlId(Long urlId) {
        String getContentSql = """
            select * from content
            where url_id = :urlId""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("urlId", urlId);

        return template.query(getContentSql, parameterSource, new ContentRowMapper());
    }

    public void deleteContentByUrlId(Long urlId) {
        String deleteSql = """
            delete from content
            where url_id = :urlId""";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("urlId", urlId);

        template.update(deleteSql, sqlParameterSource);
    }

    public List<Content> findAll() {
        String findAllSql = "select * from content";
        return template.query(findAllSql, contentRowMapper);
    }
}
