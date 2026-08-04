/*
 * @file Networking.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Main client/server message handling.
 */
package com.wfphantom.redstonequill.libmc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


@Environment(EnvType.CLIENT)
public class NetworkingClient
{
  public static void clientInit()
  {
    ClientPlayNetworking.registerGlobalReceiver(Networking.UnifiedPayload.getTYPE(), (unifed_payload, context)->{
      final LocalPlayer player = context.player();
      final Level world = player.level();
      final CompoundTag payload = unifed_payload.data().nbt();
      context.client().execute(()->{
        switch(unifed_payload.data().id()) {
          case Networking.PacketTileNotifyServerToClient.PACKET_ID -> {
            final BlockPos pos = BlockPos.of(payload.getLong("pos"));
            final CompoundTag nbt = payload.getCompound("nbt");
            final BlockEntity te = world.getBlockEntity(pos);
            if(!(te instanceof Networking.IPacketTileNotifyReceiver nte)) return;
            nte.onServerPacketReceived(nbt);
          }
          case Networking.PacketNbtNotifyServerToClient.PACKET_ID -> {
            final String hnd = payload.getString("hnd");
            final CompoundTag nbt = payload.getCompound("nbt");
            if(hnd.isEmpty() || (!Networking.PacketNbtNotifyServerToClient.handlers.containsKey(hnd))) return;
            context.client().execute(()->Networking.PacketNbtNotifyServerToClient.handlers.get(hnd).accept(nbt));
          }
        }
      });
    });
  }
}