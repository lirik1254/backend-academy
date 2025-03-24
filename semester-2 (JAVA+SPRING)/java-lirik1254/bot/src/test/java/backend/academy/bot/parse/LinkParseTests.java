package backend.academy.bot.parse;

import static org.junit.jupiter.api.Assertions.*;

import general.RegexCheck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тестирует парсинг github и stackoverflow ссылок")
public class LinkParseTests {
    private final RegexCheck regexCheck = new RegexCheck();

    @Test
    @DisplayName("Тестирует парсинг github ссылок")
    public void parseGithubLink() {
        String noHttpsGithubLink = "http://github.com/lirik1254/abTestRepo";
        String noGithubLink = "https://nogithub.com/lirik1254/abTestRepo";
        String moreThan39CharactersNickName =
                "https://github.com/lirik1252525252525252525252lkj25l2j5225252jl5j2l5/abTestRepo";
        String moreThan39CharactersRepoName =
                "https://github.com/lirik1254/PISYATDVAPISYATDVAPISYATDVAPISYATDVA5252525252525252";
        String someBadLettersBefore = "asljkdfj https://github.com/lirik1254/abTestRepo";
        String someBadLettersAfter = "https://github.com/lirik1254/abTestRepo klasdjf";
        String moreThanOneSlash = "https://github.com//lirik1254/abTestRepo";

        String correctGithubRepo = "https://github.com/lirik1254/abTestRepo";
        String correctGithubRepoWithDash = "https://github.com/lirik1254/ab-Test-REPO";

        assertFalse(regexCheck.checkApi(noHttpsGithubLink));
        assertFalse(regexCheck.checkApi(noGithubLink));
        assertFalse(regexCheck.checkApi(moreThan39CharactersNickName));
        assertFalse(regexCheck.checkApi(moreThan39CharactersRepoName));
        assertFalse(regexCheck.checkApi(someBadLettersBefore));
        assertFalse(regexCheck.checkApi(someBadLettersAfter));
        assertFalse(regexCheck.checkApi(moreThanOneSlash));

        assertTrue(regexCheck.checkApi(correctGithubRepo));
        assertTrue(regexCheck.checkApi(correctGithubRepoWithDash));
    }

    @Test
    @DisplayName("Тестирует парсинг StackOverflow ссылок")
    public void parseStackOverflowLink() {
        String noHttpsStackOverflowLink = "http://stackoverflow.com/questions/52";
        String noValidSiteStackOverflowLink = "https://us.stackoverflow.com/questions/52";
        String noValidQuestionIdSizeLink = "https://stackoverflow.com/questions/525252525252525252";
        String noValidQuestionStructureLink = "https://stackoverflow.com/52/question";
        String moreThanOneSlash = "https://stackoverflow.com//question//52";

        String correctRuLink = "https://ru.stackoverflow.com/questions/52";
        String correctGeneralLink = "https://stackoverflow.com/questions/52";

        assertFalse(regexCheck.checkApi(noHttpsStackOverflowLink));
        assertFalse(regexCheck.checkApi(noValidQuestionIdSizeLink));
        assertFalse(regexCheck.checkApi(noValidSiteStackOverflowLink));
        assertFalse(regexCheck.checkApi(noValidQuestionStructureLink));
        assertFalse(regexCheck.checkApi(moreThanOneSlash));

        assertTrue(regexCheck.checkApi(correctRuLink));
        assertTrue(regexCheck.checkApi(correctGeneralLink));
    }
}
