package org.scaffoldeditor.worldexport.mixins;

import org.scaffoldeditor.worldexport.ClientBlockPlaceCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@Mixin(ClientLevel.class)
@OnlyIn(Dist.CLIENT)
public abstract class ClientWorldMixin {

    @Inject(method = "sendBlockUpdated", at = @At("RETURN"))
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        for (ClientBlockPlaceCallback listener : ClientBlockPlaceCallback.EVENT.getListeners()) {
            listener.place(pos, oldState, newState, (Level)(Object) this);
        }
    }
}
