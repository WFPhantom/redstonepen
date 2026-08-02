package com.wfphantom.redstonequill.libmc;

import com.wfphantom.redstonequill.RedstoneQuill;
import com.wfphantom.redstonequill.blocks.RedstoneTrack;
import com.wfphantom.redstonequill.items.RedstoneQuillItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


import java.util.Set;

public class Registries {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RedstoneQuill.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, RedstoneQuill.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RedstoneQuill.MODID);

    public static final DeferredBlock<RedstoneTrack.RedstoneTrackBlock> TRACK_BLOCK = BLOCKS.register("track", () -> new RedstoneTrack.RedstoneTrackBlock(BlockBehaviour.Properties.of().noCollission().instabreak().dynamicShape().randomTicks()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneTrack.TrackBlockEntity>> TRACK_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("track", () -> new BlockEntityType<>(RedstoneTrack.TrackBlockEntity::new, Set.of(TRACK_BLOCK.get()), null));
    public static final DeferredItem<RedstoneQuillItem> QUILL_ITEM = ITEMS.register("quill", () -> new RedstoneQuillItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
    }
}