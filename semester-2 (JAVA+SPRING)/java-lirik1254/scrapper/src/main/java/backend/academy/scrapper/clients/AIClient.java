package backend.academy.scrapper.clients;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Setter
public class AIClient {
    @Value("${app.ai.model-name}")
    private String modelName;

    @Value("${app.ai.api-key}")
    private String apiKey;

    @Value("${app.ai.base-url}")
    private String baseUrl;

    String DEVELOPER_MESSAGE =
            """
        Ты рассказываешь анекдоты, сформированные по определённым сообщениям ответов/комментариев с
        stackoveflow или по определённым сообщениям ISSUE в репозитории github.
        Сейчас ты получишь данные об ответах пользователя или о сформированных вопросах. Постарайся написать анекдот
        по этой теме. Анекдот должен быть примерно в 15-100 слов, обязательно на русском языке.""";

    private OpenAIClient openAIClient;

    @PostConstruct
    public void init() {
        openAIClient =
                OpenAIOkHttpClient.builder().apiKey(apiKey).baseUrl(baseUrl).build();
    }

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "baseCircuitBreaker")
    public String createChatCompletion(String userMessage) {
        ChatCompletion chatCompletion = openAIClient
                .chat()
                .completions()
                .create(ChatCompletionCreateParams.builder()
                        .model(modelName)
                        .addDeveloperMessage(DEVELOPER_MESSAGE)
                        .addUserMessage(userMessage)
                        .temperature(0.4)
                        .maxCompletionTokens(80000L)
                        .build());

        return chatCompletion.choices().getFirst().message().content().orElse("Ошибка");
    }
}
