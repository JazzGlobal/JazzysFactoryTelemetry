package com.example.examplemod.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import com.example.examplemod.blocks.TelemetryNodeBlock.MinimizedBlockPos;
import com.example.examplemod.config.TelemetryServerConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TelemetryBlockEntity extends BlockEntity {
    public static final String NODE_ID_TAG = "NodeId";
    public static final String POLL_RATE_TAG = "PollRate";
    public static final String LINKED_MACHINES_TAG = "LinkedMachines";
    /**
     * Unique ID for the Telemetry Node
     */
    private UUID nodeId;
    /**
     * Rate at which the Telemetry Node polls for Machine Snapshot data.
     */
    private int pollRateTicks;
    
    /**
     * List of linked machines for this Telemetry Node.
     */
    private Set<BlockPos> linkedMachines = new HashSet<>();

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

        if (tag.contains(LINKED_MACHINES_TAG, CompoundTag.TAG_COMPOUND)) {
            CompoundTag linkedMachinesTag = tag.getCompound(LINKED_MACHINES_TAG);
            linkedMachines.clear();
            for (String key : linkedMachinesTag.getAllKeys()) {
                linkedMachines.add(BlockPos.of(linkedMachinesTag.getLong(key)));
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putUUID(NODE_ID_TAG, this.nodeId);
        tag.putInt(POLL_RATE_TAG, this.pollRateTicks);

        // Save the list of linked machines
        CompoundTag linkedMachinesTag = new CompoundTag();
        List<BlockPos> linkedMachinesAsList = new ArrayList<>(linkedMachines);
        for (int i = 0; i < linkedMachinesAsList.size(); i++) {
            
            linkedMachinesTag.putLong("Machine" + i, linkedMachinesAsList.get(i).asLong());
        }
        tag.put(LINKED_MACHINES_TAG, linkedMachinesTag);
    }

    public List<MinimizedBlockPos> getLinkedMachinesMinimized() {
        List<MinimizedBlockPos> minimized = new ArrayList<>();
        for (BlockPos pos : linkedMachines) {
            minimized.add(new MinimizedBlockPos(pos.getX(), pos.getY(), pos.getZ()));
        }
        return minimized;
    }

    public void addLinkedMachine(long machinePos, UseOnContext context) {

        if(!this.linkedMachines.add(BlockPos.of(machinePos))) {
            // Handle the case where the machine was already linked, if necessary
            Player player = context.getPlayer();
            if (player != null) {
                player.displayClientMessage(Component.literal("Machine is already linked."), true);
            }
        }
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public int getPollRateTicks() {
        return pollRateTicks;
    }

    public List<BlockPos> getLinkedMachines() {
        return new ArrayList<>(linkedMachines);
    }
}
