package backend.academy.scrapper.tags.ORM;

import backend.academy.scrapper.tags.GetLinkByTagsTestsBase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class GetLinkByTagsTestsORM extends GetLinkByTagsTestsBase {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "ORM");
    }
}
