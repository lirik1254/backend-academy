package general;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class RegexCheck {

    private static final String STACKOVERFLOW_LINK_PATTERN =
            "^https://(ru\\.|)stackoverflow\\.com/questions/\\d{1,10}$";

    private static final String FILTER_PATTERN = "^(\\w+:\\w+)(\\s+\\w+:\\w+)*\\s*$";

    private static final String GITHUB_LINK_PATTERN = "^https://github\\.com/[\\w-]{1,39}/[\\w-]{1,39}$";

    Pattern stackOverflowPattern = Pattern.compile(STACKOVERFLOW_LINK_PATTERN);
    Pattern githubPattern = Pattern.compile(GITHUB_LINK_PATTERN);

    public boolean checkFilter(String filterText) {
        Pattern pattern = Pattern.compile(FILTER_PATTERN);
        Matcher matcher = pattern.matcher(filterText);

        return matcher.find();
    }

    public boolean checkApi(String url) {
        Matcher stackOverflowMatcher = stackOverflowPattern.matcher(url);
        Matcher githubMatcher = githubPattern.matcher(url);

        return stackOverflowMatcher.find() || githubMatcher.find();
    }

    public boolean isGithub(String url) {
        Matcher githubMatcher = githubPattern.matcher(url);
        return githubMatcher.find();
    }
}
