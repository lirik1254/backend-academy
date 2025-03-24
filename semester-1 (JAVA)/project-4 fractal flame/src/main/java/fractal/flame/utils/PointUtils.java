package fractal.flame.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PointUtils {

    public  Random rand = new Random();

    private final short leftRGBBound = 0;
    private final short rightRGBBound = 255;

    public Point getNewPoint(Point point, AffineCoef affineCoef) {
        double newX = affineCoef.a() * point.x() + affineCoef.b() * point.y() + affineCoef.c();
        double newY = affineCoef.d() * point.x() + affineCoef.e() * point.y() + affineCoef.f();
        return new Point(newX, newY, affineCoef.rgb(), 0, 0);
    }

    private HashMap<String, Double> getCoef() {
        HashMap<String, Double> randomCoefWithRestrictions = getRandomCoef();
        while (!checkRestrictions(randomCoefWithRestrictions)) {
            randomCoefWithRestrictions = getRandomCoef();
        }
        return randomCoefWithRestrictions;
    }

    private HashMap<String, Double> getRandomCoef() {
        HashMap<String, Double> coef = new HashMap<>();
        coef.put("a", rand.nextDouble(-1, 1));
        coef.put("b", rand.nextDouble(-1, 1));
        coef.put("c", rand.nextDouble(-1, 1));
        coef.put("d", rand.nextDouble(-1, 1));
        coef.put("e", rand.nextDouble(-1, 1));
        coef.put("f", rand.nextDouble(-1, 1));
        return coef;
    }

    private boolean checkRestrictions(HashMap<String, Double> coefMap) {
        return Math.pow(coefMap.get("a"), 2) + Math.pow(coefMap.get("b"), 2) < 1
            && Math.pow(coefMap.get("b"), 2) + Math.pow(coefMap.get("e"), 2) < 1
            && Math.pow(coefMap.get("a"), 2) + Math.pow(coefMap.get("b"), 2)
            + Math.pow(coefMap.get("d"), 2) + Math.pow(coefMap.get("e"), 2)
            < 1 + Math.pow(coefMap.get("a") * coefMap.get("e") - coefMap.get("b") - coefMap.get("d"), 2);
    }

    public RGB getRandRGB() {
        short r = (short) rand.nextInt(leftRGBBound, rightRGBBound);
        short g = (short) rand.nextInt(leftRGBBound, rightRGBBound);
        short b = (short) rand.nextInt(leftRGBBound, rightRGBBound);
        return new RGB(r, g, b);
    }

    public List<AffineCoef> getAffineTransformationList(int n) {
        List<AffineCoef> affineCoefList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            HashMap<String, Double> coefs = getCoef();
            AffineCoef affineCoef = new AffineCoef(coefs.get("a"), coefs.get("b"),
                coefs.get("c"), coefs.get("d"), coefs.get("e"), coefs.get("f"),
                getRandRGB());
            affineCoefList.add(affineCoef);
        }
        return affineCoefList;
    }
}
