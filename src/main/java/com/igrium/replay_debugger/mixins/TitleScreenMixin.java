package com.igrium.replay_debugger.mixins;

import java.awt.HeadlessException;

import com.igrium.replay_debugger.ReplayDebugger;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLLoader;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo ci) {
        if (!FMLLoader.isProduction()) {

            Button button = Button.builder(Component.literal("Debug Replays"), (b) -> {
                try {
                    ReplayDebugger instance = new ReplayDebugger();
                    instance.launch();
                } catch (HeadlessException e) {
                    LogManager.getLogger(ReplayDebugger.class)
                            .error("Unable to launch debugger in headless environment.");
                }

            }).pos(width - 98, 0)
              .size(98, 20).build();
            
            addRenderableWidget(button);

        }
    }
}
