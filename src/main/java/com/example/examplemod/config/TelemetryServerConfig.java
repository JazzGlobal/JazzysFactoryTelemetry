package com.example.examplemod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TelemetryServerConfig {
    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final ForgeConfigSpec.IntValue DEFAULT_POLL_RATE_TICKS;
    public static final ForgeConfigSpec.IntValue MINIMUM_POLL_RATE_TICKS;
    public static final ForgeConfigSpec.IntValue MAX_QUEUE_SIZE;
    public static final ForgeConfigSpec.IntValue QUEUE_SEND_DEFAULT;
    public static final ForgeConfigSpec.IntValue QUEUE_SEND_RATE;
    public static final ForgeConfigSpec.IntValue QUEUE_SEND_DELAY;
    public static final ForgeConfigSpec.IntValue MAX_RETRIES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        DEFAULT_POLL_RATE_TICKS = builder
                .comment("Default poll rate For Telemetry Node Block in ticks. This is the value that a Telemetry Node Block's poll rate will use when first placed.")
                .defineInRange("defaultPollRateTicks", 20, 1, Integer.MAX_VALUE);

        MINIMUM_POLL_RATE_TICKS = builder
                .comment("Minimum poll rate For Telemetry Node Block in ticks. This is the lowest value that a Telemetry Node Block's poll rate can be set to. If set below, it will be clamped to this value.")
                .defineInRange("minimumPollRateTicks", 1, 1, Integer.MAX_VALUE);
        MAX_QUEUE_SIZE = builder
                .comment("Maximum size of the outbound machine snapshot queue.")
                .defineInRange("maxQueueSize", 100, 1, Integer.MAX_VALUE);

        QUEUE_SEND_DEFAULT = builder
                .comment("Default number of snapshots to send from the queue per tick.")
                .defineInRange("queueSendDefault", 10, 1, Integer.MAX_VALUE);

        QUEUE_SEND_RATE = builder
                .comment("Rate at which the queue sends snapshots in ticks in miliseconds")
                .defineInRange("queueSendRate", 20000, 2000, Integer.MAX_VALUE);

        QUEUE_SEND_DELAY = builder
                .comment("Delay from server startup before sending snapshots from the queue in ticks in miliseconds")
                .defineInRange("queueSendDelay", 20000, 5000, Integer.MAX_VALUE);

        MAX_RETRIES = builder
                .comment("Maximum number of retries for sending a snapshot from the queue.")
                .defineInRange("maxRetries", 2, 1, Integer.MAX_VALUE);

        SERVER_CONFIG = builder.build();
    }
}
