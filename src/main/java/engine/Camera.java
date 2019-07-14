package engine;

import org.joml.Vector3f;

public class Camera {

    private final float PI = (float)Math.acos(-1);
    private Vector3f position;
    private float horizontalAngle; // rad
    private float verticalAngle; // rad

    public Camera() {
        position = new Vector3f(10, 70, 10);
        horizontalAngle = PI / 4;
        verticalAngle = 0;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void move(Vector3f offset) {
        position.x += offset.x;
        position.y += offset.y;
        position.z += offset.z;
    }

    public void setRotation(float horizontal, float vertical) {
        horizontalAngle = horizontal;
        verticalAngle = vertical;
    }

    public void rotate(float horizontal, float vertical) {
        horizontalAngle += horizontal;
        verticalAngle += vertical;
        if (horizontalAngle < 0) horizontalAngle += 2 * PI;
        if (horizontalAngle > 2 * PI) horizontalAngle -= 2 * PI;
        if (verticalAngle < -PI / 2) verticalAngle = -PI / 2 + 0.0001f;
        if (verticalAngle > PI / 2) verticalAngle = PI / 2 - 0.0001f;
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public Vector3f getDirection() {
        return new Vector3f(
                (float)Math.cos(verticalAngle) * (float)Math.sin(horizontalAngle),
                (float)Math.sin(verticalAngle),
                (float)Math.cos(verticalAngle) * (float)Math.cos(horizontalAngle)
        );
    }

    public Vector3f getHorizontalDirection(float speed) {
        return new Vector3f(
                (float)Math.sin(horizontalAngle) * speed,
                0,
                (float)Math.cos(horizontalAngle) * speed
        );
    }

    public Vector3f getRight(float speed) {
        return new Vector3f(
                (float)Math.sin(horizontalAngle - PI / 2) * speed,
                0,
                (float)Math.cos(horizontalAngle - PI / 2) * speed
        );
    }

}
