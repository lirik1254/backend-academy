package backend.academy.scrapper.registration.ORM;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.registration.RegisterUserTestsBase;
import backend.academy.scrapper.repositories.ORM.UsersRepositoryORM;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class RegisterUserTestsORM extends RegisterUserTestsBase {

    @Autowired
    private UsersRepositoryORM usersRepositoryORM;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "ORM");
    }

    @Test
    @DisplayName("Пользователь ещё не зарегистрирован")
    public void test1() throws Exception {
        assertTrue(usersRepositoryORM.findAll().isEmpty());

        performRegisterUserRequest(52L);

        assertTrue(usersRepositoryORM.existsByChatId(52L));
    }

    @Test
    @DisplayName("Пользователь уже был зарегистрирован")
    public void test2() throws Exception {
        performRegisterUserRequest(52L);

        assertTrue(usersRepositoryORM.existsByChatId(52L));
        assertEquals(1, usersRepositoryORM.findAll().size());

        performRegisterUserRequest(52L);

        assertTrue(usersRepositoryORM.existsByChatId(52L));
        assertEquals(1, usersRepositoryORM.findAll().size());
    }
}
