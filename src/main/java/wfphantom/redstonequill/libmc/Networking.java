/*
 * @file Networking.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Main client/server message handling.
 */
package wfphantom.redstonequill.libmc;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import wfphantom.redstonequill.RedstoneQuill;

public class Networking {
    public static void init(PayloadRegistrar registrar) {
        registrar.playToClient(UnifiedPayload.TYPE, UnifiedPayload.STREAM_CODEC, (unified_payload, context) -> {
            final LocalPlayer player = (LocalPlayer) context.player();
            final Level world = player.level();
            final CompoundTag payload = unified_payload.data().nbt();
            context.enqueueWork(() -> {
                final BlockPos pos = BlockPos.of(payload.getLong("pos"));
                final CompoundTag nbt = payload.getCompound("nbt");
                final BlockEntity te = world.getBlockEntity(pos);
                if (!(te instanceof IPacketTileNotifyReceiver nte)) return;
                nte.onServerPacketReceived(nbt);
            });
        });
    }

    //--------------------------------------------------------------------------------------------------------------------
    // Unified Packet Handling
    //--------------------------------------------------------------------------------------------------------------------
    public record UnifiedPayload(UnifiedData data) implements CustomPacketPayload {
        public static final StreamCodec<FriendlyByteBuf, UnifiedPayload> STREAM_CODEC = CustomPacketPayload.codec(UnifiedPayload::write, UnifiedPayload::new);
        public static final CustomPacketPayload.Type<UnifiedPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RedstoneQuill.MODID, "unpnbt"));

        private UnifiedPayload(FriendlyByteBuf buf) {
            this(new UnifiedData(buf.readNbt()));
        }

        private void write(FriendlyByteBuf buf) {
            data.write(buf);
        }

        public CustomPacketPayload.Type<UnifiedPayload> type() {
            return TYPE;
        }

        public record UnifiedData(CompoundTag nbt) {
            public void write(FriendlyByteBuf buf) {
                buf.writeNbt(nbt);
            }

            @Override
            public String toString() {
                return nbt.toString();
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------------------
    // Tile entity notifications
    // --------------------------------------------------------------------------------------------------------------------
    public interface IPacketTileNotifyReceiver {
        default void onServerPacketReceived(CompoundTag nbt) {
        }

    }

    public static class PacketTileNotifyServerToClient {
        public static void sendToPlayers(BlockEntity te, CompoundTag nbt) {
            if ((te == null) || (!(te.getLevel() instanceof ServerLevel sworld))) return;
            final CompoundTag payload = new CompoundTag();
            payload.putLong("pos", te.getBlockPos().asLong());
            payload.put("nbt", nbt);
            final var unified = new UnifiedPayload(new UnifiedPayload.UnifiedData(payload));
            for (ServerPlayer player : sworld.players()) PacketDistributor.sendToPlayer(player, unified);
        }
    }
}
