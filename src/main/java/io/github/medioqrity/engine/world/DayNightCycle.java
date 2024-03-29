package io.github.medioqrity.engine.world;

import org.joml.Vector3f;

import io.github.medioqrity.engine.IO.Window;
import io.github.medioqrity.engine.graphics.DirectionalLight;
import io.github.medioqrity.engine.graphics.Fog;

/**
 * this class provides effects about day-night cycle
 * - window background according to day time
 * - lightening angle & intensity & colour
 */
public class DayNightCycle {

    private static double PI = Math.acos(-1);
    private static double THRESHOLD = 0.1;
    private static Vector3f nightColor = new Vector3f((float) 18.0 / 255, (float) 22.0 / 255, (float) 25.0 / 255);
    private static Vector3f dayColor = new Vector3f((float) 84.0 / 255, (float) 125.0 / 255, (float) 165. / 255);
    private static Vector3f duskColor = new Vector3f((float) 254.0 / 255, (float) 133.0 / 255, (float) 88.0 / 255);
    private static Vector3f dayLight = new Vector3f(1, 1, 1);

    private static Vector3f mixColor(Vector3f colorA, Vector3f colorB, double mixRatio) {
        assert 0 <= mixRatio && mixRatio <= 1;
        float r = (float) (colorA.x + (colorB.x - colorA.x) * mixRatio);
        float g = (float) (colorA.y + (colorB.y - colorA.y) * mixRatio);
        float b = (float) (colorA.z + (colorB.z - colorA.z) * mixRatio);
        return new Vector3f(r, g, b);
    }

    private static float mix(float a, float b, double mixRatio) {
        return a + (b - a) * (float) mixRatio;
    }

    public static void setDirectionalLight(double currentTimeRatio, DirectionalLight directionalLight,
                                           Window window) {
        double currentTimeRatioRad = currentTimeRatio * 2 * PI;
        directionalLight.setDirection(
                new Vector3f(
                        (float) -Math.cos(currentTimeRatioRad),
                        (float) Math.sin(currentTimeRatioRad),
                        0.27f * (float) Math.sin(currentTimeRatioRad)
                )
        );
        double tempIntensity = (Math.sin(currentTimeRatioRad) + 0.35) / (1 + 0.35);
        if (tempIntensity >= 0) {
            directionalLight.setIntensity((float) (0.65 * Math.sqrt(tempIntensity)));
        }
        if ((1 - THRESHOLD <= currentTimeRatio || currentTimeRatio <= THRESHOLD) ||
            (0.5 - THRESHOLD <= currentTimeRatio && currentTimeRatio <= 0.5 + THRESHOLD)) {
            if (currentTimeRatio >= 1 - THRESHOLD) currentTimeRatio -= 1;
            if (currentTimeRatio >= 0.5 - THRESHOLD) currentTimeRatio = 0.5 - currentTimeRatio;
            currentTimeRatio *= (1 / THRESHOLD); // currentTimeRatio in [-1, 1]
            Vector3f resultColor, resultLight;
            if (currentTimeRatio < 0) {
                resultColor = mixColor(nightColor, duskColor, currentTimeRatio + 1);
                resultLight = new Vector3f(resultColor);
            } else {
                resultColor = mixColor(duskColor, dayColor, currentTimeRatio);
                resultLight = mixColor(duskColor, dayLight, currentTimeRatio);
            }
            directionalLight.setColour(resultLight);
            window.setBackgroundColor(resultColor.x, resultColor.y, resultColor.z, 1.0f);
        } else if (currentTimeRatio > 0.5) {
            directionalLight.setColour(nightColor);
            window.setBackgroundColor(nightColor.x, nightColor.y, nightColor.z, 1.0f);
        } else {
            directionalLight.setColour(dayLight);
            window.setBackgroundColor(dayColor.x, dayColor.y, dayColor.z, 1.0f);
        }
    }

    public static void setFog(double currentTimeRatio, Fog fog) {
        float nightDensity = 0.008f;
        float dawnDensity = 0.0095f;
        float dayDensity = 0.005f;
        float duskDensity = 0.0075f;
        double backupRatio = currentTimeRatio;
        if ((1 - THRESHOLD <= currentTimeRatio || currentTimeRatio <= THRESHOLD) ||
                (0.5 - THRESHOLD <= currentTimeRatio && currentTimeRatio <= 0.5 + THRESHOLD)) {
            if (currentTimeRatio >= 1 - THRESHOLD) currentTimeRatio -= 1;
            if (currentTimeRatio >= 0.5 - THRESHOLD) currentTimeRatio = 0.5 - currentTimeRatio;
            currentTimeRatio *= (1 / THRESHOLD); // currentTimeRatio in [-1, 1]
            if (currentTimeRatio < 0) {
                if (backupRatio >= 1 - THRESHOLD) fog.setDensity(mix(nightDensity, dawnDensity, currentTimeRatio + 1));
                else fog.setDensity(mix(nightDensity, duskDensity, currentTimeRatio + 1));
            } else {
                if (backupRatio <= THRESHOLD) fog.setDensity(mix(dawnDensity, dayDensity, currentTimeRatio));
                else fog.setDensity(mix(duskDensity, dayDensity, currentTimeRatio));
            }
        } else if (currentTimeRatio > 0.5) {
            fog.setDensity(nightDensity);
        } else {
            fog.setDensity(dayDensity);
        }
    }
}
