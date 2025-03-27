package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.Url;
import backend.academy.scrapper.repositories.SQL.RowMappers.UrlRowMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class UrlRepositorySQL {
    private final NamedParameterJdbcTemplate template;
    private final UrlRowMapper urlRowMapper;

    public boolean existUrlByUrl(String url) {
        String existSql = """
            select * from scrapper.url where url = (:url)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("url", url);

        try {
            template.queryForObject(existSql, parameterSource, urlRowMapper);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public Url getByUrl(String url) {
        String returnUrl = """
            select * from scrapper.url where url = (:url)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("url", url);

        try {
            return template.queryForObject(returnUrl, parameterSource, urlRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void createUrl(String url, String linkType) {
        String createUrl = """
            insert into scrapper.url (url, link_type) values (:url, :linkType)""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("url", url).addValue("linkType", linkType);

        template.update(createUrl, parameterSource);
    }

    public void checkDeleteUrl(String url) {
        String deleteUrl =
                """
            delete from scrapper.url
            where url = :url
            and not exists (select * from scrapper.link where link.url_id = (
            select id from scrapper.url where url = :url
            ))""";

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("url", url);

        template.update(deleteUrl, parameterSource);
    }

    public Url getByUrlId(Long urlId) {
        String getByUrlIdSql = """
            select * from scrapper.url
            where id = :urlId""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("urlId", urlId);

        return template.queryForObject(getByUrlIdSql, parameterSource, urlRowMapper);
    }

    public Page<Url> findAllWithPagination(int pageNumber, int pageSize) {
        String sql =
                """
        SELECT * FROM scrapper.url
        ORDER BY id
        LIMIT :pageSize OFFSET :offset
    """;

        String countSql = "SELECT COUNT(*) FROM scrapper.url";

        int offset = pageNumber * pageSize;

        SqlParameterSource params =
                new MapSqlParameterSource().addValue("pageSize", pageSize).addValue("offset", offset);

        // Получаем список объектов Url
        try {
            List<Url> urls = template.query(sql, params, new UrlRowMapper());
            Integer total = template.queryForObject(countSql, new MapSqlParameterSource(), Integer.class);

            if (total == null) {
                total = 0; // Устанавливаем значение по умолчанию, если результат null
            }
            return new PageImpl<>(urls, PageRequest.of(pageNumber, pageSize), total);
        } catch (EmptyResultDataAccessException e) {
            return new PageImpl<>(List.of(), PageRequest.of(pageNumber, pageSize), 0);
        }
    }
}
