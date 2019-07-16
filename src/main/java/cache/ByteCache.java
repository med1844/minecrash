package cache;

import java.util.ArrayList;
import java.util.List;

public class ByteCache {

    private static int a = 256;
    private static List b = new ArrayList();
    private static List c = new ArrayList();
    private static List d = new ArrayList();
    private static List e = new ArrayList();

    public static synchronized byte[] getByteCache(int i) {
        byte[] aint;

        if (i <= 256) {
            if (b.isEmpty()) {
                aint = new byte[256];
                c.add(aint);
                return aint;
            } else {
                aint = (byte[]) b.remove(b.size() - 1);
                c.add(aint);
                return aint;
            }
        } else if (i > a) {
            a = i;
            d.clear();
            e.clear();
            aint = new byte[a];
            e.add(aint);
            return aint;
        } else if (d.isEmpty()) {
            aint = new byte[a];
            e.add(aint);
            return aint;
        } else {
            aint = (byte[]) d.remove(d.size() - 1);
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
