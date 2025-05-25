package backend.academy.bot.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.bot.BaseConfigure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import dto.ContentDTO;
import dto.UpdateDTO;
import dto.UpdateType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateSendTests extends BaseConfigure {

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TelegramBot telegramBot;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Тестирование отправки обновления - дата отправки строка")
    public void test1() throws Exception {
        ContentDTO contentDTO = new ContentDTO(
                UpdateType.ISSUE,
                "Заголовок иссуе",
                "lirik1254",
                "2025-03-13T15:27:50Z",
                "ну короче неправильно ты сделал");
        UpdateDTO updateDTO = new UpdateDTO(1L, "https://github.com/lirik1254/abTestRepo", contentDTO, List.of(123L));

        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление обработано"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, atLeastOnce()).execute(captor.capture());

        String retAnswer =
                """
            Пришло уведомление по url https://github.com/lirik1254/abTestRepo
            Тип: ISSUE
            Текст темы: Заголовок иссуе
            Имя пользователя: lirik1254
            Время создания: 2025-03-13T15:27:50Z
            Превью: ну короче неправильно ты сделал""";

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                retAnswer, sentMessage.getParameters().get("text").toString().replace("\r\n", "\n"));
    }

    @Test
    @DisplayName("Тестирование отправки обновления - дата отправки unix строка")
    public void test2() throws Exception {
        ContentDTO contentDTO = new ContentDTO(
                UpdateType.ISSUE, "Заголовок иссуе", "lirik1254", "1742045314", "ну короче неправильно ты сделал");
        UpdateDTO updateDTO = new UpdateDTO(1L, "https://github.com/lirik1254/abTestRepo", contentDTO, List.of(123L));

        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление обработано"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, atLeastOnce()).execute(captor.capture());

        String retAnswer =
                """
            Пришло уведомление по url https://github.com/lirik1254/abTestRepo
            Тип: ISSUE
            Текст темы: Заголовок иссуе
            Имя пользователя: lirik1254
            Время создания: 2025-03-15T18:28:34
            Превью: ну короче неправильно ты сделал""";

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String actualMessage =
                sentMessage.getParameters().get("text").toString().replace("\r\n", "\n");
        actualMessage = actualMessage.replaceAll("T\\d{2}:\\d{2}:\\d{2}.*", "T18:28:34");

        assertEquals(retAnswer, actualMessage);
    }

    @Test
    @DisplayName("Тестирование отправки обновления - комментарий")
    public void test3() throws Exception {
        ContentDTO contentDTO = new ContentDTO(
                UpdateType.COMMENT, "Заголовок иссуе", "lirik1254", "1742045314", "ну короче неправильно ты сделал");
        UpdateDTO updateDTO = new UpdateDTO(1L, "https://github.com/lirik1254/abTestRepo", contentDTO, List.of(123L));

        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление обработано"));

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, atLeastOnce()).execute(captor.capture());

        String retAnswer =
                """
            Пришло уведомление по url https://github.com/lirik1254/abTestRepo
            Тип: Комментарий
            Текст темы: Заголовок иссуе
            Имя пользователя: lirik1254
            Время создания: 2025-03-15T18:28:34
            Превью: ну короче неправильно ты сделал""";

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String actualMessage =
                sentMessage.getParameters().get("text").toString().replace("\r\n", "\n");

        String expectedMessage = retAnswer.substring(0, retAnswer.indexOf("T") + 1);
        String actualMessageTrimmed = actualMessage.substring(0, actualMessage.indexOf("T") + 1);

        assertEquals(expectedMessage, actualMessageTrimmed);
    }
}
