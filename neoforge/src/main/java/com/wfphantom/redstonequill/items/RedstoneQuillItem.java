/*
 * @file RedstonePenItem.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package com.wfphantom.redstonequill.items;

import com.wfphantom.redstonequill.blocks.RedstoneTrack;
import com.wfphantom.redstonequill.libmc.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RedstoneQuillItem extends Item {
    public RedstoneQuillItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return (state.getBlock().defaultDestroyTime() < 0.5f) ? 10000f : 0f;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        // Hand needs to be guessed here.
        ItemStack stack = player.getItemInHand(player.getUsedItemHand());
        if (!isQuill(stack)) stack = player.getMainHandItem();
        if (!isQuill(stack)) stack = player.getOffhandItem();
        if (isQuill(stack)) attack(stack, pos, player);
        if (state.is(Registries.TRACK_BLOCK.get())) return false;
        return state.getBlock().defaultDestroyTime() < 0.5f;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Player player = context.getPlayer();
        final InteractionHand hand = context.getHand();
        final BlockPos pos = context.getClickedPos();
        final Direction facing = context.getClickedFace();
        final Level world = context.getLevel();
        final BlockState state = world.getBlockState(pos);
        final ItemStack stack = context.getItemInHand();
        // Add to track
        if (state.getBlock() instanceof RedstoneTrack.RedstoneTrackBlock track) {
            if (world.isClientSide()) return InteractionResult.SUCCESS;
            final BlockHitResult rtr = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());
            return track.modifySegments(state, world, pos, player, stack, hand, rtr, false, true);
        }
        // Check if a new track can be placed.
        if (!RedstoneTrack.RedstoneTrackBlock.canBePlacedOnFace(state, world, pos, facing)) {
            // Cannot place here.
            return InteractionResult.FAIL;
        }
        if (world.isClientSide()) return InteractionResult.SUCCESS;
        // Place new track
        final BlockPos target_pos = pos.relative(facing);
        final BlockState target_state = world.getBlockState(target_pos);
        if (target_state.getBlock() instanceof RedstoneTrack.RedstoneTrackBlock track_block) {
            // Add/remove tracks to existing RedstoneTrackBlock
            final BlockHitResult rtr = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), target_pos, context.isInside());
            return track_block.modifySegments(target_state, world, target_pos, player, stack, hand, rtr, false, true);
        } else {
            final BlockHitResult rtr = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), target_pos, context.isInside());
            final BlockPlaceContext ctx = new BlockPlaceContext(Objects.requireNonNull(player), context.getHand(), new ItemStack(Items.REDSTONE), rtr);
            final BlockState rs_state = Registries.TRACK_BLOCK.get().getStateForPlacement(ctx);
            if (rs_state == null) return InteractionResult.FAIL;
            if (!target_state.canBeReplaced(ctx)) return InteractionResult.FAIL;
            if (!world.setBlock(target_pos, rs_state, 1 | 2 | 16)) return InteractionResult.FAIL;
            final BlockState placed_state = world.getBlockState(target_pos);
            if (placed_state.getBlock() instanceof RedstoneTrack.RedstoneTrackBlock track_block) {
                return (track_block.modifySegments(target_state, world, target_pos, player, stack, hand, rtr, false, true) == InteractionResult.FAIL) ? InteractionResult.FAIL : InteractionResult.CONSUME;
            } else {
                world.removeBlock(target_pos, false);
                return InteractionResult.FAIL;
            }
        }
    }

    private void attack(ItemStack stack, BlockPos pos, Player player) {
        final Level world = player.getCommandSenderWorld();
        final BlockState state = world.getBlockState(pos);
        if (state.is(Registries.TRACK_BLOCK.get())) {
            final HitResult rt = player.pick(10.0, 0f, false);
            if (rt.getType() != HitResult.Type.BLOCK) return;
            final InteractionHand hand = (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == this) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (!(state.getBlock() instanceof RedstoneTrack.RedstoneTrackBlock track)) return;
            track.modifySegments(state, player.getCommandSenderWorld(), pos, player, stack, hand, ((BlockHitResult) rt), true, false);
        } else if (state.is(Blocks.REDSTONE_WIRE)) {
            pushRedstone(stack, 1, player);
            world.removeBlock(pos, false);
        }
    }

    public static void pushRedstone(ItemStack stack, int amount, Player player) {
        if (player.isCreative()) return;
        if (amount > 0) {
            if (stack.getItem() == Items.REDSTONE) {
                if (stack.getCount() <= stack.getMaxStackSize() - amount) stack.grow(amount);
                else give(player, new ItemStack(Items.REDSTONE, amount));
            }
            else give(player, new ItemStack(Items.REDSTONE, amount));
        }
    }

    public static void popRedstone(ItemStack stack, int amount, Player player, InteractionHand hand) {
        if (player.isCreative()) return;
        if (amount <= 0) return;
        if (stack.getItem() == Items.REDSTONE) {
            if (stack.getCount() <= amount) player.setItemInHand(hand, ItemStack.EMPTY);
            else stack.shrink(amount);
        }
        else extract(player, new ItemStack(Items.REDSTONE), amount, false);
    }

    public static boolean hasEnoughRedstone(ItemStack stack, int amount, Player player) {
        if (player.isCreative()) return true;
        if (isQuill(stack)) return extract(player, new ItemStack(Items.REDSTONE), amount, true).getCount() >= amount;
        else if (stack.getItem() == Items.REDSTONE) return (stack.getCount() >= amount);
        else return false;
    }

    public static boolean isQuill(ItemStack stack) {
        return (stack.getItem() instanceof RedstoneQuillItem);
    }

    private static ItemStack extract(Player player, ItemStack match, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;
        final Container inventory = player.getInventory();
        final int size = Mth.clamp(36, 0, inventory.getContainerSize());
        List<ItemStack> matches = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            final ItemStack stack = inventory.getItem(i);
            if ((!stack.isEmpty()) && (ItemStack.isSameItemSameComponents(stack, match))) matches.add(stack);
        }
        matches.sort(Comparator.comparingInt(ItemStack::getCount));
        if (matches.isEmpty()) return ItemStack.EMPTY;
        if (!simulate) {
            int n_left = amount;
            ItemStack fetched_stack = matches.getFirst().split(n_left);
            n_left -= fetched_stack.getCount();
            for (int i = 1; (i < matches.size()) && (n_left > 0); ++i) {
                ItemStack stack = matches.get(i).split(n_left);
                n_left -= stack.getCount();
                fetched_stack.grow(stack.getCount());
            }
            return fetched_stack.isEmpty() ? ItemStack.EMPTY : fetched_stack;
        } else {
            int total = 0;
            for (ItemStack m : matches) total += m.getCount();
            if (total == 0) return ItemStack.EMPTY;
            ItemStack result = match.copy();
            result.setCount(Math.min(total, amount));
            return result;
        }
    }

    private static void give(Player entity, ItemStack stack) {
        entity.getInventory().placeItemBackInInventory(stack);
    }
}