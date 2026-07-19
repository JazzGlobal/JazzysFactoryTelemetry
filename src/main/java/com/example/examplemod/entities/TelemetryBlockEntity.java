package com.example.examplemod.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import com.example.examplemod.config.TelemetryServerConfig;

import java.util.UUID;

public class TelemetryBlockEntity extends BlockEntity {
    public static final String NODE_ID_TAG = "NodeId";
    public static final String POLL_RATE_TAG = "PollRate";

    /**
     * Unique ID for the Telemetry Node
     */
    private UUID nodeId;
    /**
     * Rate at which the Telemetry Node polls for Machine Snapshot data.
     */
    private int pollRateTicks;

    public TelemetryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        this.nodeId = UUID.randomUUID();

        this.pollRateTicks = TelemetryServerConfig.DEFAULT_POLL_RATE_TICKS.get();
        // Ensure the poll rate is not below the minimum allowed by the server configuration
        this.pollRateTicks = Math.max(this.pollRateTicks, TelemetryServerConfig.MINIMUM_POLL_RATE_TICKS.get());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if (tag.hasUUID(NODE_ID_TAG)) {
            this.nodeId = tag.getUUID(NODE_ID_TAG);
        }

        if (tag.contains(POLL_RATE_TAG, CompoundTag.TAG_INT)) {
            this.pollRateTicks = tag.getInt(POLL_RATE_TAG);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putUUID(NODE_ID_TAG, this.nodeId);
        tag.putInt(POLL_RATE_TAG, this.pollRateTicks);
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public int getPollRateTicks() {
        return pollRateTicks;
    }
}
