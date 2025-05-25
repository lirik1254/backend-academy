package backend.academy.bot.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.BaseConfigure;
import backend.academy.bot.services.TelegramBotService;
import backend.academy.bot.services.messages.State;
import backend.academy.bot.services.messages.TimeSettingsCommand;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("Тест команды /time")
@SpringBootTest
public class TimeSettingsCommandTests extends BaseConfigure {
    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private TimeSettingsCommand timeSettingsCommand;

    @Test
    @DisplayName("Если вызвал команду /timesettings, просят выбрать тип отправки")
    public void test1() {
        wireMockServer.stubFor(get(urlEqualTo("/time"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("Отправлять сразу")));

        when(message.text()).thenReturn("/timesettings");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Сейчас ваши настройки следующие:\n" + "Отправлять сразу\n"
                        + "\n"
                        + "Выберите, когда вам отправлять уведомления:\n"
                        + "\n"
                        + "1. Сразу\n"
                        + "2. Раз в сутки\n"
                        + "\n"
                        + "Введите либо 1, либо 2",
                sentMessage.getParameters().get("text"));
        assertEquals(
                State.WAITING_FOR_SETTING_CHOOSE,
                timeSettingsCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Если во время выбора настроек вызовешь команду /stop, выйдешь из выбора настроек")
    public void test2() {
        when(message.text()).thenReturn("/stop");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_SETTING_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Вы вышли из выбора способа отправки уведомлений",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), timeSettingsCommand.userStates());
    }

    @Test
    @DisplayName("Если во время выбора настроек напишешь не 1 или не 2, попросят ввести заново")
    public void test3() {
        when(message.text()).thenReturn("https://someincorrectURL.com");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_SETTING_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите либо 1, либо 2, либо /stop",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.WAITING_FOR_SETTING_CHOOSE)), timeSettingsCommand.userStates());
    }

    @Test
    @DisplayName("Если выберешь 1 (отправлять уведомления сразу), настройки сохранятся")
    public void test4() {
        wireMockServer.stubFor(post(urlEqualTo("/time"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse().withStatus(200)));

        when(message.text()).thenReturn("1");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_SETTING_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals("Сохранено", sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), timeSettingsCommand.userStates());
    }

    @Test
    @DisplayName("Если ошибка на стороне сервера при сохранении настроек, выведет ошибку")
    public void test5() {
        wireMockServer.stubFor(post(urlEqualTo("/time"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse().withStatus(500)));

        when(message.text()).thenReturn("1");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_SETTING_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals("Ошибка", sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), timeSettingsCommand.userStates());
    }

    @Test
    @DisplayName("Если выберешь 2, предложат ввести время")
    public void test6() {
        when(message.text()).thenReturn("2");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_SETTING_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Выберите время (например, 10:00)", sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.WAITING_FOR_TIME_CHOOSE)), timeSettingsCommand.userStates());
    }

    @Test
    @DisplayName("Если введёшь время не в формате HH:mm - попросят ввести заново или набрать /stop")
    public void test7() {
        when(message.text()).thenReturn("h:52");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_TIME_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите время в формате hh:mm, либо введите введите /stop",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.WAITING_FOR_TIME_CHOOSE)), timeSettingsCommand.userStates());
    }

    @Test
    @DisplayName("Если введёшь /stop выйдешь из настроек времени")
    public void test8() {
        when(message.text()).thenReturn("/stop");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_TIME_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Вы вышли из выбора способа отправки уведомлений",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), timeSettingsCommand.userStates());
    }

    @Test
    @DisplayName("После ввода корректного времени ответит сохранено")
    public void test9() {
        wireMockServer.stubFor(post(urlEqualTo("/time"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse().withStatus(200)));

        when(message.text()).thenReturn("10:52");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_TIME_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals("Сохранено", sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), timeSettingsCommand.userStates());
    }

    @Test
    @DisplayName("Если произошла ошибка на сервера при некорректном вводе времени - выведет 'ошибка'")
    public void test10() {
        wireMockServer.stubFor(post(urlEqualTo("/time"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse().withStatus(500)));

        when(message.text()).thenReturn("10:52");
        ReflectionTestUtils.setField(
                timeSettingsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_TIME_CHOOSE)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals("Ошибка", sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), timeSettingsCommand.userStates());
    }
}
