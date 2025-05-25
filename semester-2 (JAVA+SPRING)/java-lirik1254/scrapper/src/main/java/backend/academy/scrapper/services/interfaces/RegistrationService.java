package backend.academy.scrapper.services.interfaces;

public interface RegistrationService {
    void registerUser(Long chatId);

    void deleteUser(Long userId);
}
