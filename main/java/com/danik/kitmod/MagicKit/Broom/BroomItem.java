package com.danik.kitmod.MagicKit.Broom;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BroomItem extends Item {
    public BroomItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, net.minecraft.world.InteractionHand hand) {
        if (!level.isClientSide) {
            BroomEntity entity = new BroomEntity(BroomRegistry.BROOM_ENTITY.get(), level);
            entity.setPos(player.getX(), player.getY(), player.getZ());
            level.addFreshEntity(entity);
            player.startRiding(entity);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
