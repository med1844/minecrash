package engine.IO;

import engine.Camera;

import static engine.world.TextureManager.*;
import static org.lwjgl.glfw.GLFW.*;

import engine.world.Scene;
import org.joml.Vector3f;

public class Input {
    private int centerX, centerY;
    private int dx = 0, dy = 0;
    private boolean inWindow = true;
    private boolean leftButtonPressed = false, rightButtonPressed = false;
    private boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private Window window;
    private Camera camera;
    private long lastUpdateTime;
    private static final float PI = (float)Math.acos(-1);
    private static float keyboardSpeed = 0.008f;
    private static float mouseSpeed = 0.0005f;
    private static final float DECAY_FACTOR = 0.85f;
    private static final float ACCELERATION_FACTOR = 0.3f;
    private int coolDownLeft = 0;
    private int coolDownRight = 0;
    private float frontSpeed = 0, upSpeed = 0, rightSpeed = 0;
    private Vector3f speed = new Vector3f(0);

    public Input() {
    }

    /**
     * this method initializes several callbacks, including:
     * - Mouse click callback -> left button pressed, right button pressed
     * - Key callback -> keys[]
     * - Mouse position callback -> mouse move
     * @param window sets centerX and centerY;
     */
    public void init(Window window, Camera camera) {
        this.window = window;
        this.camera = camera;
        centerX = window.getWidth() / 2;
        centerY = window.getHeight() / 2;
        glfwSetCursorPos(window.getWindowHandle(), centerX, centerY);

        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
            dx += centerX - (int)xpos;
            dy += centerY - (int)ypos;
            glfwSetCursorPos(window.getWindowHandle(), centerX, centerY);
        });

        glfwSetKeyCallback(window.getWindowHandle(), (windowHandle, key, scancode, action, mods) -> {
            if (scancode <= GLFW_KEY_LAST) {
                if (action == GLFW_PRESS) keys[scancode] = true;
                else if (action == GLFW_RELEASE) keys[scancode] = false;
            }
        });

        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });

        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS;
        });

        lastUpdateTime = System.currentTimeMillis();
    }

    public void setCenter(int x, int y) {
        centerX = x;
        centerY = y;
    }

    public boolean isKeyDown(int scancode) {
         return glfwGetKey(window.getWindowHandle(), scancode) == GLFW_PRESS;
    }

    private void limit(Vector3f v, float keyboardSpeed) {
        v.x = limit(v.x, 25 * keyboardSpeed);
        v.y = limit(v.y, 25 * keyboardSpeed);
        v.z = limit(v.z, 25 * keyboardSpeed);
    }

    private float limit(float a, float threshold) {
        return Math.max(Math.min(a, threshold), -threshold);
    }

    public void update(Vector3f selectedBlockPos, Scene scene, Vector3f normalVector) {
        long deltaTime = System.currentTimeMillis() - lastUpdateTime;
        coolDownLeft -= deltaTime;
        coolDownRight -= deltaTime;
        if (coolDownLeft < 0) coolDownLeft = 0;
        if (coolDownRight < 0) coolDownRight = 0;

        scene.update(deltaTime);

        camera.rotate(dx * deltaTime * mouseSpeed, dy * deltaTime * mouseSpeed);
        dx = 0;
        dy = 0;

        Vector3f horizontalDirection, right, up;

        if (isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            keyboardSpeed = 0.02f;
        } else {
            keyboardSpeed = 0.008f;
        }

        horizontalDirection = camera.getHorizontalDirection(deltaTime * keyboardSpeed);
        right = camera.getRight(deltaTime * keyboardSpeed);
        up = new Vector3f(0, deltaTime * keyboardSpeed * 3, 0);

        frontSpeed *= DECAY_FACTOR;
        upSpeed *= DECAY_FACTOR;
        rightSpeed *= DECAY_FACTOR;

        if (Math.abs(frontSpeed) < 1e-5) frontSpeed = 0;
        if (Math.abs(upSpeed) < 1e-5) upSpeed = 0;
        if (Math.abs(rightSpeed) < 1e-5) rightSpeed = 0;

        if (isKeyDown(GLFW_KEY_E)) {
            frontSpeed += ACCELERATION_FACTOR;
        }
        if (isKeyDown(GLFW_KEY_D)) {
            frontSpeed -= ACCELERATION_FACTOR;
        }
        if (isKeyDown(GLFW_KEY_F)) {
            rightSpeed += ACCELERATION_FACTOR;
        }
        if (isKeyDown(GLFW_KEY_S)) {
            rightSpeed -= ACCELERATION_FACTOR;
        }
        if (isKeyDown(GLFW_KEY_SPACE)) {
            upSpeed += ACCELERATION_FACTOR;
        }
        if (isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            upSpeed -= ACCELERATION_FACTOR;
        }

        if (isKeyDown(GLFW_KEY_ESCAPE)) {
            window.setShouldClose(true);
        }

        if (leftButtonPressed && selectedBlockPos != null && coolDownLeft == 0) {
            scene.destroyBlock(selectedBlockPos);
            coolDownLeft = 200;
        }

        if (rightButtonPressed && selectedBlockPos != null && normalVector != null && coolDownRight == 0) {
            scene.putBlock(selectedBlockPos.add(normalVector), GLASS);
            coolDownRight = 200;
        }

        frontSpeed = limit(frontSpeed, 2f);
        rightSpeed = limit(rightSpeed, 2f);
        upSpeed = limit(upSpeed, 2f);

        speed.zero();
        speed.add(horizontalDirection.mul(frontSpeed));
        speed.add(right.mul(rightSpeed));
        speed.add(up.mul(upSpeed));
//        System.out.println(speed);
        limit(speed, keyboardSpeed);
        camera.move(speed);

        lastUpdateTime = System.currentTimeMillis();
    }
}
