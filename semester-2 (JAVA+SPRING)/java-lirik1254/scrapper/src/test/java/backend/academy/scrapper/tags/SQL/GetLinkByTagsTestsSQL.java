package backend.academy.scrapper.tags.SQL;

import backend.academy.scrapper.tags.GetLinkByTagsTestsBase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class GetLinkByTagsTestsSQL extends GetLinkByTagsTestsBase {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "SQL");
    }
}
