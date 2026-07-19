package com.example.examplemod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TelemetryServerConfig {
    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final ForgeConfigSpec.IntValue DEFAULT_POLL_RATE_TICKS;
    public static final ForgeConfigSpec.IntValue MINIMUM_POLL_RATE_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DEFAULT_POLL_RATE_TICKS = builder
                .comment("Default poll rate For Telemetry Node Block in ticks. This is the value that a Telemetry Node Block's poll rate will use when first placed.")
                .defineInRange("defaultPollRateTicks", 20, 1, Integer.MAX_VALUE);

        MINIMUM_POLL_RATE_TICKS = builder
                .comment("Minimum poll rate For Telemetry Node Block in ticks. This is the lowest value that a Telemetry Node Block's poll rate can be set to. If set below, it will be clamped to this value.")
                .defineInRange("minimumPollRateTicks", 1, 1, Integer.MAX_VALUE);

        SERVER_CONFIG = builder.build();
    }
}
