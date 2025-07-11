
Set-Content -Encoding UTF8 README.md @"
# Magic Kit Mod – Minecraft Forge Mod (1.20.1+)

📹 Demo video: [https://youtu.be/your_demo_video_link_here](https://youtu.be/p1IF7ifN24A)

A functional magic-themed mod for Minecraft Forge 1.20.1+, written entirely in Java. This mod includes a fully working Magic Wand with multiple spells and a Wizard Armor Set using vanilla Minecraft textures.

Designed to demonstrate scripting ability, item behavior, NBT state management, and interaction logic — this mod serves as a clean, readable showcase of core Forge development.

---

## Core Features

- Magic Wand:
  - Supports 5 different spells
  - Switch spells with right-click or trigger
  - In-game feedback and cooldowns
  - Multiplayer-safe logic

- Wizard Armor Set:
  - Helmet, Chestplate, Leggings, Boots
  - Uses standard Minecraft armor models/textures
  - Custom durability and armor values
  - Fully wearable and enchantable

---

## Spell Types

The wand supports 5 spells:
1. Fireball
2. Teleport
3. Lightning
4. Ice Wall (summons blocks)
5. Levitation Beam

Switching is handled through in-game logic and saved using NBT tags.

---

## Code Highlights

### MagicWandItem.java

```java
@Override
public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    if (!level.isClientSide) {
        MagicWandLogic.castSelectedSpell(player, level);
        player.getCooldowns().addCooldown(this, 20);
    }
    return InteractionResultHolder.success(player.getItemInHand(hand));
}
```

---

### Spell Switching with NBT

```java
public static void switchSpell(Player player) {
    CompoundTag tag = player.getMainHandItem().getOrCreateTag();
    int current = tag.getInt("SelectedSpell");
    tag.putInt("SelectedSpell", (current + 1) % 5);
}
```

---

### WizardArmorItem.java

```java
@Override
public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    return "minecraft:textures/models/armor/iron_layer_1.png";
}
```

---

## Project Structure

```
src/main/java/com/example/magickit/
├─ item/
│  ├─ MagicWandItem.java
│  └─ WizardArmorItem.java
├─ KitsMod.java
├─ ClientMod.java
```

---


## Author

Developed by @danifeyrus as part of a Forge modding portfolio.

This mod demonstrates:
- Item logic with NBT spell management
- Use of Forge lifecycle and registration
- Multiplayer-safe effects
- Clean Java architecture using only standard assets
"@
