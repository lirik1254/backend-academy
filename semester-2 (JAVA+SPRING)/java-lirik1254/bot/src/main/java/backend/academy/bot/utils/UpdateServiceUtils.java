package backend.academy.bot.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

@Component
public class UpdateServiceUtils {
    public String toDate(Integer creationDate) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(creationDate), ZoneId.systemDefault())
                .toString();
    }

    public boolean isInteger(String time) {
        try {
            Integer.parseInt(time);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String dateConverter(String date) {
        Boolean isInteger = isInteger(date);
        if (isInteger) {
            date = toDate(Integer.parseInt(date));
        }
        return date;
    }

    public String answerDelimitation(String answer) {
        return answer.length() > 200 ? answer.substring(0, 200) : answer;
    }
}
