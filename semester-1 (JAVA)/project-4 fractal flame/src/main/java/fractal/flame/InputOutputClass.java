package fractal.flame;

import fractal.flame.transformations.RandomTransform;
import fractal.flame.transformations.Transform;
import fractal.flame.utils.AffineCoef;
import fractal.flame.utils.Point;
import fractal.flame.utils.PointUtils;
import fractal.flame.utils.RGB;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("RegexpSinglelineJava")
public class InputOutputClass {
    private final int aAffineCoefIndex = 0;
    private final int bAffineCoefIndex = 1;
    private final int cAffineCoefIndex = 2;
    private final int dAffineCoefIndex = 3;
    private final int eAffineCoefIndex = 4;
    private final int fAffineCoefIndex = 5;
    private final int rAfffineCoefIndex = 6;
    private final int gAfffineCoefIndex = 7;
    private final int bAfffineCoefIndex = 8;
    private final short leftRGBBound = 0;
    private final short rightRGBBound = 255;

    private String transformationText = """
            1. DISK
            2. HEARTH
            3. LINEAR
            4. POLAR
            5. SINUSOIDAL
            6. SPHERICAL

            Введите цифрами через пробел, какие нелинейные трансформации вы хотите применить:\s""";

    private String affineChoiceText = """
        Выберите, как сгенерировать коэффициенты для афинного преобразования
        1. Рандомно
        2. Самостоятельно

        Ввод:\s""";

    private String trasformationChoiceText = """
        \nВыберите, как сгенерировать трансформации
        1. Рандомно
        2. Самостоятельно

        Ввод:\s""";

    private String symetryChoice = """
        \nДобавить симметрию?
        1. Да
        2. Нет

        Ввод:\s""";

    Scanner in = new Scanner(System.in);

    private Render render = new Render();

    @SuppressWarnings("checkstyle:MagicNumber")
    private AffineCoef getAffineCoef(ArrayList<Object> coef) {
        return new AffineCoef((Double) coef.get(0), (Double) coef.get(1), (Double) coef.get(2),
            (Double) coef.get(3), (Double) coef.get(4), (Double) coef.get(5), new RGB((Short) coef.get(6),
            (Short) coef.get(7), (Short) coef.get(8)));
    }

    private int getWidth() {
        return getNumInput(1, Integer.MAX_VALUE, "Введите ширину: ");
    }

    private int getHeight() {
        return getNumInput(1, Integer.MAX_VALUE, "Введите высоту: ");
    }

    private int getPoints() {
        return getNumInput(1, Integer.MAX_VALUE, "Введите кол-во точек: ");
    }

    private int getIterCount() {
        return getNumInput(1, Integer.MAX_VALUE, "Введите кол-во итераций: ");
    }

