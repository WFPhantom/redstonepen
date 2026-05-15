/*
 * @file ModContent.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package wfphantom.redstonequill;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import wfphantom.redstonequill.blocks.RedstoneTrack;
import wfphantom.redstonequill.items.RedstoneQuillItem;
import wfphantom.redstonequill.libmc.Registries;

public class ModContent {
    public static void init() {
        Registries.addBlock("track", () -> new RedstoneTrack.RedstoneTrackBlock(BlockBehaviour.Properties.of().noCollission().instabreak().dynamicShape().randomTicks()), RedstoneTrack.TrackBlockEntity::new);
        Registries.addItem("quill", () -> new RedstoneQuillItem(new Item.Properties().stacksTo(1)));
    }

    public static void initReferences() {
        references.TRACK_BLOCK = (RedstoneTrack.RedstoneTrackBlock) Registries.getBlock("track");
    }

    public static final class references {
        public static RedstoneTrack.RedstoneTrackBlock TRACK_BLOCK = null;
    }
}