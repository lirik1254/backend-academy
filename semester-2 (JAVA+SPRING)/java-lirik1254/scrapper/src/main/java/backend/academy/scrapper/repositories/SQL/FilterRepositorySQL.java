package backend.academy.scrapper.repositories.SQL;

import backend.academy.scrapper.entities.SQL.LinkFilters;
import backend.academy.scrapper.repositories.SQL.RowMappers.LinkFiltersRowMapper;
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

    public void addFilter(Long linkId, String filter) {
        String addSql = """
            insert into link_filters (link_id, filters) values ((:link_id), (:filter))""";

        SqlParameterSource parameterSource =
                new MapSqlParameterSource().addValue("link_id", linkId).addValue("filter", filter);

        template.update(addSql, parameterSource);
    }

    public void deleteFilter(Long linkId) {
        String updateSql = """
            delete from link_filters
            where link_id = (:linkId)""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("linkId", linkId);

        template.update(updateSql, parameterSource);
    }

    public List<LinkFilters> getFiltersByLinkId(Long linkId) {
        String getFiltersSql = """
            select * from link_filters
            where link_id = :linkId""";

        SqlParameterSource parameterSource = new MapSqlParameterSource("linkId", linkId);

        return template.query(getFiltersSql, parameterSource, linkFiltersRowMapper);
    }

    public List<LinkFilters> findAll() {
        String findAllSql = "select * from link_filters";
        return template.query(findAllSql, linkFiltersRowMapper);
    }
}
