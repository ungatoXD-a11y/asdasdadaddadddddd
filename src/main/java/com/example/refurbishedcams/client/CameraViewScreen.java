package com.example.refurbishedcams.client;

import com.example.refurbishedcams.data.CameraEntry;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Live view through the security cameras.
 *
 * Mouse/keyboard: hold LEFT MOUSE and drag, or arrows / WASD to look. Q / E (or PageUp / PageDown,
 *                 or the < > buttons) switch to the previous / next camera. ESC exits.
 * Gamepad:        right stick = look, LB / RB (or D-pad left/right) = switch camera, B / Back = exit.
 */
public class CameraViewScreen extends Screen {
    private static final float DRAG_SENSITIVITY = 0.3F;   // degrees per pixel
    private static final float KEY_SPEED = 90F;           // degrees per second
    private static final float PAD_SPEED = 120F;          // degrees per second at full stick

    private final List<CameraEntry> cams;
    private final Screen parent;
    private int index;

    private ArmorStand camEntity;
    private Entity previousCamera;
    private boolean previousHideGui;

    private float yaw;
    private float pitch;
    private long lastMillis;

    public CameraViewScreen(List<CameraEntry> cams, int index, Screen parent) {
        super(Component.literal("Camera"));
        this.cams = cams;
        this.index = Math.max(0, Math.min(cams.size() - 1, index));
        this.parent = parent;
    }

    private CameraEntry entry() {
        return cams.get(index);
    }

    @Override
    protected void init() {
        // Widgets are rebuilt on every init() (e.g. window resize)
        int cx = width / 2;
        addRenderableWidget(Button.builder(Component.literal("<"), b -> switchCamera(-1))
                .bounds(cx - 90, height - 32, 30, 20).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> switchCamera(1))
                .bounds(cx + 60, height - 32, 30, 20).build());

        Minecraft mc = Minecraft.getInstance();
        if (camEntity != null || mc.level == null || cams.isEmpty()) return;
        previousCamera = mc.getCameraEntity();
        previousHideGui = mc.options.hideGui;

        camEntity = new ArmorStand(EntityType.ARMOR_STAND, mc.level);
        camEntity.setInvisible(true);
        camEntity.setNoGravity(true);
        camEntity.noPhysics = true;

        placeAtCurrent();
        lastMillis = Util.getMillis();

        mc.setCameraEntity(camEntity);
        mc.options.hideGui = true;
    }

    /** Moves the view to the current camera, just in front of its lens, looking where the lens points. */
    private void placeAtCurrent() {
        CameraEntry e = entry();
        Direction facing = Direction.fromYRot(e.yaw());
        double x = e.pos().getX() + 0.5 + facing.getStepX() * 0.52;
        double z = e.pos().getZ() + 0.5 + facing.getStepZ() * 0.52;
        double y = e.pos().getY() + 0.5 - camEntity.getEyeHeight();
        camEntity.setPos(x, y, z);
        camEntity.xo = x;
        camEntity.yo = y;
        camEntity.zo = z;
        yaw = e.yaw();
        pitch = 0F;
        applyRotation();
    }

    private void switchCamera(int delta) {
        if (camEntity == null || cams.size() < 2) return;
        index = Math.floorMod(index + delta, cams.size());
        placeAtCurrent();
    }

    /**
     * Living entities use their HEAD rotation as the view direction, so every rotation
     * field (current AND previous) is set to the same value: no interpolation = no jitter.
     */
    private void applyRotation() {
        if (camEntity == null) return;
        pitch = Math.max(-90F, Math.min(90F, pitch));
        camEntity.setYRot(yaw);
        camEntity.setXRot(pitch);
        camEntity.yRotO = yaw;
        camEntity.xRotO = pitch;
        camEntity.setYHeadRot(yaw);
        camEntity.yHeadRotO = yaw;
        camEntity.yBodyRot = yaw;
        camEntity.yBodyRotO = yaw;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        if (camEntity == null) {
            onClose();
            return;
        }
        long now = Util.getMillis();
        float dt = Math.min(0.1F, (now - lastMillis) / 1000F);
        lastMillis = now;

        // Keyboard look (polled every frame, no key-repeat delay)
        long window = Minecraft.getInstance().getWindow().getWindow();
        float step = KEY_SPEED * dt;
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_A)) yaw -= step;
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_D)) yaw += step;
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_UP) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_W)) pitch -= step;
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_DOWN) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_S)) pitch += step;

        // Gamepad
        GamepadInput.poll();
        if (GamepadInput.connected) {
            yaw += GamepadInput.rx * PAD_SPEED * dt;
            pitch += GamepadInput.ry * PAD_SPEED * dt;
            if (GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
                    || GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT)) switchCamera(-1);
            if (GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
                    || GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT)) switchCamera(1);
            if (GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_B)
                    || GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_BACK)) {
                onClose();
                return;
            }
        }
        applyRotation();

        // HUD (no background: we want to see the world through the camera)
        CameraEntry e = entry();
        boolean blink = (now / 500) % 2 == 0;
        if (blink) g.fill(10, 10, 18, 18, 0xFFFF2222);
        g.drawString(font, "REC  Cam " + (index + 1) + "/" + cams.size() + "  [" + e.pos().getX() + ", "
                + e.pos().getY() + ", " + e.pos().getZ() + "]", 24, 10, 0xFFFFFF);
        g.drawCenteredString(font, "Cam " + (index + 1) + " / " + cams.size(), width / 2, height - 26, 0xFFFFFF);
        g.drawString(font, Component.translatable(GamepadInput.connected
                ? "gui.refurbishedcams.view_hint_pad" : "gui.refurbishedcams.view_hint"), 10, height - 50, 0xCCCCCC);

        super.render(g, mx, my, pt);   // draws the < > buttons
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (key == GLFW.GLFW_KEY_Q || key == GLFW.GLFW_KEY_PAGE_UP) { switchCamera(-1); return true; }
        if (key == GLFW.GLFW_KEY_E || key == GLFW.GLFW_KEY_PAGE_DOWN) { switchCamera(1); return true; }
        return super.keyPressed(key, scan, mods);   // ESC closes
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (button == 0) {
            yaw += (float) dx * DRAG_SENSITIVITY;
            pitch += (float) dy * DRAG_SENSITIVITY;
            applyRotation();
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public void removed() {
        Minecraft mc = Minecraft.getInstance();
        if (camEntity == null) return;
        mc.setCameraEntity(previousCamera != null ? previousCamera : mc.player);
        mc.options.hideGui = previousHideGui;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
