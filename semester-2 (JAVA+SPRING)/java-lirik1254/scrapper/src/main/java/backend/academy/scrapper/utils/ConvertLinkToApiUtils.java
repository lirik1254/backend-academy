package backend.academy.scrapper.utils;

import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("MagicNumber")
public class ConvertLinkToApiUtils {

    public String convertGithubLinkToIssueApi(String link) {
        String[] linkParts = link.split("/");

        String owner = linkParts[3];
        String repoName = linkParts[4];

        return String.format("https://api.github.com/repos/%s/%s/issues", owner, repoName);
    }

    public String convertSOtoQuestionComments(String link) {
        String[] linkParts = link.split("/");

        String site = linkParts[2];
        String questionId = linkParts[4];

        return String.format("https://api.stackexchange.com/2.3/questions/%s/comments?site=%s", questionId, site);
    }

    public String convertSOtoQuestion(String link) {
        String[] linkParts = link.split("/");

        String site = linkParts[2];
        String questionId = linkParts[4];

        return String.format("https://api.stackexchange.com/2.3/questions/%s?site=%s", questionId, site);
    }

    public String convertSOtoQuestionAnswers(String link) {
        String[] linkParts = link.split("/");

        String site = linkParts[2];
        String questionId = linkParts[4];

        return String.format("https://api.stackexchange.com/2.3/questions/%s/answers?site=%s", questionId, site);
    }

    public String convertSOtoAnswerCommentsLink(String answerId, String link) {
        String[] linkParts = link.split("/");

        String site = linkParts[2];

        return String.format("https://api.stackexchange.com/2.3/answers/%s/comments?site=%s", answerId, site);
    }
}
