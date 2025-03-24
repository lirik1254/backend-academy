package backend.academy.bot.controllers;

import backend.academy.bot.services.UpdateService;
import dto.UpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/updates", produces = "application/json")
public class UpdateController {
    private final UpdateService updateService;

    @PostMapping
    public String update(@RequestBody UpdateDTO updateDTO) {
        updateService.update(updateDTO.tgChatIds(), updateDTO.url(), updateDTO.contentDTO());
        return "Обновление обработано";
    }
}
