package backend.academy.scrapper.repositories.SQL.RowMappers;

import backend.academy.scrapper.entities.SQL.Content;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class ContentRowMapper implements RowMapper<Content> {
    @Override
    public Content mapRow(ResultSet rs, int rowNum) throws SQLException {
        Content content = new Content();
        content.id(rs.getLong("id"));
        content.updatedType(rs.getString("updated_type"));
        content.title(rs.getString("title"));
        content.answer(rs.getString("answer"));
        content.creationTime(rs.getString("creation_time"));
        content.urlId(rs.getLong("url_id"));
        content.userName(rs.getString("user_name"));

        return content;
    }
}
