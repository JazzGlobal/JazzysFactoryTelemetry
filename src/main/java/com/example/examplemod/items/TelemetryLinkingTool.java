package com.example.examplemod.items;

import com.example.examplemod.entities.TelemetryBlockEntity;
import com.example.examplemod.util.Notification;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.WorkableTieredMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TelemetryLinkingTool extends Item {
    private static final String SELECTED_NODE_POS_TAG = "SelectedNodePos";

    public TelemetryLinkingTool(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Handle right-click on a block
        ItemStack stack = context.getItemInHand();
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        
        // Short-circuit client-side and only perform NBT and linking logic on the server side.
        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // If the selected block is a TelemetryBlockEntity, store its position in the tool's NBT.
        if (blockEntity instanceof TelemetryBlockEntity telemetryBlockEntity) {
            CompoundTag tag = stack.getTagElement(SELECTED_NODE_POS_TAG);
            if (tag == null) {
                tag = new CompoundTag();
                stack.addTagElement(SELECTED_NODE_POS_TAG, tag);
            }
            tag.putLong("SelectedNodePos", telemetryBlockEntity.getBlockPos().asLong());
            return InteractionResult.SUCCESS;
        }
        // If the selected block is a MetaMachineBlockEntity, handle linking with the selected Telemetry node.
        else if (blockEntity instanceof MetaMachineBlockEntity metaMachineBlockEntity) {

            if (!SupportsRecipeTelemetry(metaMachineBlockEntity))
            {
                Notification.sendMessageToPlayer(context, "This machine does not support telemetry");
                return InteractionResult.FAIL;
            }

            CompoundTag tag = stack.getTagElement(SELECTED_NODE_POS_TAG);
            // No selected node / selected node is somehow default value, show message and return fail
            if (tag == null || !tag.contains("SelectedNodePos", CompoundTag.TAG_LONG)) {
                Notification.sendMessageToPlayer(context, "Select a Telemetry Node first");
                return InteractionResult.FAIL;
            }

            BlockPos selectedNodePos = BlockPos.of(tag.getLong("SelectedNodePos"));
            BlockEntity selectedNode = context.getLevel().getBlockEntity(selectedNodePos);
            if (selectedNode == null || !(selectedNode instanceof TelemetryBlockEntity))
            {
                // Selected node no longer exists, clear tool selection and show message
                stack.removeTagKey(SELECTED_NODE_POS_TAG);
                Notification.sendMessageToPlayer(context, "Selected Telemetry Node no longer exists, select another!");
                return InteractionResult.FAIL;
            }
            else
            {
                // Selected node is a valid TelemetryBlockEntity
                TelemetryBlockEntity telemetryBlockEntity = (TelemetryBlockEntity) selectedNode;
                telemetryBlockEntity.addLinkedMachine(metaMachineBlockEntity.getBlockPos().asLong(), context);
                return InteractionResult.SUCCESS;
            }
        }
        else {
            Notification.sendMessageToPlayer(context, "This tool can only be used on Telemetry Nodes or supported machines");
            return InteractionResult.FAIL;
        }
    }

    public static boolean SupportsRecipeTelemetry(MetaMachineBlockEntity blockEntity) {
        MetaMachine machine = blockEntity.getMetaMachine();
        return machine instanceof WorkableTieredMachine;
    }
}
