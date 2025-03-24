package fractal.flame;

import fractal.flame.transformations.RandomTransform;
import fractal.flame.utils.Point;
import fractal.flame.utils.PointUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
public class FractalBot extends TelegramLongPollingBot {

    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private final HashMap<String, Boolean> isGeneratedNow = new HashMap<>();
    private final HashMap<String, Integer> generateNum = new HashMap<>();

    private final Integer iterationNum = 4000;
    private final Integer transformationNum = 5;
    private final Integer secondIterationNum = 2500;
    private final Integer xRes = 500;
    private final Integer yRes = 500;

    @SuppressWarnings("checkstyle:UncommentedMain")
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new FractalBot());
        } catch (TelegramApiException e) {
            log.error("Ошибка при запуске бота: ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "FractalGenerationBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("TG_BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        cachedThreadPool.submit(() -> handleUpdate(update));
    }

    private void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userId = update.getMessage().getFrom().getId().toString();
            String userName = update.getMessage().getFrom().getUserName();
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            logMessage(userId, userName, messageText);

            if (isGeneratedNow.getOrDefault(userId, false)) {
                sendMessage(chatId, "Подождите, пока завершится текущая генерация.");
                return;
            }

            switch (messageText.toLowerCase()) {
                case "/start":
                    sendMessage(chatId, "Добро пожаловать! Отправьте команду /generate для создания фрактала.");
                    break;
                case "/generate":
                    startFractalGeneration(chatId, userId);
                    break;
                default:
                    sendMessage(chatId, "Неизвестная команда. Попробуйте /generate.");
                    break;
            }
        }
    }

    private void startFractalGeneration(long chatId, String userId) {
        sendMessage(chatId, "Генерация фрактала началась...");
        isGeneratedNow.put(userId, true);
        try {
            generateNum.compute(userId, (key, val) -> (val == null) ? 1 : val + 1);
            Instant start = Instant.now();

            Render render = new Render();
            List<List<Point>> renderImage = render.render(
                iterationNum, transformationNum, secondIterationNum, xRes, yRes,
                PointUtils.rand.nextBoolean(), PointUtils.getAffineTransformationList(transformationNum),
                RandomTransform.getRandomTransformation(), 1
            );

            Correction correction = new Correction();
            correction.correction(renderImage);

            CreateImage image = new CreateImage();
            String fractalPath = String.format("%s_%d.png", userId, generateNum.get(userId));
            image.createImage(renderImage, fractalPath);

            sendFractal(chatId, fractalPath, start);
        } catch (Exception e) {
            log.error("Ошибка при генерации фрактала: ", e);
            sendMessage(chatId, "Произошла ошибка при генерации фрактала.");
        } finally {
            isGeneratedNow.put(userId, false);
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void sendFractal(long chatId, String imagePath, Instant start) {
        try {
            File image = new File(imagePath);
            InputFile inputFile = new InputFile(image);

            org.telegram.telegrambots.meta.api.methods.send.SendPhoto photo = new
                org.telegram.telegrambots.meta.api.methods.send.SendPhoto();
            photo.setChatId(chatId);
            photo.setPhoto(inputFile);
            execute(photo);

            Instant stop = Instant.now();
            sendMessage(chatId, String.format("Время отправки фрактала - %d,%dс.",
                Duration.between(start, stop).toSeconds(),
                (int) Duration.between(start, stop).toMillis() / 100));
            image.delete();
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке фрактала: ", e);
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: ", e);
        }
    }

    private void logMessage(String userId, String userName, String messageText) {
        String logEntry = String.format("[%s] User ID: %s, UserName: %s, Message: %s", Instant.now(), userId,
            userName, messageText);
        log.info(logEntry);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bot_logs.txt", true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            log.error("Ошибка при записи логов: ", e);
        }
    }
}
