package backend.academy.scrapper;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Тестирование метода конвертации базовых ссылок в ссылку для запроса по api")
public class ConvertTest {
    ConvertLinkToApiUtils convertLinkToApiUtils = new ConvertLinkToApiUtils();

    @Test
    @DisplayName("Конвертация ГХ ссылки в api")
    public void convertGithubLinkToApi() {
        String githubLink = "https://github.com/lirik1254/abTestRepo";
        String githubLinkAnotherFormat = "https://github.com/lirik1254/ANOTHER-FORMAAt-REPo";

        String githubConvertedLink = "https://api.github.com/repos/lirik1254/abTestRepo/issues";
        String githubConvertedLinkAnotherFormat = "https://api.github.com/repos/lirik1254/ANOTHER-FORMAAt-REPo/issues";

        assertEquals(githubConvertedLink, convertLinkToApiUtils.convertGithubLinkToIssueApi(githubLink));
        assertEquals(
                githubConvertedLinkAnotherFormat,
                convertLinkToApiUtils.convertGithubLinkToIssueApi(githubLinkAnotherFormat));
    }

    @Test
    @DisplayName("Конвертация SO ссылки в api для комментариев к вопросу")
    public void convertSOtoQuestionCommentsTest() {
        String stackOverflowLink = "https://ru.stackoverflow.com/questions/52";
        String convertedStackOverflowLink =
                "https://api.stackexchange.com/2.3/questions/52/comments?site=ru.stackoverflow.com";

        assertEquals(convertedStackOverflowLink, convertLinkToApiUtils.convertSOtoQuestionComments(stackOverflowLink));
    }

    @Test
    @DisplayName("Конвертация SO ссылки в api для вопроса")
    public void convertGeneralStackOverflowLinkToApi() {
        String stackOverflowLink = "https://stackoverflow.com/questions/52";
        String convertedStackoverflowLink = "https://api.stackexchange.com/2.3/questions/52?site=stackoverflow.com";

        assertEquals(convertedStackoverflowLink, convertLinkToApiUtils.convertSOtoQuestion(stackOverflowLink));
    }

    @Test
    @DisplayName("Конвертация SO ссылки в api для ответов на вопрос")
    public void convertSOtoQuestionAnswers() {
        String stackOverflowLink = "https://stackoverflow.com/questions/52";
        String convertedStackoverflowLink =
                "https://api.stackexchange.com/2.3/questions/52/answers?site=stackoverflow.com";

        assertEquals(convertedStackoverflowLink, convertLinkToApiUtils.convertSOtoQuestionAnswers(stackOverflowLink));
    }

    @Test
    @DisplayName("Конвертация SO ссылки в api для комментариев к вопросу")
    public void convertSOtoAnswerCommentsLink() {
        String stackOverflowLink = "https://stackoverflow.com/questions/52";
        String convertedStackoverflowLink =
                "https://api.stackexchange.com/2.3/answers/52/comments?site=stackoverflow.com";

        assertEquals(
                convertedStackoverflowLink,
                convertLinkToApiUtils.convertSOtoAnswerCommentsLink("52", stackOverflowLink));
    }
}
