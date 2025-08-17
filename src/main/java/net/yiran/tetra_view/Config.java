package net.yiran.tetra_view;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<Boolean> EnableTooltip;

    static {
        EnableTooltip = BUILDER
                .define("enableTooltip", true);
        SPEC = BUILDER.build();
    }
}
