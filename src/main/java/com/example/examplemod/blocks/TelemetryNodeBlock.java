package com.example.examplemod.blocks;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.http.TelemetryApiClient;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TelemetryNodeBlock extends BaseEntityBlock {
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TelemetryBlockEntity(ExampleMod.TELEMETRY_NODE_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    // TODO: These records probably shouldn't be in this class.
    public record RelativeBlock(BlockEntity blockEntity, Direction direction) {}
    public record MachineSnapshot(
            String machineId,
            String machineType,
            boolean poweredOn,
            Instant observedAt
    ) {}

    public TelemetryNodeBlock(BlockBehaviour.Properties properties) {
        super(properties);
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
        TelemetryBlockEntity telemetryBlockEntity = (TelemetryBlockEntity)level.getBlockEntity(pos);

        var msg = player.getDisplayName().getString() + " Interacted with " + telemetryBlockEntity.Name + "!";
        System.out.println(msg);
        player.sendSystemMessage(Component.literal(msg));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // Schedule our very first tick to initiate adjacent block polling.
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        // Generate UUID for ID.
        UUID uuid = UUID.randomUUID();
        TelemetryBlockEntity telemetryBlockEntity  = (TelemetryBlockEntity)level.getBlockEntity(pos);
        if (telemetryBlockEntity instanceof TelemetryBlockEntity){
            telemetryBlockEntity.UUID = uuid.toString();
            telemetryBlockEntity.Name = uuid.toString() + ": Telemetry Block";
        }

        if (!level.isClientSide)
        {
            // TODO: delay should be configurable and should match the tick override's delay.
            level.scheduleTick(pos, this, 200);
        }
    }

    // On tick, perform a scan for adjacent blocks, then schedule another tick.
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        List<RelativeBlock> adjacentBlocks = GetAdjacentBlocks(this, pos, level);
        List<MachineSnapshot> snapshots = new ArrayList<>();
        for (RelativeBlock block : adjacentBlocks)
        {
            System.out.println("Adjacent Block for " + block.direction.name() + ": " + block.blockEntity.getBlockState().getBlock().getName());
            if (block.blockEntity instanceof MetaMachineBlockEntity machineTile) {
                MetaMachine machine = machineTile.getMetaMachine();
                MachineSnapshot snapshot = new MachineSnapshot(
                        machine.getDefinition().getId().toString(),
                        machine.getDefinition().getName(),
                        isMachineActive(machine),
                        Instant.now()
                );
                System.out.println(
                        "Machine ID: " + snapshot.machineId + "\n" +
                        "Machine Name: " + snapshot.machineType + "\n" +
                        "Is Active: " + snapshot.poweredOn + "\n" +
                        "Timestamp: " + snapshot.observedAt.toString() + "\n"
                );
                snapshots.add(snapshot);
            }
        }

        TelemetryApiClient client = new TelemetryApiClient();
        client.sendSnapshot(snapshots);

        // TODO: delay should be configurable and should match the onPlace override's first tick
        level.scheduleTick(pos, this, 200);
    }

    private List<RelativeBlock> GetAdjacentBlocks(TelemetryNodeBlock block, BlockPos pos, ServerLevel level)
    {
        List<RelativeBlock> relativeBlocks = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            BlockPos adjacentPos = pos.relative(direction);
            BlockEntity blockEntity = level.getBlockEntity(adjacentPos);
            if (blockEntity != null)
                relativeBlocks.add(new RelativeBlock(blockEntity, direction));
        }

        return relativeBlocks;
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
}
