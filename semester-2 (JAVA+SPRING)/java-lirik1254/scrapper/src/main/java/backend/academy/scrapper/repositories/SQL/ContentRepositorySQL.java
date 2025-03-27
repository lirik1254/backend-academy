package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.Content;
import backend.academy.scrapper.repositories.SQL.RowMappers.ContentRowMapper;
import backend.academy.scrapper.utils.LinkType;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class ContentRepositorySQL {
    private final NamedParameterJdbcTemplate template;

    private final ContentRowMapper contentRowMapper;

    public void addContent(
            LinkType linkType,
            String updatedType,
            String answer,
            String creationTime,
            String title,
            String userName,
            long url) {
        String addSql =
                """
        INSERT INTO scrapper.content (answer, creation_time, title, user_name, url_id)
        VALUES (:answer, :creationTime, :title, :userName, :url)
        """;

        // Подготовка параметров
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("answer", answer)
                .addValue("creationTime", creationTime)
                .addValue("title", title)
                .addValue("userName", userName)
                .addValue("url", url);

        // Используем KeyHolder для получения сгенерированного id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(addSql, parameterSource, keyHolder, new String[] {"id"});

        // Получаем сгенерированный id
        Long contentId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        String addForeign;
        if (linkType.equals(LinkType.GITHUB)) {
            addForeign =
                    """

                INSERT INTO scrapper.github_content (id, updated_type)
                            VALUES (:id, :updatedType)
            """;
            MapSqlParameterSource foreignParams = new MapSqlParameterSource()
                    .addValue("id", contentId)
                    .addValue("updatedType", updatedType); // Здесь значение должно быть либо 'PR', либо 'ISSUE'
            template.update(addForeign, foreignParams);
        } else {
            addForeign =
                    """
insert into scrapper.stackoverflow_content (id, updated_type) VALUES (:id, :updatedType)""";
            MapSqlParameterSource foreignParams = new MapSqlParameterSource()
                    .addValue("id", contentId)
                    .addValue("updatedType", updatedType); // Здесь значение должно быть либо 'PR', либо 'ISSUE'
            template.update(addForeign, foreignParams);
        }
    }

    public List<Content> getContentByUrlId(Long urlId) {
        String getContentSql =
                """
            SELECT c.*, COALESCE(g.updated_type, s.updated_type) AS updated_type
            FROM scrapper.content c
            LEFT JOIN scrapper.github_content g ON c.id = g.id
            LEFT JOIN scrapper.stackoverflow_content s ON c.id = s.id
            WHERE c.url_id = :urlId;""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("urlId", urlId);

        return template.query(getContentSql, parameterSource, new ContentRowMapper());
    }

    public void deleteContentByUrlId(Long urlId) {
        String deleteSql = """
            delete from scrapper.content
            where url_id = :urlId""";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("urlId", urlId);

        template.update(deleteSql, sqlParameterSource);
    }

    public List<Content> findAll() {
        String findAllSql =
                """
            SELECT c.*, COALESCE(g.updated_type, s.updated_type) AS updated_type
            FROM scrapper.content c
            LEFT JOIN scrapper.github_content g ON c.id = g.id
            LEFT JOIN scrapper.stackoverflow_content s ON c.id = s.id""";
        return template.query(findAllSql, contentRowMapper);
    }
}
