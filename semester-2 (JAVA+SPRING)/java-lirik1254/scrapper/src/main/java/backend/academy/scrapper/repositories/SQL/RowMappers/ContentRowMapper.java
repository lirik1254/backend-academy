package backend.academy.scrapper.repositories.SQL.RowMappers;

import backend.academy.scrapper.entities.SQL.Content;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ContentRowMapper implements RowMapper<Content> {
    @Override
    public Content mapRow(ResultSet rs, int rowNum) throws SQLException {
        Content content = new Content();
        content.contentId(rs.getLong("content_id"));
        content.title(rs.getString("title"));
        content.answer(rs.getString("answer"));
        content.creationTime(rs.getString("creation_time"));
        content.updatedType(rs.getString("updated_type"));
        content.urlId(rs.getLong("url_id"));
        content.userName(rs.getString("user_name"));

        return content;
    }
}
