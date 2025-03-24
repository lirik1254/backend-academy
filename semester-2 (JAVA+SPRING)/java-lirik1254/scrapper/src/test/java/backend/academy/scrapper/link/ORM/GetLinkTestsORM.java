package backend.academy.scrapper.link.ORM;

import backend.academy.scrapper.link.GetLinkTestsBase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class GetLinkTestsORM extends GetLinkTestsBase {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "ORM");
    }
}
