/*
 * @file ModRedstonePen.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 */
package com.wfphantom.redstonequill;

import com.mojang.logging.LogUtils;
import com.wfphantom.redstonequill.libmc.Networking;
import com.wfphantom.redstonequill.libmc.Registries;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;


public class RedstoneQuill implements ModInitializer {
    public static final String MODID = "redstonequill";
    public static final Logger LOGGER = LogUtils.getLogger();

    public void onInitialize() {
        Registries.init();
        Networking.init();
        ModContent.init();
        ModContent.initReferences();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> Registries.getRegisteredItems().forEach(it -> {
            if (!(it instanceof BlockItem bit) || (bit.getBlock() != ModContent.references.TRACK_BLOCK)) entries.accept(it);
        }));
    }
}
