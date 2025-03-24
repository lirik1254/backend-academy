package general;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExceptionUtils {
    public List<String> getStacktrace(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();
    }
}
