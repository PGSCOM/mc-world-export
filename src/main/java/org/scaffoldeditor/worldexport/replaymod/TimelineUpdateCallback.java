package org.scaffoldeditor.worldexport.replaymod;

import com.replaymod.replaystudio.pathing.path.Timeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Called whenever the playhead on the replay timeline updates.
 */
public interface TimelineUpdateCallback {
    SimpleEvent<TimelineUpdateCallback> EVENT = new SimpleEvent<>();

    /**
     * Called whenever the playhead on the replay timeline moves.
     * @param timeline The timeline instance.
     * @param replayHandler The current replay handler.
     * @param time The new time.
     */
    void onUpdate(Timeline timeline, Object replayHandler, long time);
    
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
