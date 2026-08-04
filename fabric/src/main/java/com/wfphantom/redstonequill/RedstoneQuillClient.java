/*
 * @file ModRedstonePenClient.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package com.wfphantom.redstonequill;

import com.wfphantom.redstonequill.blocks.RedstoneTrack;
import com.wfphantom.redstonequill.detail.ModRenderers;
import com.wfphantom.redstonequill.libmc.NetworkingClient;
import com.wfphantom.redstonequill.libmc.Registries;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;


@Environment(EnvType.CLIENT)
public class RedstoneQuillClient implements ClientModInitializer {
    public RedstoneQuillClient() {
        ModelLoadingPlugin.register(pluginContext -> ModRenderers.TrackTer.registerModels().forEach(pluginContext::addModels));
    }

    @Override
    public void onInitializeClient() {
        NetworkingClient.clientInit();
        registerBlockEntityRenderers();

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, ignored) -> true);
    }


    private static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register((BlockEntityType<RedstoneTrack.TrackBlockEntity>) Registries.getBlockEntityTypeOfBlock("track"), ModRenderers.TrackTer::new);
    }
}
