package backend.academy.bot.parse;

import static org.junit.jupiter.api.Assertions.*;

import general.RegexCheck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FilterParseTest {
    private RegexCheck regexCheck = new RegexCheck();

    @Test
    @DisplayName("Тестирует парсинг фильтров")
    public void parseFilterTest() {
        String filterWithoutColon = "user";
        String filterWithSpaces = "   user=Filter  ";
        String moreThanOneFilterNoColon = "user=Filter user&filter";
        String filtersWithComma = "user=user, user=comma";

        String oneCorrectFilter = "user=lirik1254";
        String moreThanOneCorrectFilter = "user=lirik1254 user=FILTER52";

        assertFalse(regexCheck.checkFilter(filterWithoutColon));
        assertFalse(regexCheck.checkFilter(filterWithSpaces));
        assertFalse(regexCheck.checkFilter(moreThanOneFilterNoColon));
        assertFalse(regexCheck.checkFilter(filtersWithComma));

        assertTrue(regexCheck.checkFilter(oneCorrectFilter));
        assertTrue(regexCheck.checkFilter(moreThanOneCorrectFilter));
    }
}
