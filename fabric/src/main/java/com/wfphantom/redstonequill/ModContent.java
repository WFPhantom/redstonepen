/*
 * @file ModContent.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package com.wfphantom.redstonequill;

import com.wfphantom.redstonequill.blocks.RedstoneTrack;
import com.wfphantom.redstonequill.items.RedstoneQuillItem;
import com.wfphantom.redstonequill.libmc.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class ModContent
{
  public static void init()
  {
    initBlocks();
    initItems();
  }

  public static void initBlocks()
  {
    Registries.addBlock("track",
      ()->new RedstoneTrack.RedstoneTrackBlock(BlockBehaviour.Properties.of().noCollission().instabreak().dynamicShape().randomTicks()),
      RedstoneTrack.TrackBlockEntity::new
    );
  }

  public static void initItems()
  {
    Registries.addItem("quill", ()->new RedstoneQuillItem(
      (new Item.Properties()).rarity(Rarity.UNCOMMON).stacksTo(1)
    ));
  }

  public static void initReferences()
  {
    Registries.instantiateAll();
    references.TRACK_BLOCK = (RedstoneTrack.RedstoneTrackBlock)Registries.getBlock("track");
  }

  public static final class references
  {
    public static RedstoneTrack.RedstoneTrackBlock TRACK_BLOCK = null;
  }
}
