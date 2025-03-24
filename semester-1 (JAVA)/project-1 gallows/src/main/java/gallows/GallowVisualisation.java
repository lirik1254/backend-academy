package gallows;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GallowVisualisation {

    @Getter
    @SuppressWarnings("MagicNumber")
    public static int numberOfAttempts = 10;

    @SuppressWarnings("LineLength")
    public static final String GALLOW = """


          ___________________________________
          |               |__________/      |
          |___ \\      /___|                 |
                \\    /                      |
                 |  |                       |
                 |  |                     ____
                 |  |                    /    \\
                 |  |                    \\____/
                 |  |                       |
                 |  |                     / | \\
                 |  |                    /  |  \\
                 |  |                   /   |   \\
                 |  |                       |
                 |  |                       |
                 |  |                       |
                 |  |                       |
                 |  |                      / \\
                 |  |                     /   \\
           ________________              /     \\
          /                \\
         /                  \\
        /                    \\
        """;

    @SuppressWarnings("RegexpSinglelineJava")
    public void visualisationByAttempts(int attemps) {
        System.out.println(GALLOW.substring(0, attemps == 0 ? GALLOW.length()
            : GALLOW.length() / numberOfAttempts * (numberOfAttempts - attemps)) + "\n\n");
    }

//    public static void main(String[] args) {
//
//    }
}
