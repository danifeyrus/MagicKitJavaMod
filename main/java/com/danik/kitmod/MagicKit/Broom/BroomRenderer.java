package com.danik.kitmod.MagicKit.Broom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class BroomRenderer extends EntityRenderer<BroomEntity> {

    private final EntityModel<BroomEntity> model;
    private static final ResourceLocation TEXTURE = new ResourceLocation("kitmod", "textures/entity/broom.png");

    public BroomRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BroomModel<>(context.bakeLayer(BroomModel.LAYER_LOCATION));
    }

    @Override
    public void render(BroomEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        this.model.setupAnim(entity, 0.0F, 0.0F, partialTicks, 0.0F, 0.0F);
        this.model.renderToBuffer(poseStack, buffer.getBuffer(this.model.renderType(TEXTURE)),
                packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BroomEntity entity) {
        return TEXTURE;
    }
}
