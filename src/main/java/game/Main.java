package game;

import engine.MainEngine;

public class Main {
    public static void main(String[] args) {
        MainEngine engine = new MainEngine(1280, 720, "minecrash", true);
        engine.start();
    }
}
