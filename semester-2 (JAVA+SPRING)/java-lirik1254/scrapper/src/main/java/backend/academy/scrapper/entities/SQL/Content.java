package backend.academy.scrapper.entities.SQL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Content {
    private Long id;
    private String answer;
    private String updatedType;
    private String creationTime;
    private String title;
    private String userName;
    private Long urlId;
}
