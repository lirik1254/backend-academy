package backend.academy.samples;

import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Disabled
public class RestAssuredTest {
    String BaseURL = "https://reqres.in/api";

    @Test
    public void createUser() {
        Map<String, String> data = Map.of(
            "name", "NewUser1",
            "job", "Testing"
        );

        given()
            .contentType(ContentType.JSON)
            .body(data)

            .when()
            .post(BaseURL + "/users")

            .then()
            .statusCode(201)
            .body("name", equalTo("NewUser1"))
            .body("job", equalTo("Testing"))
            .log().all();

    }

    @Test
    public void getUser() {
        given()
            .contentType(ContentType.JSON)

            .when()
            .get(BaseURL + "/users/2")

            .then()
            .statusCode(200)
            .body("data.first_name", equalTo("Janet"))
            .log().all();
    }
}
