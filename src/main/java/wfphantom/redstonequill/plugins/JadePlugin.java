package wfphantom.redstonequill.plugins;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import wfphantom.redstonequill.libmc.Registries;

public class JadePlugin {
    @WailaPlugin
    public static class TrackPlugin implements IWailaPlugin {
        @Override
        public void registerClient(IWailaClientRegistration registration) {
            registration.registerBlockComponent(JadeComponentProvider.INSTANCE, Registries.TRACK_BLOCK.get().getClass());
        }
    }
}