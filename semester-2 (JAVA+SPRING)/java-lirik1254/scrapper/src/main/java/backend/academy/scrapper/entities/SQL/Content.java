package backend.academy.scrapper.entities.SQL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Content {
    private long contentId;
    private String answer;
    private String creationTime;
    private String title;
    private String updatedType;
    private String userName;
    private long urlId;
}
