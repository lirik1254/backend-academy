package backend.academy.scrapper.repositories.SQL.RowMappers;

import backend.academy.scrapper.entities.SQL.LinkTags;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class LinkTagsRowMapper implements RowMapper<LinkTags> {
    @Override
    public LinkTags mapRow(ResultSet rs, int rowNum) throws SQLException {
        LinkTags linkTags = new LinkTags();
        linkTags.linkId(rs.getLong("link_id"));
        linkTags.text(rs.getString("tags"));
        return linkTags;
    }
}
