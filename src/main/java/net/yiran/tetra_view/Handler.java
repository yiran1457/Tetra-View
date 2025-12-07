package net.yiran.tetra_view;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.yiran.tetra_view.screen.ViewScreen;
import se.mickelus.tetra.items.modular.IModularItem;

public class Handler {
    public static void init() {
        MinecraftForge.EVENT_BUS.register(new Handler());
    }

    public boolean isSelected = false;
    public ItemStack viewItem;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemTooltip(ItemTooltipEvent event) {
        viewItem = event.getItemStack();
        if (!(viewItem.getItem() instanceof IModularItem)) return;
        isSelected = true;
        if (!Config.EnableTooltip.get()) return;
        if (ViewScreen.FromItem.isEmpty()) {
            event.getToolTip().add(1, Component.translatable("tetra_view.store", KeyMappingUtil.getKeyMappingName(KeyMappings.STORE)));
        } else if (viewItem.equals(ViewScreen.FromItem)) {
            event.getToolTip().add(1, Component.translatable("tetra_view.same"));
        } else {
            var name = ViewScreen.FromItem.getDisplayName();
            event.getToolTip().add(1, Component.translatable("tetra_view.tooltip", KeyMappingUtil.getKeyMappingName(KeyMappings.VIEW), name));
        }
    }

    @SubscribeEvent
    public void onKeyPressInGui(ScreenEvent.KeyPressed.Pre event) {
        if (!isSelected) return;
        var key = InputConstants.getKey(event.getKeyCode(), event.getScanCode());
        if (KeyMappings.VIEW.isActiveAndMatches(key)) {
            openGui(viewItem);
        } else if (KeyMappings.STORE.isActiveAndMatches(key) && !Screen.hasShiftDown()) {
            if (viewItem.getItem() instanceof IModularItem) {
                if (ViewScreen.FromItem.equals(viewItem)) {
                    ViewScreen.FromItem = ItemStack.EMPTY;
                } else {
                    ViewScreen.FromItem = viewItem;
                }
            } else {
                ViewScreen.FromItem = ItemStack.EMPTY;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            isSelected = false;
        } else {
            if (KeyMappings.VIEW.consumeClick()) {
                var mc = Minecraft.getInstance();
                openGui(mc.player.getMainHandItem());
            }
        }
    }

    public void openGui(ItemStack itemStack) {
        if (itemStack == null || !(itemStack.getItem() instanceof IModularItem)) return;
        var mc = Minecraft.getInstance();
        mc.setScreen(new ViewScreen(itemStack, mc.screen));
    }
}
