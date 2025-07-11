package com.danik.kitmod.MagicKit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class MagicWandItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "kitmod");
    public static final RegistryObject<Item> MAGIC_WAND =
            ITEMS.register("magic_wand", () -> new MagicWand(new Item.Properties().stacksTo(1)));

    public static class MagicWand extends Item {
        private int spellIndex = 0;
        private final String[] spells = {"fireball", "teleport", "lightning", "levitate", "ice_wall"};
        private final Map<String, TextColor> spellColors = Map.of(
                "fireball", TextColor.fromRgb(0xFF4500),
                "teleport", TextColor.fromRgb(0x7CFC00),
                "lightning", TextColor.fromRgb(0x00FFFF),
                "levitate", TextColor.fromRgb(0x9370DB),
                "ice_wall", TextColor.fromRgb(0xADD8E6)
        );
        private final HashMap<UUID, Long> lastCastTime = new HashMap<>();
        private final long cooldownMillis = 3000;

        public MagicWand(Properties props) {
            super(props);
        }

        private HitResult getTarget(Level world, Player player) {
            Vec3 eye = player.getEyePosition();
            Vec3 look = eye.add(player.getLookAngle().scale(100));

            AABB box = player.getBoundingBox().expandTowards(player.getLookAngle().scale(100)).inflate(1.0);
            EntityHitResult entityHit = getEntityHitResult(world, player, eye, look, box);

            if (entityHit != null) return entityHit;

            return world.clip(new ClipContext(eye, look, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        }

        private EntityHitResult getEntityHitResult(Level world, Player player, Vec3 from, Vec3 to, AABB box) {
            double closestDistance = Double.MAX_VALUE;
            EntityHitResult closestResult = null;

            for (Entity entity : world.getEntities(player, box)) {
                if (entity == player || !(entity instanceof LivingEntity)) continue;
                AABB aabb = entity.getBoundingBox().inflate(0.3);
                Optional<Vec3> optional = aabb.clip(from, to);
                if (optional.isPresent()) {
                    double distance = from.distanceTo(optional.get());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestResult = new EntityHitResult(entity, optional.get());
                    }
                }
            }
            return closestResult;
        }

        private String formatName(String key) {
            return switch (key) {
                case "ice_wall" -> "Ice Wall";
                default -> key.substring(0, 1).toUpperCase() + key.substring(1);
            };
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
            ItemStack stack = player.getItemInHand(hand);
            UUID playerId = player.getUUID();
            long now = System.currentTimeMillis();
            long lastUsed = lastCastTime.getOrDefault(playerId, 0L);

            if (player.isCrouching()) {
                spellIndex = (spellIndex + 1) % spells.length;
                if (!world.isClientSide) {
                    String spell = spells[spellIndex];
                    Component msg = Component.literal("Selected Spell: ")
                            .append(Component.literal(formatName(spell))
                                    .withStyle(Style.EMPTY.withColor(spellColors.getOrDefault(spell, TextColor.fromRgb(0xFFFFFF)))));
                    player.displayClientMessage(msg, true);
                    world.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1f, 1f);
                }
            } else {
                if (!world.isClientSide) {
                    if (now - lastUsed < cooldownMillis) {
                        long remaining = (cooldownMillis - (now - lastUsed)) / 1000;
                        player.sendSystemMessage(Component.literal("Cooldown: " + remaining + "s"));
                        return InteractionResultHolder.fail(stack);
                    }

                    lastCastTime.put(playerId, now);
                    player.getCooldowns().addCooldown(this, (int) (cooldownMillis / 50));

                    HitResult hit = getTarget(world, player);
                    Vec3 target = hit.getLocation();
                    BlockPos targetPos = BlockPos.containing(target);
                    ServerLevel server = (ServerLevel) world;

                    String spell = spells[spellIndex];
                    switch (spell) {
                        case "fireball" -> {
                            world.explode(null, target.x, target.y, target.z, 2.5f, Level.ExplosionInteraction.MOB);
                            server.playSound(null, targetPos, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1f, 1f);
                        }
                        case "teleport" -> {
                            server.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY(), player.getZ(), 50, 0.5, 1, 0.5, 0);
                            player.teleportTo(target.x, target.y, target.z);
                            server.sendParticles(ParticleTypes.PORTAL, target.x, target.y, target.z, 50, 0.5, 1, 0.5, 0);
                            server.playSound(null, targetPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
                        }
                        case "lightning" -> {
                            Entity lightning = EntityType.LIGHTNING_BOLT.create(server);
                            if (lightning != null) {
                                lightning.moveTo(target);
                                server.addFreshEntity(lightning);
                                server.playSound(null, targetPos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1f, 1f);
                            }
                            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.x, target.y + 1, target.z, 30, 0.5, 1, 0.5, 0);
                        }
                        case "levitate" -> {
                            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity living) {
                                living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 1));
                                server.sendParticles(ParticleTypes.END_ROD, living.getX(), living.getY(), living.getZ(), 40, 0.5, 1, 0.5, 0);
                            } else {
                                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 1));
                                server.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY(), player.getZ(), 40, 0.5, 1, 0.5, 0);
                            }
                            server.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.PLAYERS, 1f, 1.5f);
                        }
                        case "ice_wall" -> {
                            Vec3 forward = player.getLookAngle().normalize();
                            Vec3 center = player.getEyePosition().add(forward.scale(3));
                            BlockPos base = BlockPos.containing(center).below();

                            for (int y = 0; y < 3; y++) {
                                for (int dx = -1; dx <= 1; dx++) {
                                    BlockPos pos = base.offset(dx, y, 0);
                                    if (server.isEmptyBlock(pos)) {
                                        server.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3);
                                    }
                                }
                            }

                            server.sendParticles(ParticleTypes.SNOWFLAKE, center.x, center.y, center.z, 30, 1, 1, 1, 0.01);
                            server.playSound(null, base, SoundEvents.GLASS_PLACE, SoundSource.PLAYERS, 1f, 1.5f);
                        }
                    }
                }
            }

            return InteractionResultHolder.success(stack);
        }
    }
}
