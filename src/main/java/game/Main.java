package game;

import engine.MainEngine;

public class Main {
    public static void main(String[] args) {
        MainEngine engine = new MainEngine(1400, 900, "minecrash", true);
        engine.start();
    }
}
