package com.danik.kitmod.MagicKit;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class WizardArmorItem extends ArmorItem {

    public WizardArmorItem(ArmorItem.Type type, Item.Properties props) {
        super(ArmorMaterials.LEATHER, type, props);
    }

    public static class ModRegistry {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "kitmod");

        public static final RegistryObject<Item> WIZARD_HELMET = ITEMS.register("wizard_helmet",
                () -> new WizardArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
        public static final RegistryObject<Item> WIZARD_CHESTPLATE = ITEMS.register("wizard_chestplate",
                () -> new WizardArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
        public static final RegistryObject<Item> WIZARD_LEGGINGS = ITEMS.register("wizard_leggings",
                () -> new WizardArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
        public static final RegistryObject<Item> WIZARD_BOOTS = ITEMS.register("wizard_boots",
                () -> new WizardArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));
    }

    public static boolean isWearingFullWizardSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModRegistry.WIZARD_HELMET.get()
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModRegistry.WIZARD_CHESTPLATE.get()
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModRegistry.WIZARD_LEGGINGS.get()
                && player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModRegistry.WIZARD_BOOTS.get();
    }

    private static final Set<BlockPos> tempIce = new HashSet<>();

    @Mod.EventBusSubscriber(modid = "kitmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ArmorTickHandler {

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Player player = event.player;
            if (!(player.level() instanceof ServerLevel server)) return;
            if (!isWearingFullWizardSet(player)) return;

            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 220, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 220, 0, true, false));
            if (player.fallDistance > 0) player.fallDistance = 0;

        }
    }
}
