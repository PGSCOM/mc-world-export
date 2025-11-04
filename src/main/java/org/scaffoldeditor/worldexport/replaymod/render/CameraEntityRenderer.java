package org.scaffoldeditor.worldexport.replaymod.render;

import org.scaffoldeditor.worldexport.replaymod.AnimatedCameraEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CameraEntityRenderer extends EntityRenderer<AnimatedCameraEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("replaymod", "camera_head.png");
    // private final RenderLayer RENDER_LAYER = RenderLayer.getEntitySolid(TEXTURE);

    // private final ModelPart model;
    
    public CameraEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        // model = ctx.bakeLayer(ReplayExportMod.CAMERA_MODEL_LAYER);
    }

    @Override
    public void render(AnimatedCameraEntity entity, float yaw, float tickDelta, PoseStack matrices,
            MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        // VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RENDER_LAYER);
        // matrices.pushPose();
        
        // matrices.mulPose(Axis.YN.rotationDegrees(180 + entity.getYRot(tickDelta)));
        // matrices.mulPose(Axis.XN.rotationDegrees(entity.getXRot(tickDelta)));
        // matrices.mulPose(Axis.ZP.rotationDegrees(entity.getRoll()));
        
        // int rgb = entity.getColor();
        // float r = ((rgb >> 16) & 0xFF) / 256f;
        // float g = ((rgb >> 8) & 0xFF) / 256f;
        // float b = (rgb & 0xFF) / 256f;

        // model.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, r, g, b, 1);

        // matrices.popPose();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("root",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0, 0, 0));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ResourceLocation getTextureLocation(AnimatedCameraEntity var1) {
        return TEXTURE;
    }
    
}
