/*
 * @file Registries.java
 * @author Stefan Wilhelm (wile)
 * @copyright (C) 2020 Stefan Wilhelm
 * @license MIT (see https://opensource.org/licenses/MIT)
 *
 * Common game registry handling.
 */
package com.wfphantom.redstonequill.libmc;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.*;
import java.util.function.Supplier;

import static com.wfphantom.redstonequill.RedstoneQuill.LOGGER;
import static com.wfphantom.redstonequill.RedstoneQuill.MODID;


public class Registries
{

  private static final List<Tuple<String, Supplier<? extends Block>>> block_suppliers = new ArrayList<>();
  private static final List<Tuple<String, Supplier<? extends Item>>> item_suppliers = new ArrayList<>();
  private static final List<Tuple<String, Supplier<? extends BlockEntityType<?>>>> block_entity_type_suppliers = new ArrayList<>();

    private static final Map<String, Block> registered_blocks = new LinkedHashMap<>();
  private static final Map<String, Item> registered_items = new LinkedHashMap<>();
  private static final Map<String, BlockEntityType<?>> registered_block_entity_types = new HashMap<>();


  public static void init()
  {
  }

  public static void instantiateAll()
  {
    registered_blocks.clear();
    block_suppliers.forEach((reg)->{
      registered_blocks.put(reg.getA(), reg.getB().get());
      Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, reg.getA()), registered_blocks.get(reg.getA()));
    });
    registered_items.clear();
    item_suppliers.forEach((reg)->{
      registered_items.put(reg.getA(), reg.getB().get());
      Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, reg.getA()), registered_items.get(reg.getA()));
    });
    registered_block_entity_types.clear();
    block_entity_type_suppliers.forEach((reg)->{
      registered_block_entity_types.put(reg.getA(), reg.getB().get());
      Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, reg.getA()), registered_block_entity_types.get(reg.getA()));
    });
  }

  // -------------------------------------------------------------------------------------------------------------

  public static Block getBlock(String block_name)
  { return registered_blocks.get(block_name); }


  public static BlockEntityType<?> getBlockEntityType(String block_name)
  { return registered_block_entity_types.get(block_name); }


  public static BlockEntityType<?> getBlockEntityTypeOfBlock(String block_name)
  { return getBlockEntityType("tet_"+block_name); }

  public static BlockEntityType<?> getBlockEntityTypeOfBlock(Block block)
  { return getBlockEntityTypeOfBlock(BuiltInRegistries.BLOCK.getKey(block).getPath()); }



  // -------------------------------------------------------------------------------------------------------------


  public static List<Item> getRegisteredItems()
  { return registered_items.values().stream().toList(); }


  // -------------------------------------------------------------------------------------------------------------

  public static <T extends Item> void addItem(String registry_name, Supplier<T> supplier)
  { item_suppliers.add(new Tuple<>(registry_name, supplier)); }

  public static <T extends Block> void addBlock(String registry_name, Supplier<T> block_supplier)
  {
    block_suppliers.add(new Tuple<>(registry_name, block_supplier));
    item_suppliers.add(new Tuple<>(registry_name, ()->new BlockItem(registered_blocks.get(registry_name), new Item.Properties())));
  }

  public static <T extends BlockEntity> void addBlockEntityType(String registry_name, BlockEntityType.BlockEntitySupplier<T> ctor, String... block_names)
  {
    block_entity_type_suppliers.add(new Tuple<>(registry_name, ()->{
      final Block[] blocks = Arrays.stream(block_names).map(s -> {
        Block b = registered_blocks.get(s);
        if (b == null) LOGGER.error("registered_blocks does not encompass '{}'", s);
        return b;
      }).filter(Objects::nonNull).toList().toArray(new Block[]{});
      return BlockEntityType.Builder.of(ctor, blocks).build(null);
    }));
  }

  // -------------------------------------------------------------------------------------------------------------

  public static void addBlock(String registry_name, Supplier<? extends Block> block_supplier, BlockEntityType.BlockEntitySupplier<?> block_entity_ctor)
  {
    addBlock(registry_name, block_supplier);
    addBlockEntityType("tet_"+registry_name, block_entity_ctor, registry_name);
  }
}