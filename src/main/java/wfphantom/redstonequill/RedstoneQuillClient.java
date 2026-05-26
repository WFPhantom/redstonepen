package wfphantom.redstonequill;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import wfphantom.redstonequill.detail.ModRenderers;
import wfphantom.redstonequill.libmc.Registries;

@Mod(value = RedstoneQuill.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = RedstoneQuill.MODID, value = Dist.CLIENT)
public class RedstoneQuillClient {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(Registries.TRACK_BLOCK_ENTITY.get(), renderer -> new ModRenderers.TrackTer());
    }

    @SubscribeEvent
    public static void onRegisterModels(final ModelEvent.RegisterAdditional event) {
        ModRenderers.TrackTer.registerModels().forEach(event::register);
    }
}