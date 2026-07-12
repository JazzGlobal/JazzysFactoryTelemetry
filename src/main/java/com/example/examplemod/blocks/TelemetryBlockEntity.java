package com.example.examplemod.blocks;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TelemetryBlockEntity extends BlockEntity {
    public TelemetryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public String UUID;
    public String Name;
    public List<MetaMachineBlockEntity> ConnectedMachines;
}
