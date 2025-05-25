package backend.academy.scrapper.entities.SQL;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettings {
    private Long userId;
    private String notifyMood;
    private LocalTime notifyTime;
}
