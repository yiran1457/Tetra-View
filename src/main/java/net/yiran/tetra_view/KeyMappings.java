package net.yiran.tetra_view;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import se.mickelus.tetra.client.keymap.TetraKeyMappings;

public class KeyMappings {
    public static KeyMapping VIEW = new KeyMapping(
            "tetra_view.view",
            GLFW.GLFW_KEY_V,
            TetraKeyMappings.bindingGroup
    );
    public static KeyMapping STORE = new KeyMapping(
            "tetra_view.store",
            GLFW.GLFW_KEY_C,
            TetraKeyMappings.bindingGroup
    );
}
