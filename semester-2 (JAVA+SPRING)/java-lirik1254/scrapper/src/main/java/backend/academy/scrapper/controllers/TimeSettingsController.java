package backend.academy.scrapper.controllers;

import backend.academy.scrapper.services.interfaces.TimeSettingsService;
import dto.TimeSettingsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/time", produces = "application/json")
@RequiredArgsConstructor
public class TimeSettingsController {
    private final TimeSettingsService timeSettingsService;

    @PostMapping
    public void addTime(@RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody TimeSettingsDTO timeSettingsDTO) {
        timeSettingsService.addTimeSettings(chatId, timeSettingsDTO);
    }

    @GetMapping
    public String getTime(@RequestHeader("Tg-Chat-Id") Long chatId) {
        return timeSettingsService.getTimeSettings(chatId);
    }
}
