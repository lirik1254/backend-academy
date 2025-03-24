package backend.academy.scrapper.entities.SQL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Url {
    private long urlId;
    private String url;
    private String linkType;
}
