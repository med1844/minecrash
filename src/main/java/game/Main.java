package Main;

import Engine.MainEngine;

public class Main {
    public static void main(String[] args) {
        MainEngine engine = new MainEngine(1920, 1080, "minecrash", false);
        engine.run();
    }
}
