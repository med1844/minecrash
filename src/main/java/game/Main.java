package game;

import engine.MainEngine;

public class Main {
    public static void main(String[] args) {
        MainEngine engine = new MainEngine(800, 600, "minecrash", true);
        engine.run();
    }
}
