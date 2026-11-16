package com.example.refurbishedcams.client;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;

/**
 * Minimal gamepad reader using GLFW's gamepad API (standard Xbox-style layout).
 * Call poll() once per frame; then read the axes / rising-edge button presses.
 * Works with or without the Controllable mod installed.
 */
public class GamepadInput {
    private static final int BUTTONS = 15;
    private static final boolean[] prev = new boolean[BUTTONS];
    private static final boolean[] pressed = new boolean[BUTTONS];

    public static boolean connected = false;
    public static float lx, ly, rx, ry;

    public static void poll() {
        connected = false;
        lx = ly = rx = ry = 0F;
        java.util.Arrays.fill(pressed, false);

        for (int jid = GLFW.GLFW_JOYSTICK_1; jid <= GLFW.GLFW_JOYSTICK_LAST; jid++) {
            if (!GLFW.glfwJoystickPresent(jid) || !GLFW.glfwJoystickIsGamepad(jid)) continue;
            try (MemoryStack stack = MemoryStack.stackPush()) {
                GLFWGamepadState st = GLFWGamepadState.malloc(stack);
                if (!GLFW.glfwGetGamepadState(jid, st)) continue;
                connected = true;
                lx = dead(st.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X));
                ly = dead(st.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y));
                rx = dead(st.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X));
                ry = dead(st.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y));
                for (int b = 0; b < BUTTONS; b++) {
                    boolean down = st.buttons(b) == GLFW.GLFW_PRESS;
                    pressed[b] = down && !prev[b];
                    prev[b] = down;
                }
            }
            break;
        }
    }

    /** True only on the frame the button went down. Use GLFW.GLFW_GAMEPAD_BUTTON_* constants. */
    public static boolean pressed(int button) {
        return connected && button >= 0 && button < BUTTONS && pressed[button];
    }

    private static float dead(float v) {
        float a = Math.abs(v);
        if (a < 0.2F) return 0F;
        return Math.signum(v) * (a - 0.2F) / 0.8F;   // rescale so it starts smoothly from 0
    }
}
