package backend.academy.scrapper.services;

public interface RegistrationService {
    void registerUser(Long chatId);

    void deleteUser(Long userId);
}
