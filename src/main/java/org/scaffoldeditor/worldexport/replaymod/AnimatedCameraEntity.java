package org.scaffoldeditor.worldexport.replaymod;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.worldexport.replaymod.camera_animations.Rotation;
import org.scaffoldeditor.worldexport.replaymod.util.FovProvider;
import org.scaffoldeditor.worldexport.replaymod.util.RollProvider;

import com.replaymod.replaystudio.util.Location;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public class AnimatedCameraEntity extends Entity implements RollProvider, FovProvider {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("worldexport", "camera");

    public float roll;
    public double fov;

    private int color = 0xFFFFFFFF;

    public AnimatedCameraEntity(EntityType<? extends AnimatedCameraEntity> type, Level world) {
        super(type, world);
        if (!world.isClientSide) {
            throw new IllegalStateException("Animated camera entity should never be spawned on the server!");
        }
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {        
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag var1) {        
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag var1) {        
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new IllegalStateException("This entity is client-side only.");
    }

    @Override
    public ClientLevel level() {
        return (ClientLevel) super.level();
    }

    @Override
    public final float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public final double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Moves the camera by the specified delta.
     * @param x Delta in X direction
     * @param y Delta in Y direction
     * @param z Delta in Z direction
     */
    public void moveCamera(double x, double y, double z) {
        setCameraPosition(this.getX() + x, this.getY() + y, this.getZ() + z);
    }
    
    /**
     * Set the camera position.
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public void setCameraPosition(double x, double y, double z) {
        this.xOld = this.xo = x;
        this.yOld = this.yo = y;
        this.zOld = this.zo = z;
        this.setPos(x, y, z);
    }

    /**
     * Sets the camera rotation.
     * @param yaw Yaw in degrees
     * @param pitch Pitch in degrees
     * @param roll Roll in degrees
     */
    public void setCameraRotation(float yaw, float pitch, float roll) {
        if (yaw != yaw || pitch != pitch) {
            LogManager.getLogger().error("Cannot set camera to NaN rotation. Yaw: {}, Pitch: {}", yaw, pitch);
            return;
        }

        this.yRotO = yaw;
        this.xRotO = pitch;
        setXRot(pitch);
        setYRot(yaw);
        setRoll(roll);
    }

    /**
     * Sets the camera rotation.
     * @param rotation Abstracted rotation object.
     */
    public void setCameraRotation(Rotation rotation) {
        float pitch = (float) Math.toDegrees(rotation.pitch());
        float yaw = (float) Math.toDegrees(rotation.yaw());
        float roll = (float) Math.toDegrees(rotation.roll());

        // TODO: verify this isn't fixing a mistake in the Blender addon
        yaw = -Mth.wrapDegrees(yaw + 180);
        pitch = 90 - pitch; // Why is Minecraft's rotation system so weird?

        setCameraRotation(yaw, pitch, roll);
    }

    public void setCameraPosRot(Location loc) {
        setCameraRotation(loc.getPitch(), loc.getYaw(), roll);
        setCameraPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false; // We are in full control of ourselves
    }

    @Override
    protected void spawnSprintParticles() {
        // We do not produce any particles, we are a camera
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
    
    @Override
    public boolean isPickable() {
        return true; // Allows player to spectate
    }

}
