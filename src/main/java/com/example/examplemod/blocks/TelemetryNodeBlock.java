package com.example.examplemod.blocks;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.config.TelemetryServerConfig;
import com.example.examplemod.entities.TelemetryBlockEntity;
import com.example.examplemod.http.TelemetryApiClient;
import com.example.examplemod.models.MachineSnapshot;
import com.example.examplemod.models.MinimizedBlockPos;
import com.example.examplemod.models.TelemetryNode;
import com.example.examplemod.telemetry.RetryableOutboundItem;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TelemetryNodeBlock extends BaseEntityBlock {
    public TelemetryNodeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ExampleMod.TELEMETRY_BLOCK_ENTITY_BLOCK_ENTITY_ENTRY.create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    )
    {
        var msg = player.getDisplayName().getString() + " Interacted with " + state.getBlock().getName().getString() + "!";
        System.out.println(msg);
        player.sendSystemMessage(Component.literal(msg));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // Schedule our very first tick to initiate adjacent block polling.
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        ScheduleFirstTick(pos, level, state);
    }

    // On tick, perform a scan for adjacent blocks, then schedule another tick.
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        System.out.println("Telemetry node tick at position: " + pos);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TelemetryBlockEntity telemetryNodeBlockEntity)) {
            ExampleMod.LOGGER.warn("Telemetry node at {} is missing its block entity", pos);
            level.scheduleTick(pos, this, 200);
            return;
        }

        // Get list of Block Entities from the Telemetry Node Entity's Stored Linked Machines.
        List<RetryableOutboundItem<MachineSnapshot>> snapshots = new ArrayList<>();
        List<MinimizedBlockPos> linkedMachineCoordinates = telemetryNodeBlockEntity.getLinkedMachinesMinimized();
        for (MinimizedBlockPos minimizedPos : linkedMachineCoordinates) 
        {
            BlockPos machinePos = new BlockPos(new Vec3i(minimizedPos.x, minimizedPos.y, minimizedPos.z));
            BlockEntity machineBlockEntity = level.getBlockEntity(machinePos);
            
            if (machineBlockEntity == null || !(machineBlockEntity instanceof MetaMachineBlockEntity)) {
                telemetryNodeBlockEntity.removeLinkedMachine(machinePos.asLong());
                continue;
            }

            MetaMachineBlockEntity machineTile = (MetaMachineBlockEntity) machineBlockEntity;
            MetaMachine machine = machineTile.getMetaMachine();
            TelemetryNode telemetryNode = new TelemetryNode(
                    telemetryNodeBlockEntity.getNodeId().toString(),
                    telemetryNodeBlockEntity.getLinkedMachinesMinimized()
            );
            MachineSnapshot snapshot = new MachineSnapshot(
                    telemetryNode,
                    machine.getDefinition().getId().toString(),
                    machine.getDefinition().getName(),
                    isMachineActive(machine),
                    Instant.now()
            );
            System.out.println(
                    "Telemetry Node ID: " + snapshot.TelemetryNode.TelemetryNodeId + "\n" +
                    "Machine ID: " + snapshot.MachineId + "\n" +
                    "Machine Name: " + snapshot.MachineType + "\n" +
                    "Is Active: " + snapshot.PoweredOn + "\n" +
                    "Timestamp: " + snapshot.ObservedAt.toString() + "\n"
            );

            RetryableOutboundItem<MachineSnapshot> retryableSnapshot = new RetryableOutboundItem<MachineSnapshot>(
                snapshot,
                TelemetryServerConfig.MAX_RETRIES.get()
            );
            
            snapshots.add(retryableSnapshot);
        }

        if(!ExampleMod.OUTBOUND_MACHINE_SNAPSHOT_QUEUE.enqueueSnapshots(snapshots))
        {
            System.out.println("Failed to enqueue all snapshots to the outbound queue because the queue is");
        };

        level.scheduleTick(pos, this, ((TelemetryBlockEntity) blockEntity).getPollRateTicks());
    }

    public boolean isMachineActive(MetaMachine machine)
    {
        for (var trait : machine.getTraits()) {
            if (trait instanceof RecipeLogic logic) {
                return logic.isActive();
            }
        }
        return false;
    }

    private void ScheduleFirstTick(BlockPos pos, Level level, BlockState state) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TelemetryBlockEntity telemetryNodeBlockEntity) {
                int pollRateTicks = telemetryNodeBlockEntity.getPollRateTicks();
                level.scheduleTick(pos, this, pollRateTicks);
            }
        }
    }
}
