package com.example.refurbishedcams.client;

import com.example.refurbishedcams.compat.CameraProgram;
import com.example.refurbishedcams.data.CameraEntry;
import com.mrcrayfish.furniture.refurbished.computer.client.DisplayableProgram;
import com.mrcrayfish.furniture.refurbished.computer.client.Scene;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** UI of the Cameras program (runs inside the Computer window, max 224x108). */
public class CameraGraphics extends DisplayableProgram<CameraProgram> {
    public CameraGraphics(CameraProgram program) {
        super(program, 224, 108);
        this.setScene(new ListScene(this));
    }

    public static class ListScene extends Scene {
        private static final int ROWS = 4;

        private final CameraGraphics graphics;
        private final Button[] rows = new Button[ROWS];
        private final Button up, down, refresh;
        private int scroll = 0;
        private int selected = 0;   // highlighted row (gamepad navigation)

        public ListScene(CameraGraphics graphics) {
            this.graphics = graphics;
            for (int i = 0; i < ROWS; i++) {
                final int slot = i;
                rows[i] = this.addWidget(Button.builder(Component.empty(), b -> open(scroll + slot))
                        .size(216, 16).build());
            }
            up = this.addWidget(Button.builder(Component.literal("^"), b -> scroll(-1)).size(20, 14).build());
            down = this.addWidget(Button.builder(Component.literal("v"), b -> scroll(1)).size(20, 14).build());
            refresh = this.addWidget(Button.builder(Component.translatable("gui.refurbishedcams.refresh"), b -> {
                scroll = 0;
                CameraClientState.request();
            }).size(60, 14).build());
            CameraClientState.request(); // scan as soon as the program opens
        }

        private void scroll(int d) {
            int max = Math.max(0, CameraClientState.cams.size() - ROWS);
            scroll = Math.max(0, Math.min(max, scroll + d));
        }

        private void open(int index) {
            List<CameraEntry> cams = CameraClientState.cams;
            if (index < 0 || index >= cams.size()) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            // Close the Computer cleanly (releases the container), then show the live view.
            // Inside the view you can switch cameras with Q/E, the < > buttons or the gamepad bumpers.
            mc.player.closeContainer();
            mc.setScreen(new CameraViewScreen(cams, index, null));
        }

        /** D-pad up/down = select, X = open, Y = refresh (Controllable's A still clicks the focused button). */
        private void handleGamepad() {
            GamepadInput.poll();
            if (!GamepadInput.connected) return;
            int size = CameraClientState.cams.size();
            if (size > 0) {
                if (GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP)) selected = Math.max(0, selected - 1);
                if (GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN)) selected = Math.min(size - 1, selected + 1);
                selected = Math.min(selected, size - 1);
                if (selected < scroll) scroll = selected;
                if (selected >= scroll + ROWS) scroll = selected - ROWS + 1;
                if (GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_X)) open(selected);
            }
            if (GamepadInput.pressed(GLFW.GLFW_GAMEPAD_BUTTON_Y)) {
                scroll = 0;
                selected = 0;
                CameraClientState.request();
            }
        }

        private void refreshRows() {
            List<CameraEntry> cams = CameraClientState.cams;
            var player = Minecraft.getInstance().player;
            for (int i = 0; i < ROWS; i++) {
                int idx = scroll + i;
                boolean has = CameraClientState.scanned && idx < cams.size();
                rows[i].visible = has;
                rows[i].active = has;
                if (has) {
                    CameraEntry c = cams.get(idx);
                    int dist = player == null ? 0 : (int) Math.sqrt(player.blockPosition().distSqr(c.pos()));
                    String mark = (GamepadInput.connected && idx == selected) ? "> " : "";
                    rows[i].setMessage(Component.literal(mark + "Cam " + (idx + 1) + "  " + c.pos().getX() + ", "
                            + c.pos().getY() + ", " + c.pos().getZ() + "  " + dist + " m"));
                }
            }
            up.active = scroll > 0;
            down.active = scroll + ROWS < cams.size();
        }

        @Override
        public void updateWidgets(int contentStart, int contentTop) {
            for (int i = 0; i < ROWS; i++) rows[i].setPosition(contentStart + 4, contentTop + 16 + i * 18);
            up.setPosition(contentStart + 4, contentTop + 90);
            down.setPosition(contentStart + 28, contentTop + 90);
            refresh.setPosition(contentStart + 160, contentTop + 90);
        }

        @Override
        public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            handleGamepad();
            refreshRows();
            Font font = Minecraft.getInstance().font;
            g.fill(0, 0, graphics.getWidth(), graphics.getHeight(), 0xFF1B2833);
            if (!CameraClientState.scanned) {
                g.drawString(font, Component.translatable("gui.refurbishedcams.scanning"), 6, 5, 0xFFFF55);
            } else {
                g.drawString(font, Component.translatable("gui.refurbishedcams.header",
                        CameraClientState.cams.size(), CameraClientState.radius), 6, 5, 0xFFFFFF);
                if (CameraClientState.cams.isEmpty()) {
                    g.drawString(font, Component.translatable("gui.refurbishedcams.none"), 6, 30, 0xFF5555);
                }
            }
        }
    }
}
