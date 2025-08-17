package net.yiran.tetra_view;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import se.mickelus.mutil.gui.*;
import se.mickelus.tetra.blocks.workbench.gui.WorkbenchStatsGui;
import se.mickelus.tetra.gui.stats.bar.GuiStatBarTool;
import se.mickelus.tetra.gui.stats.bar.GuiStatBase;

import java.awt.*;
import java.util.List;
import java.util.stream.Stream;

public class ViewScreen extends Screen {
    public static ItemStack FromItem = ItemStack.EMPTY;
    public ItemStack stack;
    public ItemStack to;
    public Screen lastScreen;
    public GuiElement statsBarGroup;
    public GuiElement statsToolBarGroup;
    public GuiElement statsCommonBarGroup;
    public GuiElement statsItemGroup;
    public double xOffset;
    public double yOffset;

    protected ViewScreen(ItemStack stack, Screen lastScreen) {
        super(Component.literal(""));
        var mc = Minecraft.getInstance();
        this.statsBarGroup = new GuiElement(0, 0, 0, 0);
        this.statsBarGroup.addChild(this.statsToolBarGroup = new GuiElement(0, 0, 0, 0));
        this.statsBarGroup.addChild(this.statsCommonBarGroup = new GuiElement(0, 0, 0, 0));
        this.statsBarGroup.addChild(this.statsItemGroup = new GuiElement(0, 0, 0, 0));
        init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        if (FromItem.isEmpty()) {
            this.stack = stack;
            this.to = FromItem;
        } else {
            this.stack = FromItem;
            this.to = stack;
        }
        this.lastScreen = lastScreen;
        updateBars();
        xOffset = 0;
        yOffset = 0;
    }

    @Override
    protected void init() {
        super.init();
        this.statsBarGroup.setX(width / 2);
        this.statsBarGroup.setY(height / 2);
    }

    void updateBars() {
        Stream<GuiStatBase> stream;
        var gt = new GuiTexture(-9,-9,34,34,new ResourceLocation("tetra_view","textures/slot1.png"));
        gt.setSpriteSize(34,34);
        if (FromItem.isEmpty()||stack.equals(to)) {
            GuiItem item = new GuiItem(-8, -8);
            item.setItem(this.stack);
            item.addChild(gt);
            this.statsItemGroup.addChild(item);
        } else {
            var pr = new GuiTexture(-25,-10,50,20,new ResourceLocation("tetra_view","textures/progress.png"));
            pr.setSpriteSize(50,20);
            this.statsItemGroup.addChild(pr);
            GuiItem item = new GuiItem(-8-23, -8);
            item.setItem(this.stack);
            this.statsItemGroup.addChild(item);
            item.addChild(gt);
            GuiItem item2 = new GuiItem(-8+23, -8);
            item2.setItem(this.to);
            item2.addChild(gt);
            this.statsItemGroup.addChild(item2);
        }
        try {
            Class<WorkbenchStatsGui> clazz = WorkbenchStatsGui.class;
            var staticBars_F = clazz.getDeclaredField("staticBars");
            var bars_F = clazz.getDeclaredField("bars");
            staticBars_F.setAccessible(true);
            bars_F.setAccessible(true);
            stream = Stream.concat(((List<GuiStatBase>) staticBars_F.get(clazz)).stream(), ((List<GuiStatBase>) bars_F.get(clazz)).stream());
            stream.filter(guiStatBase -> guiStatBase.shouldShow(minecraft.player, stack, to, null, null))
                    .forEach(guiStatBase -> {
                        guiStatBase.update(minecraft.player, stack, to, null, null);
                        if (guiStatBase instanceof GuiStatBarTool) {
                            this.realignBar(guiStatBase, this.statsToolBarGroup.getNumChildren(), true);
                            this.statsToolBarGroup.addChild(guiStatBase);
                        } else {
                            this.realignBar(guiStatBase, this.statsCommonBarGroup.getNumChildren(), false);
                            this.statsCommonBarGroup.addChild(guiStatBase);
                        }
                    });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    int indexToY(int index) {
        index /= 2;
        int line = 2;
        int extra = 1;
        while (index >= line + extra) {
            index -= line + extra;
            line++;
            extra++;
        }
        var result = index > line ? line : index;
        return result;
    }

    int indexToX(int index) {
        index /= 2;
        int line = 2;
        int extra = 1;
        while (index >= line + extra) {
            index -= line + extra;
            line++;
            extra++;
        }
        index -= line;
        var result = index >= 0 ? line - extra + index : extra;
        return result - 1;
    }

    void realignBar(GuiStatBase bar, int index, boolean isTool) {
        //var y =-17 * (index % 6 / 2) - 3;
        var y = -17 * indexToY(index) - 3;
        bar.setY(isTool ? -y + 30 : y - 12);
        bar.setAttachmentAnchor(GuiAttachment.bottomCenter);
        //int xOffset = 3 + index / 6 * 62;
        int xOffset = 3 + indexToX(index) * 62;
        if (index % 2 == 0) {
            bar.setX(xOffset);
            bar.setAttachmentPoint(GuiAttachment.bottomLeft);
            bar.setAlignment(GuiAlignment.left);
        } else {
            bar.setX(-xOffset);
            bar.setAttachmentPoint(GuiAttachment.bottomRight);
            bar.setAlignment(GuiAlignment.right);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        /*
        if(FromItem.isEmpty()){
            pGuiGraphics.renderItem(stack, minecraft.getWindow().getGuiScaledWidth() / 2 - 8 + (int) xOffset, minecraft.getWindow().getGuiScaledHeight() / 2 - 8 + (int) yOffset);
        }else {
            pGuiGraphics.renderItem(stack, minecraft.getWindow().getGuiScaledWidth() / 2 - 8 + (int) xOffset-20, minecraft.getWindow().getGuiScaledHeight() / 2 - 8 + (int) yOffset);
            pGuiGraphics.renderItem(to, minecraft.getWindow().getGuiScaledWidth() / 2 - 8 + (int) xOffset+20, minecraft.getWindow().getGuiScaledHeight() / 2 - 8 + (int) yOffset);
        }
        */
        statsBarGroup.updateFocusState((int) xOffset, (int) yOffset, pMouseX, pMouseY);
        statsBarGroup.draw(pGuiGraphics, (int) xOffset, (int) yOffset, width, height, pMouseX, pMouseY, 1);

        List<Component> tooltipLines = statsBarGroup.getTooltipLines();
        if (tooltipLines != null) {
            pGuiGraphics.renderComponentTooltip(this.font, tooltipLines, pMouseX, Math.max(pMouseY, 14));
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        xOffset += pDragX;
        yOffset += pDragY;
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics) {
        pGuiGraphics.fill(0, 0, width, height, -1, new Color(0, 0, 0, 145).getRGB());
    }

    @Override
    public void onClose() {
        minecraft.setScreen(lastScreen);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (this.minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
            this.onClose();
            return true;
        }
        return false;
    }
}
