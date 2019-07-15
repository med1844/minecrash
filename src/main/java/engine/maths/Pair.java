package engine.maths;


public class Pair implements Comparable<Pair> {
    public int first;
    public int second;

    public Pair() {
        first = 0;
        second = 0;
    }

    public Pair(int a, int b) {
        first = a;
        second = b;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (obj instanceof Pair) {
            Pair pair = (Pair) obj;
            if (this.first == pair.first && this.second == pair.second) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Pair o) {
        if (this.first != o.first) return this.first - o.first;
        else return this.second - o.second;
    }

    @Override
    public int hashCode() {
        return (first * (second * first * 15731 + 789221) + 1376312589) & 0x7fffffff;
    }


}
