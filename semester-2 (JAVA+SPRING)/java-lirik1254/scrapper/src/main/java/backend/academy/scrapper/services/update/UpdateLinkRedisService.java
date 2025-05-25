package backend.academy.scrapper.services.update;

import dto.ContentDTO;
import dto.SendUpdateDTO;
import dto.UpdatePayload;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateLinkRedisService {
    private final RedisTemplate<Long, UpdatePayload> redisTemplate;

    public void addUpdate(Long chatId, String url, ContentDTO content) {
        UpdatePayload updatePayload = new UpdatePayload(url, content);
        redisTemplate.opsForList().rightPush(chatId, updatePayload);
    }

    public List<SendUpdateDTO> getUpdates(Long chatId) {
        return Objects.requireNonNull(redisTemplate.opsForList().range(chatId, 0, -1)).stream()
                .map(update -> new SendUpdateDTO(chatId, update.link(), update.content()))
                .toList();
    }

    public void clearUpdates(Long chatId) {
        redisTemplate.delete(chatId);
    }
}
