package backend.academy.scrapper.client;

public class StackOverflowRetString {
    public static final String FIRST_ANSWER_COMMENTS =
            """
            {
                "items" : [
                {
                "owner" : {
                    "display_name" : "lirik1254"
                },
                "creation_date" : 1742078859,
                "body" : "first_comment",
                "comment_id" : 152435
                },
                {
                "owner" : {
                    "display_name" : "andrey545454"
                },
                "creation_date" : 1445078859,
                "body": "second_comment",
                "comment_id" : 34534543
                }
                ]
            }""";

    public static final String RETURN_ANSWER_STRING =
            """
            {
                "items" : [
                {
                "owner" : {
                    "display_name" : "lirik1254"
                },
                "creation_date" : 1742078859,
                "body" : "piisyat dva",
                "answer_id" : 152
                },
                {
                "owner" : {
                    "display_name" : "another"
                },
                "creation_date" : 1445078859,
                "body": "another_answer",
                "answer_id" : 52
                }
                ]
            }""";

    public static final String SECOND_ANSWER_COMMENTS =
            """
            {
                "items" : [
                {
                "owner" : {
                    "display_name" : "lirik1254"
                },
                "creation_date" : 1742078859,
                "body" : "third_comment",
                "comment_id" : 152435
                },
                {
                "owner" : {
                    "display_name" : "another"
                },
                "creation_date" : 1445078859,
                "body": "fourth_comment",
                "comment_id" : 34534543
                }
                ]
            }""";

    public static final String RETURN_TITLE_STRING =
            """
            {
                "items" : [
                {
                "title" : "52"
                }
                ]
            }""";

    public static final String SO_TO_QUESTION_LINK =
            "http://localhost:8080/stackoverflow/questions/52?site=ru.stackoverflow.com";

    public static final String SO_TO_QUESTION_COMMENTS_LINK =
            "http://localhost:8080/stackoverflow/questions/52/comments?site=ru.stackoverflow.com";

    public static final String SO_TO_QUESTION_ANSWERS =
            "http://localhost:8080/stackoverflow/questions/52/answers?site=ru.stackoverflow.com";

    public static final String SO_TO_ANSWER_COMMENTS_52 =
            "http://localhost:8080/stackoverflow/answers/52/comments?site=ru.stackoverflow.com";

    public static final String SO_TO_ANSWER_COMMENTS_152 =
            "http://localhost:8080/stackoverflow/answers/152/comments?site=ru.stackoverflow.com";

    public static final String WIREMOCK_SO_TO_QUESTION_LINK =
            "/stackoverflow/questions/52?site=ru.stackoverflow.com&key=test" + "&access_token=test&filter=withbody";

    public static final String WIREMOCK_SO_TO_QUESTION_COMMENTS_LINK =
            "/stackoverflow/questions/52/comments?site=ru.stackoverflow.com&key=test"
                    + "&access_token=test&filter=withbody";

    public static final String WIREMOCK_SO_TO_QUESTION_ANSWERS_LINK =
            "/stackoverflow/questions/52/answers?site=ru.stackoverflow.com&key=test"
                    + "&access_token=test&filter=withbody";

    public static final String WIREMOCK_SO_TO_ANSWER_COMMENTS_52 =
            "/stackoverflow/answers/52/comments?site=ru.stackoverflow.com&key=test"
                    + "&access_token=test&filter=withbody";

    public static final String WIREMOCK_SO_TO_ANSWER_COMMENTS_152 =
            "/stackoverflow/answers/152/comments?site=ru.stackoverflow.com&key=test"
                    + "&access_token=test&filter=withbody";
}
