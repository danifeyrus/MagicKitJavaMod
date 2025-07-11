package com.danik.kitmod.MagicKit.Broom;

import com.danik.kitmod.KitsMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BroomRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KitsMod.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, KitsMod.MODID);

    public static final RegistryObject<Item> BROOM_ITEM = ITEMS.register("broom", () ->
            new BroomItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<EntityType<BroomEntity>> BROOM_ENTITY = ENTITY_TYPES.register("broom", () ->
            EntityType.Builder.<BroomEntity>of(BroomEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F).build("broom"));
}
