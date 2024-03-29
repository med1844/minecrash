package io.github.medioqrity.engine.IO;

import com.sun.org.apache.regexp.internal.RE;
import org.joml.AABBf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import io.github.medioqrity.engine.graphics.HUD.Inventory;
import io.github.medioqrity.engine.world.ChunkUtils.Chunk;
import io.github.medioqrity.engine.world.Scene;
import io.github.medioqrity.engine.Camera;
import org.lwjgl.glfw.GLFWJoystickCallback;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static io.github.medioqrity.engine.world.TextureManager.*;

public class Input {
    private int centerX, centerY;
    private int dx = 0, dy = 0;
    private boolean inWindow = true;
    private boolean leftButtonPressed = false, rightButtonPressed = false, middleButtonPressed = false;
    private boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private boolean[] lastControllerKeys = new boolean[6];
    private Window window;
    private Camera camera;
    private long lastUpdateTime;
    private static final float PI = (float)Math.acos(-1);
    private static float keyboardSpeed = 0.008f;
    private static float mouseSpeed = 0.0005f;
    private static final float DECAY_FACTOR = 0.8f;
    private static final float ACCELERATION_FACTOR = 0.3f;
    private int coolDownLeft = 0;
    private int coolDownRight = 0;
    private int coolDownBackspace = 0;
    private float frontSpeed = 0, upSpeed = 0, rightSpeed = 0;
    private Vector3f speed = new Vector3f(0);
    private int currentChoseBlockID = GLASS;
    private boolean fly = true;
    private boolean floating = false;
    private double scrollY = 0;