    private AffineCoef getAffineCoef(String affineCoefString) {
        String[] affineCoefs = affineCoefString.split(" ");
        try {
            if (((Short.parseShort(affineCoefs[rAfffineCoefIndex]) >= leftRGBBound
                && Short.parseShort(affineCoefs[rAfffineCoefIndex]) <= rightRGBBound
                && Short.parseShort(affineCoefs[gAfffineCoefIndex]) >= leftRGBBound
                && Short.parseShort(affineCoefs[gAfffineCoefIndex]) <= rightRGBBound
            && Short.parseShort(affineCoefs[bAfffineCoefIndex]) >= leftRGBBound
                && Short.parseShort(affineCoefs[bAffineCoefIndex]) <= rightRGBBound))) {
                return new AffineCoef(Double.parseDouble(affineCoefs[aAffineCoefIndex]),
                    Double.parseDouble(affineCoefs[bAffineCoefIndex]),
                    Double.parseDouble(affineCoefs[cAffineCoefIndex]),
                    Double.parseDouble(affineCoefs[dAffineCoefIndex]),
                    Double.parseDouble(affineCoefs[eAffineCoefIndex]),
                    Double.parseDouble(affineCoefs[fAffineCoefIndex]),
                    new RGB(Short.parseShort(affineCoefs[rAfffineCoefIndex]),
                        Short.parseShort(affineCoefs[gAfffineCoefIndex]),
                        Short.parseShort(affineCoefs[bAffineCoefIndex])));
            } else {
                System.out.println("RGB - от 0 до 255");
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private ArrayList<AffineCoef> getAffineCoefs(int affineCount) {
        ArrayList<AffineCoef> affineCoefs = new ArrayList<>();
        int i = 0;
        while (i < affineCount) {
            System.out.println("Введите a, b, c, d, e, f, R, G, B через пробел: ");
            String affineCoef = in.nextLine();
            if (getAffineCoef(affineCoef) != null) {
                affineCoefs.add(getAffineCoef(affineCoef));
                i++;
            } else {
                System.out.println("Некорректный ввод коэффициентов");
            }
        }
        return affineCoefs;
    }

    private ArrayList<Transform> getTransformations() {
        ArrayList<Transform> transformations = new ArrayList<>();
        while (true) {
            System.out.print(transformationText);
            String transformationsChoice = in.nextLine();
            try {
                String[] choicesNum = transformationsChoice.split(" ");
                int i = 0;
                while (i < choicesNum.length) {
                    Transform transforms = RandomTransform.TRANSFORMATIONS.get(Integer.parseInt(choicesNum[i]) - 1)
                        .getDeclaredConstructor().newInstance();
                    if (!transformations.contains(transforms)) {
                        transformations.add(transforms);
                        i += 1;
                    }
                }
                if (!transformations.isEmpty()) {
                    return transformations;
                } else {
                    System.out.println("Некорректный ввод");
                }
            } catch (Exception e) {
                System.out.println("Некорректный выбор");
            }
        }

    }


    @SuppressWarnings({"MissingSwitchDefault", "MagicNumber"})
    public void getStart() {
        int width = getWidth();
        int height = getHeight();
        int pointsNumber = getPoints();
        int iterationNumber = getIterCount();

        int affineCoefChoice = getNumInput(1, 2, affineChoiceText);
        ArrayList<AffineCoef> affineCoefs = new ArrayList<>();
        int affineCount = getNumInput(1, Integer.MAX_VALUE, "\nВведите кол-во афинных преобразований (число от 0 до "
            + "2147483647): ");
        switch (affineCoefChoice) {
            case 1 -> affineCoefs = (ArrayList<AffineCoef>) PointUtils.getAffineTransformationList(affineCount);
            case 2 -> affineCoefs = getAffineCoefs(affineCount);
        }
        ArrayList<AffineCoef> affineCoefsCopy = new ArrayList<>();
        affineCoefs.forEach(coef -> {
            affineCoefsCopy.add(new AffineCoef(coef.a(), coef.b(), coef.c(), coef.d(), coef.e(), coef.f(),
                new RGB(coef.rgb().r(), coef.rgb().g(), coef.rgb().b())));
        });

        int transformationsChoice = getNumInput(1, 2, trasformationChoiceText);
        ArrayList<Transform> transformations = new ArrayList<>();
        switch (transformationsChoice) {
            case 1 -> transformations = (ArrayList<Transform>) RandomTransform.getRandomTransformation();
            case 2 -> transformations = getTransformations();
        }

        int symmetryChoice = getNumInput(1, 2, symetryChoice);
        Boolean symmetry = false;

        switch (symmetryChoice) {
            case 1 -> symmetry = true;
            case 2 -> symmetry = false;
        }

        int threadNumber = getNumInput(1, Integer.MAX_VALUE, String.format("\nВведите кол-во потоков, которые"
            + " будут использоваться для рендера\n(доступное кол-во потоков - %d. "
            + "Всё, что больше - не даст увеличения производительности): ",
            Runtime.getRuntime().availableProcessors()));

        Instant start = Instant.now();
        List<List<Point>> renderImage = render.render(pointsNumber, affineCount, iterationNumber, width,
            height, symmetry, affineCoefs, transformations, threadNumber);
        Instant end = Instant.now();
        Correction correction = new Correction();
        correction.correction(renderImage);
        CreateImage image = new CreateImage();
        image.createImage(renderImage, "fractalFlame.png");
        printConfig(pointsNumber, affineCount, iterationNumber, width, height, symmetry, affineCoefsCopy,
            transformations, threadNumber);
        System.out.printf("\n\nПотрачено времени на генерацию - %d,%d секунд\n",
            Duration.between(start, end).toMillis() / 1000, Duration.between(start, end).toMillis() % 1000);
    }

    @SuppressWarnings("ParameterNumber")
    private void printConfig(int pointNumber, int affineCount, int iterationNumber, int width, int height,
        boolean symmetry,
        ArrayList<AffineCoef> affineCoefs, ArrayList<Transform> transformations, int threadNumber) {
        System.out.printf("""
            \nКОНФИГУРАЦИЯ

            Ширина: %d
            Высота: %d
            Кол-во точек: %d
            Кол-во итераций: %d
            Кол-во афинных преобразований: %d\n
            Симметрия: %s""",
            width, height, pointNumber, iterationNumber, affineCount,
            symmetry ? "да" : "нет");
        AtomicInteger count = new AtomicInteger(1);
        affineCoefs.forEach(affineCoef -> {
            System.out.printf("%d) a - %.2f, b - %.2f, c - %.2f, d - %.2f, e - %.2f," + " f - %.2f,"
                    + " R = %d, G = %d, B = %d\n", count.get(), affineCoef.a(), affineCoef.b(),
                affineCoef.c(), affineCoef.d(), affineCoef.e(), affineCoef.f(),
                affineCoef.rgb().r(), affineCoef.rgb().g(), affineCoef.rgb().b());
            count.addAndGet(1);
        });
        System.out.println("Трансформации: ");
        count.set(1);
        transformations.forEach(transformation -> {
            System.out.printf("%d) %s\n", count.get(), transformation.toString());
            count.addAndGet(1);
        });
        System.out.printf("Кол-во потоков: %d\n", threadNumber);
        System.out.printf("Процессор: %s", getCPUName());
    }

    private int getNumInput(int leftBorder, int rightBorder, String message) {

        System.out.print(message);

        int someIntValue;

        while (true) {
            if (in.hasNextInt()) {
                someIntValue = in.nextInt();
                in.nextLine();
                if (someIntValue < leftBorder || someIntValue > rightBorder) {
                    System.out.printf("Введенное число должно быть в диапазоне от %d до %d\n\n%s",
                        leftBorder, rightBorder, message);
                } else {
                    return someIntValue;
                }
            } else {
                in.nextLine();
                System.out.printf("Вы ввели не целое число!\n\n%s\n", message);
            }
        }
    }

    private String getCPUName() {
        String os = System.getProperty("os.name").toLowerCase();
        String command;
        String macDesignation = "mac";
        String errorMessage = "ошибка в получении названия процессора";

        if (os.contains("win")) {
            command = "wmic cpu get Name";
        } else if (os.contains("nix") || os.contains("nux") || os.contains(macDesignation)) {
            if (os.contains(macDesignation)) {
                command = "sysctl -n machdep.cpu.brand_string";
            } else {
                command = "lscpu | grep 'Model name'";
            }
        } else {
            return errorMessage;
        }

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder cpuName = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("Name") && !line.startsWith("Model name")) {
                    cpuName.append(line.split(":").length > 1 ? line.split(":")[1].trim() : line.trim());
                }
            }
            reader.close();

            return !cpuName.isEmpty() ? cpuName.toString() : "CPU name not found";
        } catch (Exception e) {
            return errorMessage;
        }
    }

    public void setScanner(Scanner scanner) {
        in = scanner;
    }
}
