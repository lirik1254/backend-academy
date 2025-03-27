package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.LinkFilters;
import backend.academy.scrapper.repositories.SQL.RowMappers.LinkFiltersRowMapper;
import backend.academy.scrapper.repositories.SQL.RowMappers.UsersRowMapper;
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
public class FilterRepositorySQL {
    private final NamedParameterJdbcTemplate template;
    private final LinkFiltersRowMapper linkFiltersRowMapper;
    private final UsersRowMapper usersRowMapper;

    public void addFilter(Long userId, Long urlId, String filter) {
        String addSql =
                """
            insert into scrapper.link_filters (user_id, url_id, filter) values (:userId, :urlId, :filter)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("urlId", urlId)
                .addValue("filter", filter);

        template.update(addSql, parameterSource);
    }

    public void deleteFilters(Long linkId, Long userId) {
        String updateSql =
                """
            delete from scrapper.link_filters
            where user_id = :userId and url_id = :linkId""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("userId", userId).addValue("linkId", linkId);

        template.update(updateSql, parameterSource);
    }

    public List<LinkFilters> getFiltersByUserIdAndUrlId(Long urlId, Long userId) {
        String getFiltersSql =
                """
            select * from scrapper.link_filters
            where url_id = :urlId and user_id = :userId""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("urlId", urlId).addValue("userId", userId);

        return template.query(getFiltersSql, parameterSource, linkFiltersRowMapper);
    }

    public List<LinkFilters> findAll() {
        String findAllSql = "select * from scrapper.link_filters";
        return template.query(findAllSql, linkFiltersRowMapper);
    }
}
