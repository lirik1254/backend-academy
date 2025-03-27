package backend.academy.scrapper.entities.SQL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkFilters {
    private Long userId;
    private Long urlId;
    private String filter;
}
