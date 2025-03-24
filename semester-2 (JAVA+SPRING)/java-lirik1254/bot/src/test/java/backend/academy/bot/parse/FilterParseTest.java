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
        String filterWithoutColon = "MyFilter";
        String filterWithSpaces = "   MyFilter:Filter  ";
        String moreThanOneFilterNoColon = "Myfilter:Filter filter&filter";
        String filtersWithComma = "filter:filter, filter:comma";

        String oneCorrectFilter = "filter:filter";
        String moreThanOneCorrectFilter = "filter:filter correctfilter:FILTER52";

        assertFalse(regexCheck.checkFilter(filterWithoutColon));
        assertFalse(regexCheck.checkFilter(filterWithSpaces));
        assertFalse(regexCheck.checkFilter(moreThanOneFilterNoColon));
        assertFalse(regexCheck.checkFilter(filtersWithComma));

        assertTrue(regexCheck.checkFilter(oneCorrectFilter));
        assertTrue(regexCheck.checkFilter(moreThanOneCorrectFilter));
    }
}
