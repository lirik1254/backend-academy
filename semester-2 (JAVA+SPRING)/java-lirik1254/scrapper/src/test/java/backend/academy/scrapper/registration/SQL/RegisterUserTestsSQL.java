package backend.academy.scrapper.registration.SQL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.scrapper.registration.RegisterUserTestsBase;
import backend.academy.scrapper.repositories.SQL.UsersRepositorySQL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class RegisterUserTestsSQL extends RegisterUserTestsBase {

    @Autowired
    public UsersRepositorySQL usersRepositorySQL;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "SQL");
    }

    @Test
    @DisplayName("Пользователь ещё не зарегистрирован")
    public void test1() throws Exception {
        assertTrue(usersRepositorySQL.findAll().isEmpty());

        performRegisterUserRequest(52L);

        assertNotNull(usersRepositorySQL.getByChatId(52L));
    }

    @Test
    @DisplayName("Пользователь уже был зарегистрирован")
    public void test2() throws Exception {
        performRegisterUserRequest(52L);

        assertNotNull(usersRepositorySQL.getByChatId(52L));

        performRegisterUserRequest(52L);

        assertNotNull(usersRepositorySQL.getByChatId(52L));
        assertEquals(1, usersRepositorySQL.findAll().size());
    }
}
