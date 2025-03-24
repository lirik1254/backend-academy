package backend.academy.scrapper.tags.ORM;

import backend.academy.scrapper.tags.GetAllTagsTestsBase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class GetAllTagsTestsORM extends GetAllTagsTestsBase {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "ORM");
    }
}
