package fractal.flame;

import fractal.flame.utils.Point;
import java.util.List;

public class Correction {

    public static double max = 0.0;
    @SuppressWarnings("checkstyle:MagicNumber")
    public static double gamma = 2.2;

    public void correction(List<List<Point>> render) {

        for (int i = 0; i < render.size(); i++) {
            for (int j = 0; j < render.getFirst().size(); j++) {
                if (render.get(i).get(j).hitsNumber() != 0) {
                    render.get(i).get(j).normal((Math.log10(render.get(i).get(j).hitsNumber())));
                    max = Math.max(max, render.get(i).get(j).normal());
                }
            }
        }

        for (int i = 0; i < render.size(); i++) {
            for (int j = 0; j < render.getFirst().size(); j++) {
                render.get(i).get(j).normal(render.get(i).get(j).normal() / max);
                short r =
                    (short) (render.get(i).get(j).rgb().r() * Math.pow(render.get(i).get(j).normal(), (1.0 / gamma)));
                short g =
                    (short) (render.get(i).get(j).rgb().g() * Math.pow(render.get(i).get(j).normal(), (1.0 / gamma)));
                short b =
                    (short) (render.get(i).get(j).rgb().b() * Math.pow(render.get(i).get(j).normal(), (1.0 / gamma)));
                render.get(i).get(j).rgb().r(r);
                render.get(i).get(j).rgb().g(g);
                render.get(i).get(j).rgb().b(b);
            }
        }
    }
}