    private static int FRONT = GLFW_KEY_W;
    private static int LEFT = GLFW_KEY_A;
    private static int BACK = GLFW_KEY_S;
    private static int RIGHT = GLFW_KEY_D;
    private static int UP = GLFW_KEY_SPACE;
    private static int DOWN = GLFW_KEY_LEFT_SHIFT;
    private static int DROP = GLFW_KEY_Q;
    private final float FAST = 0.02f;
    private final float SLOW = 0.008f;
    private final float GAMEPAD_FACTOR = 10;

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

        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xPos, yPos) -> {
            dx += centerX - (int)xPos;
            dy += centerY - (int)yPos;
            glfwSetCursorPos(window.getWindowHandle(), centerX, centerY);
        });

        glfwSetKeyCallback(window.getWindowHandle(), (windowHandle, key, scanCode, action, mods) -> {
            if (scanCode <= GLFW_KEY_LAST) {
                if (action == GLFW_PRESS) keys[scanCode] = true;
                else if (action == GLFW_RELEASE) keys[scanCode] = false;
            }
        });

        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });

        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS;
            middleButtonPressed = button == GLFW_MOUSE_BUTTON_MIDDLE && action == GLFW_PRESS;
        });

        glfwSetScrollCallback(window.getWindowHandle(), (windowHandle, dx, dy) -> scrollY += dy);

        lastUpdateTime = System.currentTimeMillis();
    }

    public void setCenter(int x, int y) {
        centerX = x;
        centerY = y;
    }

    public boolean isKeyDown(int scanCode) {
         return glfwGetKey(window.getWindowHandle(), scanCode) == GLFW_PRESS;
    }

    private void limit(Vector3f v, float keyboardSpeed) {
        v.x = limit(v.x, 25 * keyboardSpeed);
        v.y = limit(v.y, 25 * keyboardSpeed);
        v.z = limit(v.z, 25 * keyboardSpeed);
    }

    private float limit(float a, float threshold) {
        return Math.max(Math.min(a, threshold), -threshold);
    }

    private void updateGamePad() {
        ByteBuffer buttons = glfwGetJoystickButtons(GLFW_JOYSTICK_1);
        if (buttons != null) {
            if (buttons.get(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) == GLFW_PRESS && !lastControllerKeys[GLFW_GAMEPAD_BUTTON_LEFT_BUMPER]) {
                scrollY += 1;
                lastControllerKeys[GLFW_GAMEPAD_BUTTON_LEFT_BUMPER] = true;
            }
            if (buttons.get(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) == GLFW_RELEASE) {
                lastControllerKeys[GLFW_GAMEPAD_BUTTON_LEFT_BUMPER] = false;
            }

            if (buttons.get(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW_PRESS && !lastControllerKeys[GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER]) {
                scrollY -= 1;
                lastControllerKeys[GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER] = true;
            }
            if (buttons.get(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW_RELEASE) {
                lastControllerKeys[GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER] = false;
            }

            if (buttons.get(GLFW_GAMEPAD_BUTTON_X) == GLFW_PRESS) {
                keyboardSpeed = FAST;
            } else {
                keyboardSpeed = SLOW;
            }

            FloatBuffer axes = glfwGetJoystickAxes(GLFW_JOYSTICK_1);

            float MOVE_ACTIVATION = 0.4f;
            float DIRECTION_ACTIVATION = 0.1f;
            assert axes != null;

            float axisLeftX = axes.get(GLFW_GAMEPAD_AXIS_LEFT_X);
            float axisLeftY = axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y);
            float axisRightX = axes.get(GLFW_GAMEPAD_AXIS_RIGHT_X);
            float axisRightY = axes.get(GLFW_GAMEPAD_AXIS_RIGHT_Y);

            if (axisLeftX > MOVE_ACTIVATION || axisLeftX < -MOVE_ACTIVATION) {
                rightSpeed += axisLeftX * ACCELERATION_FACTOR;
            }
            if (axisLeftY > MOVE_ACTIVATION || axisLeftY < -MOVE_ACTIVATION) {
                frontSpeed -= axisLeftY * ACCELERATION_FACTOR;
            }
            if (axisRightX > DIRECTION_ACTIVATION || axisRightX < -DIRECTION_ACTIVATION) {
                dx -= axisRightX * GAMEPAD_FACTOR;
            }
            if (axisRightY > DIRECTION_ACTIVATION || axisRightY < -DIRECTION_ACTIVATION) {
                dy += axisRightY * GAMEPAD_FACTOR;
            }
        }
    }

    public void update(Vector3f selectedBlockPos, Scene scene, Vector3f normalVector, Inventory inventory) {
        updateGamePad();

        long deltaTime = System.currentTimeMillis() - lastUpdateTime;

        inventory.move(-(int) scrollY);
        scrollY = 0;

        coolDownLeft -= deltaTime;
        coolDownRight -= deltaTime;
        coolDownBackspace -= deltaTime;
        if (coolDownLeft < 0) coolDownLeft = 0;
        if (coolDownRight < 0) coolDownRight = 0;
        if (coolDownBackspace < 0) coolDownBackspace = 0;

        int x = (int) camera.getPosition().x;
        int y = (int) camera.getPosition().y;
        int z = (int) camera.getPosition().z;

        camera.rotate(dx * deltaTime * mouseSpeed, dy * deltaTime * mouseSpeed);
        dx = 0;
        dy = 0;

        Vector3f horizontalDirection, right, up;

        if (isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            keyboardSpeed = FAST;
        } else {
            keyboardSpeed = Math.max(SLOW, keyboardSpeed);
        }

        if (isKeyDown(GLFW_KEY_ENTER) && coolDownBackspace == 0) {
            fly ^= true;
            coolDownBackspace = 200;
        }

        if (isKeyDown(GLFW_KEY_ESCAPE)) {
            window.setShouldClose(true);
        }

        if (leftButtonPressed && selectedBlockPos != null && coolDownLeft == 0) {
            scene.destroyBlock(selectedBlockPos);
            coolDownLeft = 200;
        }

        if (middleButtonPressed && selectedBlockPos != null) {
            currentChoseBlockID = scene.chunkManager.getBlock((int) selectedBlockPos.x, (int) selectedBlockPos.y, (int) selectedBlockPos.z).getBlockID();
            inventory.set(currentChoseBlockID);
        }

        if (rightButtonPressed && selectedBlockPos != null && normalVector != null && coolDownRight == 0) {
            selectedBlockPos.add(normalVector);
            if (!((int) selectedBlockPos.x == x && (int) selectedBlockPos.y == y && (int) selectedBlockPos.z == z)) {
                scene.putBlock(selectedBlockPos, inventory.get());
                coolDownRight = 200;
            }
        }

        if (isKeyDown(DROP)) {
            inventory.drop();
        }

        horizontalDirection = camera.getHorizontalDirection(deltaTime * keyboardSpeed);
        right = camera.getRight(deltaTime * keyboardSpeed);

        frontSpeed *= DECAY_FACTOR;
        rightSpeed *= DECAY_FACTOR;

        if (Math.abs(frontSpeed) < 1e-5) frontSpeed = 0;
        if (Math.abs(rightSpeed) < 1e-5) rightSpeed = 0;

        if (fly) {
            up = new Vector3f(0, deltaTime * keyboardSpeed * 3, 0);
            upSpeed *= DECAY_FACTOR;
            if (Math.abs(upSpeed) < 1e-5) upSpeed = 0;
            floating = false;
            if (isKeyDown(FRONT)) {
                frontSpeed += ACCELERATION_FACTOR;
            }
            if (isKeyDown(BACK)) {
                frontSpeed -= ACCELERATION_FACTOR;
            }
            if (isKeyDown(RIGHT)) {
                rightSpeed += ACCELERATION_FACTOR;
            }
            if (isKeyDown(LEFT)) {
                rightSpeed -= ACCELERATION_FACTOR;
            }
            if (isKeyDown(UP)) {
                upSpeed += ACCELERATION_FACTOR;
            }
            if (isKeyDown(DOWN)) {
                upSpeed -= ACCELERATION_FACTOR;
            }

            frontSpeed = limit(frontSpeed, 2f);
            rightSpeed = limit(rightSpeed, 2f);
            upSpeed = limit(upSpeed, 2f);

            speed.zero();
            speed.add(horizontalDirection.mul(frontSpeed));
            speed.add(right.mul(rightSpeed));
            speed.add(up.mul(upSpeed));
            limit(speed, keyboardSpeed);
            camera.move(speed);
        } else {
            up = new Vector3f(0, deltaTime * SLOW * 3, 0);

            floating = true;
            AABBf cameraAABB = new AABBf(camera.getPosition().sub(0.4f, 1.8f, 0.4f), camera.getPosition().add(0.4f, 0.2f, 0.4f));
            if (cameraAABB.minY - (int) cameraAABB.minY < 1e-3) cameraAABB.minY = (int) cameraAABB.minY;
            for (int i = (int) cameraAABB.minX; i < (int) Math.ceil(cameraAABB.maxX) && floating; ++i) {
                for (int k = (int) cameraAABB.minZ; k < (int) Math.ceil(cameraAABB.maxZ) && floating; ++k) {
                    int j = (int) cameraAABB.minY - 1;
                    if (scene.isBlock(i, j, k, SOLID)) {
                        AABBf blockAABB = new AABBf(i, j, k, i + 1, j + 1, k + 1);
                        if (cameraAABB.testAABB(blockAABB)) {
                            floating = false;
                        }
                    }
                }
            }

            if (isKeyDown(FRONT)) {
                frontSpeed += ACCELERATION_FACTOR;
            }
            if (isKeyDown(BACK)) {
                frontSpeed -= ACCELERATION_FACTOR;
            }
            if (isKeyDown(RIGHT)) {
                rightSpeed += ACCELERATION_FACTOR;
            }
            if (isKeyDown(LEFT)) {
                rightSpeed -= ACCELERATION_FACTOR;
            }
            if (isKeyDown(UP)) {
                if (!floating) {
                    upSpeed += 30 * deltaTime * 0.001;
                    floating = true;
                } else {
                    upSpeed += 1.5 * deltaTime * 0.001;
                }
            }
            if (floating) {
                upSpeed -= 3 * deltaTime * 0.001;
            } else {
                upSpeed = 0;
            }
            frontSpeed = limit(frontSpeed, 0.5f);
            rightSpeed = limit(rightSpeed, 0.5f);
            upSpeed = limit(upSpeed, 5f);

            speed.zero();
            speed.add(horizontalDirection.mul(frontSpeed));
            speed.add(right.mul(rightSpeed));
            speed.add(up.mul(upSpeed));

            boolean xAxis = true, yAxis = true, zAxis = true;
            // try to add the speed dividing by Axis
            camera.move(speed.x, 0, 0);
            cameraAABB = new AABBf(camera.getPosition().sub(0.4f, 1.8f, 0.4f), camera.getPosition().add(0.4f, 0.2f, 0.4f));
            for (int i = (int) cameraAABB.minX; i < (int) Math.ceil(cameraAABB.maxX) && xAxis; ++i) {
                for (int j = (int) cameraAABB.minY; j < (int) Math.ceil(cameraAABB.maxY) && xAxis; ++j) {
                    for (int k = (int) cameraAABB.minZ; k < (int) Math.ceil(cameraAABB.maxZ) && xAxis; ++k) {
                        if (scene.isBlock(i, j, k, SOLID)) {
                            AABBf blockAABB = new AABBf(i, j, k, i + 1, j + 1, k + 1);
                            if (cameraAABB.testAABB(blockAABB)) {
                                xAxis = false;
                            }
                        }
                    }
                }
            }
            if (!xAxis) camera.move(-speed.x, 0, 0);


            camera.move(0, 0, speed.z);
            cameraAABB = new AABBf(camera.getPosition().sub(0.4f, 1.8f, 0.4f), camera.getPosition().add(0.4f, 0.2f, 0.4f));
            for (int i = (int) cameraAABB.minX; i < (int) Math.ceil(cameraAABB.maxX) && zAxis; ++i) {
                for (int j = (int) cameraAABB.minY; j < (int) Math.ceil(cameraAABB.maxY) && zAxis; ++j) {
                    for (int k = (int) cameraAABB.minZ; k < (int) Math.ceil(cameraAABB.maxZ) && zAxis; ++k) {
                        if (scene.isBlock(i, j, k, SOLID)) {
                            AABBf blockAABB = new AABBf(i, j, k, i + 1, j + 1, k + 1);
                            if (cameraAABB.testAABB(blockAABB)) {
                                zAxis = false;
                            }
                        }
                    }
                }
            }
            if (!zAxis) camera.move(0, 0, -speed.z);

            camera.move(0, speed.y, 0);
            cameraAABB = new AABBf(camera.getPosition().sub(0.4f, 1.8f, 0.4f), camera.getPosition().add(0.4f, 0.2f, 0.4f));
            for (int i = (int) cameraAABB.minX; i < (int) Math.ceil(cameraAABB.maxX) && yAxis; ++i) {
                for (int j = (int) cameraAABB.minY; j < (int) Math.ceil(cameraAABB.maxY) && yAxis; ++j) {
                    for (int k = (int) cameraAABB.minZ; k < (int) Math.ceil(cameraAABB.maxZ) && yAxis; ++k) {
                        if (scene.isBlock(i, j, k, SOLID)) {
                            AABBf blockAABB = new AABBf(i, j, k, i + 1, j + 1, k + 1);
                            if (cameraAABB.testAABB(blockAABB)) {
                                yAxis = false;
                            }
                        }
                    }
                }
            }
            if (!yAxis) {
                int j = (int) Math.ceil(camera.getPosition().y - 1.8f);
                for (; j < Chunk.getY() && scene.isBlock((int) camera.getPosition().x, j, (int) camera.getPosition().z, SOLID); ++j);
                camera.setY(j + 1.8f);
                upSpeed = 0;
            }
        }

        scene.update(deltaTime, camera.getPosition());
        lastUpdateTime = System.currentTimeMillis();
    }
}
