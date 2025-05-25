package backend.academy.scrapper.controllers;

import backend.academy.scrapper.services.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/joke", produces = "application/json")
public class AIController {
    private final AIService aiService;

    @GetMapping
    public String getJoke(@RequestHeader("Tg-Chat-Id") Long chatId) {
        return aiService.getJoke(chatId);
    }
}
