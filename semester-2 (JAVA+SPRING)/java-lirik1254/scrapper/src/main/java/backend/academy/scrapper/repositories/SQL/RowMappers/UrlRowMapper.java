package backend.academy.scrapper.repositories.SQL.RowMappers;

import backend.academy.scrapper.entities.SQL.Url;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class UrlRowMapper implements RowMapper<Url> {
    @Override
    public Url mapRow(ResultSet rs, int rowNum) throws SQLException {
        Url url = new Url();
        url.urlId(rs.getLong("url_id"));
        url.url(rs.getString("url"));
        url.linkType(rs.getString("link_type"));
        return url;
    }
}
