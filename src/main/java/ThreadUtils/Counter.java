package ThreadUtils;

public class Counter {

    private int cnt;

    public synchronized void add() {
        ++cnt;
    }

    public synchronized void sub() {
        --cnt;
    }

    public synchronized boolean isEmpty() {
        return cnt == 0;
    }

    public synchronized void clear() {
        cnt = 0;
    }
}
