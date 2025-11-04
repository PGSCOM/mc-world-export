package org.scaffoldeditor.worldexport;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Called on the client when a block has been updated.
 */
public interface ClientBlockPlaceCallback {
    SimpleEvent<ClientBlockPlaceCallback> EVENT = new SimpleEvent<>();
    
    void place(BlockPos pos, @Nullable BlockState oldState, BlockState state, Level world);
    
    class SimpleEvent<T> {
        private final List<T> listeners = new ArrayList<>();
        
        public void register(T listener) {
            listeners.add(listener);
        }
        
        public List<T> getListeners() {
            return listeners;
        }
    }
}
