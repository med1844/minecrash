package cache;

import java.util.ArrayList;
import java.util.List;

public class FloatCache {
    private static int a = 256;
    private static List b = new ArrayList();
    private static List c = new ArrayList();
    private static List d = new ArrayList();
    private static List e = new ArrayList();

    public static synchronized float[] getFloatCache(int i) {
        float[] aint;

        if (i <= 256) {
            if (b.isEmpty()) {
                aint = new float[256];
                c.add(aint);
                return aint;
            } else {
                aint = (float[]) b.remove(b.size() - 1);
                c.add(aint);
                return aint;
            }
        } else if (i > a) {
            a = i;
            d.clear();
            e.clear();
            aint = new float[a];
            e.add(aint);
            return aint;
        } else if (d.isEmpty()) {
            aint = new float[a];
            e.add(aint);
            return aint;
        } else {
            aint = (float[]) d.remove(d.size() - 1);
            e.add(aint);
            return aint;
        }
    }

    public static synchronized void update() {
        if (!d.isEmpty()) {
            d.remove(d.size() - 1);
        }

        if (!b.isEmpty()) {
            b.remove(b.size() - 1);
        }

        d.addAll(e);
        b.addAll(c);
        e.clear();
        c.clear();
    }

    public static synchronized String b() {
        return "cache: " + d.size() + ", tcache: " + b.size() + ", allocated: " + e.size() + ", tallocated: " + c.size();
    }
}
