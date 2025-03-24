package backend.academy.scrapper.link.SQL;

import backend.academy.scrapper.link.GetLinkTestsBase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class GetLinkTestsSQL extends GetLinkTestsBase {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "SQL");
    }
}
