package org.scaffoldeditor.worldexport.replaymod.render;

import java.util.Map;
import java.util.Optional;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.scaffoldeditor.worldexport.replaymod.AnimatedCameraEntity;
import org.scaffoldeditor.worldexport.replaymod.camera_animations.AbstractCameraAnimation;
import org.scaffoldeditor.worldexport.replaymod.camera_animations.CameraAnimationModule;
import org.scaffoldeditor.worldexport.replaymod.camera_animations.CameraAnimationModule.CameraPathFrame;
import org.scaffoldeditor.worldexport.util.RenderUtils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.replay.ReplayHandler;
import com.replaymod.replay.events.ReplayClosedCallback;
import com.replaymod.replay.events.ReplayOpenedCallback;
import com.replaymod.simplepathing.ReplayModSimplePathing;
import com.replaymod.simplepathing.Setting;
import com.replaymod.simplepathing.gui.GuiPathing;
import com.replaymod.simplepathing.preview.PathPreviewRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Renders animated cameras and their paths.
 * @see PathPreviewRenderer
 */
public class CameraPathRenderer extends EventRegistrations {
    private static final Minecraft client = Minecraft.getInstance();
    private final CameraAnimationModule module;
    private final ReplayModSimplePathing simplePathing;
    private final CameraModelRenderer modelRenderer = new CameraModelRenderer();

    // private static final float PATH_STEP
    private static final float MAX_DISTANCE_SQUARED = 6;

    private ReplayHandler replayHandler;

    public CameraPathRenderer(CameraAnimationModule module, ReplayModSimplePathing simplePathing) {
        this.module = module;
        this.simplePathing = simplePathing;

        on(ReplayOpenedCallback.EVENT, replayHandler -> {
            this.replayHandler = replayHandler;
        });

        on(ReplayClosedCallback.EVENT, replayHandler -> {
            this.replayHandler = null;
        });
    }

    public CameraModelRenderer getModelRenderer() {
        return modelRenderer;
    }

    public ReplayHandler getReplayHandler() {
        return replayHandler;
    }

    public synchronized void setReplayHandler(ReplayHandler replayHandler) {
        this.replayHandler = replayHandler;
    }

    /**
     * Render all camera paths into the world.
     * @param event Render level stage event from Neoforge.
     */
    public synchronized void render(RenderLevelStageEvent event) {
        if (!shouldRender()) return;

        Vec3 cameraPos = event.getCamera().getPosition();
        PoseStack matrices = event.getPoseStack();
        matrices.pushPose();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        MultiBufferSource.BufferSource bufferSource = client.renderBuffers().bufferSource();

        Map<Integer, AbstractCameraAnimation> animations = module.getAnimations(replayHandler.getReplayFile());
        for (AbstractCameraAnimation animation : animations.values()) {
            renderAnimPath(matrices, bufferSource, animation);
        }
        
        // Apparently Johni doesn't understand frontend / backend separation.
        // I should *not* have to go into UI code to get this.
        GuiPathing guiPathing = simplePathing.getGuiPathing();
        if (guiPathing == null) {
            matrices.popPose();
            return;
        }
        double time = guiPathing.timeline.getCursorPosition() / 1000d;

        for (AbstractCameraAnimation animation : animations.values()) {
            Optional<AnimatedCameraEntity> ent = module.optCameraEntity(client.level, animation.getId());
            // Don't render if we're currently viewing from this camera.
            if (ent.isPresent() && ent.get().equals(client.cameraEntity)) continue;
            Vec3 vecPos = animation.getPositionAt(time);
            BlockPos pos = BlockPos.containing(vecPos.x, vecPos.y, vecPos.z);

            modelRenderer.render(animation, time, matrices, bufferSource, getLight(pos, client.level));
        }

        matrices.popPose();
    } 
    
    private int getLight(BlockPos pos, BlockAndTintGetter world) {
        return LightTexture.pack(world.getBrightness(LightLayer.BLOCK, pos),
                world.getBrightness(LightLayer.SKY, pos));
    }

    private void renderAnimPath(PoseStack matrices, MultiBufferSource vertexConsumers, AbstractCameraAnimation animation) {
        int color = RenderUtils.stripAlpha(RenderUtils.colorToARGB(animation.getColor()));
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.lines());

        Vector3f prevPos = null;
        Vector3f pos;
        Vector3f delta = new Vector3f();
        for (CameraPathFrame frame : animation) {
            pos = frame.pos().toVector3f();
            if (prevPos != null && pos.sub(prevPos, delta).lengthSquared() <= MAX_DISTANCE_SQUARED) {
                drawLine(matrices, consumer, prevPos, pos, color);
            }
            prevPos = pos;
        }
        
    }

    private void drawLine(PoseStack matrices, VertexConsumer consumer, Vector3fc pos1, Vector3fc pos2, int color) {
        drawLine(matrices.last(), consumer,
                pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z(), color);
    }

    private void drawLine(PoseStack.Pose matrix, VertexConsumer consumer, float x1, float y1, float z1,
            float x2, float y2, float z2, int color) {
        
        Matrix4f model = matrix.pose();
        Matrix3f normal = matrix.normal();

        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;

        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        consumer.addVertex(model, x1, y1, z1)
                .setColor(color)
                .setNormal(normal, dx / len, dy / len, dz / len);

        consumer.addVertex(model, x2, y2, z2)
                .setColor(color)
                .setNormal(normal, dx / len, dy / len, dz / len);
    }

    protected boolean shouldRender() {
        return (replayHandler != null
                && !client.options.hideGui
                && simplePathing.getCore().getSettingsRegistry().get(Setting.PATH_PREVIEW)
                && replayHandler.getReplaySender().isAsyncMode());
    }
}
