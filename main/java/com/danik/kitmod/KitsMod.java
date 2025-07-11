package com.danik.kitmod;

import com.danik.kitmod.MagicKit.MagicWandItem;
import com.danik.kitmod.MagicKit.WizardArmorItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// This MUST match your mods.toml “modid”
@Mod(KitsMod.MODID)
public class KitsMod {
    public static final String MODID = "kitmod";

    public KitsMod() {
        MagicWandItem.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        WizardArmorItem.ModRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
