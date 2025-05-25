package backend.academy.scrapper.controllers;

import backend.academy.scrapper.services.interfaces.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/tg-chat/{id}", produces = "application/json")
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public void registerUser(@PathVariable Long id) {
        registrationService.registerUser(id);
    }

    @DeleteMapping
    public void deleteUser(@PathVariable Long id) {
        registrationService.deleteUser(id);
    }
}
