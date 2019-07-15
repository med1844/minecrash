package engine.maths;

public class NoiseMath {

    /*
     * return random double [-1.0,1.0]
     * */
    public double Noise2D(int x, int y) {
        int n = x + y * 57;
        n = (n << 13) ^ n;
        return (1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }


    /*
     * return random double [-1.0,1.0]
     * */
    public double Noise(int x) {
        x = (x << 13) ^ x;
        return (1.0 - ((x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }


    public static double CosineInterpolate(double a, double b, double x) {
        double ft = x * 3.1415927;
        double f = (1 - Math.cos(ft)) * 0.5;
        return a * (1 - f) + b * f;
    }

    public static double LinearInterpolate(double a, double b, double x) {
        return a * (1 - x) + b * x;
    }

    /*
     * return a interpolate value between [a,b]
     * */
    public static double LinearInterpolateImproved(double a, double b, double x) {
        return x < 0.0D ? a : (x > 1.0D ? b : a + (b - a) * x);
    }


};


