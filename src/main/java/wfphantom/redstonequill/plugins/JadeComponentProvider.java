package wfphantom.redstonequill.plugins;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import wfphantom.redstonequill.RedstoneQuill;
import wfphantom.redstonequill.blocks.RedstoneTrack;

public enum JadeComponentProvider implements IBlockComponentProvider {
    INSTANCE;
    private static final ResourceLocation TRACK_POWER_ID = ResourceLocation.fromNamespaceAndPath(RedstoneQuill.MODID, "track_power");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof RedstoneTrack.TrackBlockEntity te)) return;
        Direction side = accessor.getSide() != null ? accessor.getSide().getOpposite() : Direction.UP;
        int power = te.getSidePower(side);
        tooltip.add(Component.translatable("redstonequill.track_power", power));
    }

    @Override
    public ResourceLocation getUid() {
        return TRACK_POWER_ID;
    }
}