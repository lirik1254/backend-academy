package backend.academy.scrapper.controllers;

import backend.academy.scrapper.services.interfaces.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tags/{chatId}", produces = "application/json")
public class TagsController {
    private final TagService tagService;

    @GetMapping
    @Cacheable(value = "tagsCache", key = "#chatId")
    public List<String> getAllTags(@PathVariable Long chatId) {
        return tagService.getAllTags(chatId);
    }
}
