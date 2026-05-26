package wfphantom.redstonequill;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import wfphantom.redstonequill.libmc.Networking;
import wfphantom.redstonequill.libmc.Registries;

@Mod(RedstoneQuill.MODID)
public class RedstoneQuill {
    // TODO: Make work with shaders (emissive texture)
    // TODO: Make redstone unplacable with config
    // TODO: Check why redstone is not updating properly
    // TODO: Color code lines
    // TODO: Leftover code cleanup
    // TODO: Recipe really doesn't need to be all that
    // TODO: Make more translatable
    public static final String MODID = "redstonequill";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RedstoneQuill(IEventBus bus) {
        Registries.register(bus);
        bus.addListener(RedstoneQuill::onRegisterNetwork);
        bus.addListener(RedstoneQuill::onBuildCreativeTabContents);
    }

    public static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) event.accept(Registries.QUILL_ITEM.get());
    }

    private static void onRegisterNetwork(final RegisterPayloadHandlersEvent event) {
        Networking.init(event.registrar("v1"));
    }
}