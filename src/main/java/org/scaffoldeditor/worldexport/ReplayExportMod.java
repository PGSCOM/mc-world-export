package org.scaffoldeditor.worldexport;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scaffoldeditor.worldexport.replaymod.AnimatedCameraEntity;
import org.scaffoldeditor.worldexport.replaymod.ReplayModHooks;
import org.scaffoldeditor.worldexport.replaymod.camera_animations.CameraAnimationModule;
// import org.scaffoldeditor.worldexport.replaymod.render.CameraEntityRenderer;
// import org.scaffoldeditor.worldexport.replaymod.render.CameraPathRenderer;
// import org.scaffoldeditor.worldexport.replay.model_adapters.ReplayModels;
// import org.scaffoldeditor.worldexport.world_snapshot.WorldSnapshotManager;
// import com.replaymod.simplepathing.ReplayModSimplePathing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(value = "worldexport", dist = Dist.CLIENT)
public class ReplayExportMod {

    public static final Logger LOGGER = LogManager.getLogger("worldexport");
    private static ReplayExportMod instance;

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "worldexport");

    public static final DeferredHolder<EntityType<?>, EntityType<AnimatedCameraEntity>> ANIMATED_CAMERA = 
        ENTITY_TYPES.register("animated_camera", () -> 
            EntityType.Builder.of(AnimatedCameraEntity::new, MobCategory.MISC)
                .sized(0.75f, 0.75f)
                .clientTrackingRange(10)
                .updateInterval(20)
                .build("animated_camera"));
    
    public static final ResourceLocation CAMERA_MODEL_LAYER = ResourceLocation.fromNamespaceAndPath("worldexport", "camera");

    public static ReplayExportMod getInstance() {
        return instance;
    }

    private final Minecraft client = Minecraft.getInstance();

    private Set<ClientBlockPlaceCallback> blockUpdateListeners = new HashSet<>();
    private final CameraAnimationModule cameraAnimationsModule = new CameraAnimationModule();
    // private CameraPathRenderer cameraPathRenderer;

    private String modVersion;
    
    // private WorldSnapshotManager worldSnapshotManager;
    
    public String getModVersion() {
        return modVersion;
    }

    public void onBlockUpdated(ClientBlockPlaceCallback listener) {
        blockUpdateListeners.add(listener);
    }

    public boolean removeOnBlockUpdated(ClientBlockPlaceCallback listener) {
        return blockUpdateListeners.remove(listener);
    }

    // public WorldSnapshotManager getWorldSnapshotManager() {
    //     return worldSnapshotManager;
    // }

    public ReplayExportMod(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;
        this.modVersion = modContainer.getModInfo().getVersion().toString();

        ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(this::onClientSetup);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientBlockPlaceCallback.EVENT.register((pos, oldState, state, world) -> {
                blockUpdateListeners.forEach(listener -> listener.place(pos, oldState, state, world));
            });

            // worldSnapshotManager = new WorldSnapshotManager();
            // ReplayModels.registerDefaults();
            // EntityRenderers.register(ANIMATED_CAMERA.get(), CameraEntityRenderer::new);

            ReplayModHooks.onReplayModInit(replayMod -> {
                cameraAnimationsModule.register();
                cameraAnimationsModule.registerKeyBindings(replayMod);
                // cameraPathRenderer = new CameraPathRenderer(cameraAnimationsModule, ReplayModSimplePathing.instance);
                // cameraPathRenderer.register();
            });

            // Register render events
            // NeoForge.EVENT_BUS.addListener(this::onRenderLevelStage);
        });
    }

    // @SubscribeEvent
    // public void onRenderLevelStage(RenderLevelStageEvent event) {
    //     if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
    //         if (cameraPathRenderer != null) {
    //             cameraPathRenderer.render(event);
    //         }
    //     }
    //     
    //     // Allows you to spectate camera entity in replay editor.
    //     if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
    //         if (client.hitResult != null && client.hitResult.getType() == HitResult.Type.ENTITY) {
    //             Entity ent = ((EntityHitResult) client.hitResult).getEntity();
    //             if (ent instanceof AnimatedCameraEntity) {
    //                 client.crosshairPickEntity = ent;
    //             }
    //         }
    //     }
    // }

    public CameraAnimationModule getCameraAnimationsModule() {
        return cameraAnimationsModule;
    }
    
    // public CameraPathRenderer getCameraPathRenderer() {
    //     return cameraPathRenderer;
    // }
}
